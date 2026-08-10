# Reproducibilidad con Docker

Esta guía cubre el criterio de entrega **"Reproducibilidad con Docker"** del
reto: cómo ejecutar **esta suite de pruebas** (no el sistema bajo prueba) en
un entorno local usando Docker, sin instalar Java, Gradle ni Playwright en
la máquina anfitriona.

Se agregan a este repositorio, sin tocar nada del reto:

- **`Dockerfile`** — imagen de ejecución basada en `mcr.microsoft.com/playwright/java:v1.58.0-noble`
  (JDK 21 + navegadores Playwright preinstalados, versión de navegador
  alineada exactamente con la librería `com.microsoft.playwright:playwright:1.58.0`
  que resuelve `ui-tests/build.gradle`).
- **`docker-compose.tests.yml`** — compose *secundario* que define dos
  servicios (`api-tests`, `ui-tests`) para correr cada módulo de forma
  reproducible.
- **`.env`** — nombre de la red del sistema bajo prueba, configurable.

> Todo lo de esta guía fue verificado ejecutándolo de verdad contra el
> stack real (no es documentación especulativa) — la sección "Problemas
> reales encontrados y cómo se resolvieron" documenta 3 fallos genuinos
> descubiertos en el proceso, no solo el resultado final feliz.

## Prerrequisito: el sistema bajo prueba debe estar corriendo

```bash
cd ../automation-testing-artefacts
docker-compose up --build -d
curl http://localhost:8080/health   # debe responder {"status":"UP",...}
```

## Ejecutar la suite completa en Docker

Desde la raíz de **este** repositorio (`RetoAutomationIntermedia`):

```bash
docker compose -f docker-compose.tests.yml build
docker compose -f docker-compose.tests.yml run --rm api-tests
docker compose -f docker-compose.tests.yml run --rm ui-tests
```

Se corren **secuencialmente con `run --rm`**, no con `up --build --abort-on-container-exit`:
esta suite tiene escenarios que **fallan a propósito** (`@KnownDefect`, ver
el `REPORT_TEMPLATE.md` del reto) para documentar bugs reales del sistema.
`--abort-on-container-exit` mata **todos** los contenedores en cuanto el
primero termina (sin importar si fue éxito o fallo) — con dos servicios
independientes eso puede cortar `ui-tests` a mitad de camino si `api-tests`
termina primero con código de salida ≠ 0. Correrlos uno tras otro evita ese
problema, y como ambos contenedores comparten el mismo volumen montado del
código fuente, generan un único **reporte Serenity combinado**.

Los reportes quedan en el host igual que en una corrida local (el código
fuente se monta como volumen, no se copia dentro de la imagen):

```
target/site/serenity/index.html                 # reporte Serenity BDD combinado (API + UI)
api-tests/build/reports/tests/test/index.html    # reporte JUnit plano solo de api-tests
ui-tests/build/reports/tests/test/index.html     # reporte JUnit plano solo de ui-tests
```

## Si tu carpeta del reto no se llama "automation-testing-artefacts"

Docker Compose nombra la red por defecto como `<nombre_de_carpeta>_default`.
Si tu checkout tiene otro nombre, verifica la red real y sobreescribe la
variable de entorno antes de correr:

```bash
docker network ls | grep default
SUT_NETWORK_NAME=<nombre_real_de_la_red> docker compose -f docker-compose.tests.yml run --rm api-tests
```

(o edita el valor por defecto en el archivo `.env` de este repositorio).
Esto solo afecta a `api-tests` — `ui-tests` no usa esa red (ver más abajo).

## Limpieza

```bash
docker compose -f docker-compose.tests.yml down -v   # contenedores + caché de Gradle
cd ../automation-testing-artefacts && docker-compose down -v   # sistema bajo prueba
```

## Problemas reales encontrados y cómo se resolvieron

Estos tres problemas **no se detectan compilando ni leyendo el YAML** —
solo corriendo la suite de verdad contra el stack real. Se documentan para
que quede claro qué hace cada configuración y por qué existe.

### 1. Gradle 9 no auto-descarga el JDK del toolchain sin un resolver

El proyecto exige JDK 17 (`build.gradle`), pero la imagen de Playwright trae
JDK 21. Gradle 9 dejó de auto-provisionar toolchains "por las suyas" — hace
falta declarar explícitamente un resolver. Por eso `settings.gradle` incluye:

```gradle
plugins {
    id 'org.gradle.toolchains.foojay-resolver-convention' version '1.0.0'
}
```

Sin esto, el build falla con `Cannot find a Java installation... Toolchain
download repositories have not been configured.` la primera vez que corre
dentro del contenedor.

### 2. Las propiedades `-D` de la CLI no llegan al JVM forkeado de `test`

`./gradlew test -Dapi.base.url=http://api:8080` (o `-Dui.base.url`,
`-Dplaywright.headless`) **solo** setea esa propiedad en el JVM de Gradle,
no en el JVM forkeado donde corre el `Test` task — son procesos separados.
Localmente esto pasaba desapercibido porque el valor por defecto del código
(`http://localhost:8080`) coincidía con lo que se quería usar; en Docker,
donde el valor necesario es distinto (`http://api:8080`), el código seguía
usando su default y los contenedores fallaban con `ConnectException`
apuntando a sí mismos.

Solución en `build.gradle` (bloque `subprojects { test { ... } }`): reenviar
explícitamente una lista blanca de propiedades del JVM de Gradle al JVM del
test vía `systemProperty` si fueron pasadas por CLI.

### 3. El frontend del reto tiene `http://localhost:8080` horneado en el bundle JS

Este es el más sutil. El frontend (`docker-compose.yml` del reto) se
construye con `VITE_API_URL=http://localhost:8080`, y Vite **hornea** esa
URL dentro del JavaScript compilado en tiempo de build — no es una
variable de entorno que se pueda cambiar en tiempo de ejecución del
contenedor `qa-frontend`.

Eso significa que el propio React, corriendo en el navegador, hace
`fetch("http://localhost:8080/...")`. Cuando el navegador de Playwright
corre en **nuestro** contenedor `ui-tests` conectado a una red bridge
propia (`sut-network`, igual que `api-tests`), `localhost` dentro de ese
navegador apunta al contenedor `ui-tests` mismo — no al API real. Resultado
observado: la página cargaba bien (`data-testid="users-table"` visible), la
navegación funcionaba, pero **la tabla nunca recibía datos reales** sin
importar cuánto se esperara (se descartó que fuera un problema de timing:
subir el timeout de espera de 6s a 15s no cambió el resultado).

Solución: el servicio `ui-tests` usa `network_mode: host` en vez de la red
bridge del reto, y apunta a `http://localhost:3000` (no `http://frontend`).
Con red de host, el `localhost` que ve el navegador es el mismo `localhost`
del sistema anfitrión — el mismo que ya publica los puertos 3000/8080 del
reto — exactamente como en una corrida local nativa. `api-tests` **no**
tiene este problema (no hay navegador ni JS de por medio: RestAssured llama
directo desde el JVM), así que se queda con la red bridge normal.

> Nota de portabilidad: `network_mode: host` funcionó sin configuración
> adicional en este entorno (Docker Desktop 29.1.3 sobre WSL2). En
> instalaciones más antiguas de Docker Desktop puede requerir habilitar
> "Host networking" en Settings → Resources → Network.

## Notas de diseño adicionales

- **`shm_size: 1gb`** en `ui-tests`: el `/dev/shm` por defecto de Docker
  (64MB) es insuficiente para Chromium y provoca crashes intermitentes a
  mitad de la corrida; se amplía en vez de usar `--disable-dev-shm-usage`
  (ese flag degrada el rendimiento del renderizador en vez de arreglar la
  causa).
- **`-Dplaywright.headless=true`** se fuerza explícitamente en el comando
  del servicio `ui-tests`, independientemente del valor por defecto que
  tenga el código en `SetUp.java` — un contenedor no tiene entorno gráfico,
  así que headless no es opcional aquí.
- El volumen nombrado `gradle-cache` persiste `~/.gradle` entre corridas:
  la primera ejecución descarga la distribución de Gradle, el toolchain
  JDK 17 y todas las dependencias (varios minutos); las siguientes son
  notablemente más rápidas (segundos).
- Se reutiliza la misma imagen para `api-tests` y `ui-tests` (una sola
  build) aunque `api-tests` no necesite navegadores — simplifica el
  mantenimiento a cambio de una imagen algo más pesada; se consideró
  aceptable dado que no es la imagen de producción del sistema, solo la
  de ejecución de pruebas.
- El timeout de espera de `UserIsListed` (que confirma que un usuario
  recién creado aparece en la tabla) se subió de 6s a 15s: un contenedor
  sin aceleración de hardware para Chromium es consistentemente más lento
  que una corrida nativa, y 6s alcanzaba siempre en Windows nativo pero no
  en Docker.

## Resultado verificado (última corrida real, contenedores)

- `api-tests`: 24 escenarios descubiertos (con el runner de regresión +
  los 5 runners por feature ejecutando lo mismo dos veces, ver más abajo),
  2 fallos — ambos el defecto documentado `BUG-API-002`.
- `ui-tests`: 18 escenarios descubiertos, 2 fallos — ambos el hallazgo
  documentado `BUG-UI-002` (desborde horizontal en viewport móvil/tablet).
- Reporte Serenity combinado generado correctamente en
  `target/site/serenity/index.html` con escenarios de ambos módulos.

> Nota aparte (no es un problema de Docker): como ahora existen tanto el
> runner de regresión (`RegresionApiTest`/`RegresionUiTest`, filtrado a
> `@HappyPath`) como un runner dedicado por cada `.feature`, cada escenario
> se ejecuta más de una vez por corrida completa — es esperado dado que se
> pidieron ambos tipos de runner, pero alarga el tiempo total.
