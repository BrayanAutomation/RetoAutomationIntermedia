# Reproducibilidad con Docker

Esta guía cubre el criterio de entrega **"Reproducibilidad con Docker"** del
reto: cómo ejecutar **esta suite de pruebas** (no el sistema bajo prueba) en
un entorno local usando Docker, sin instalar Java, Gradle ni Playwright en
la máquina anfitriona.

Se agregan dos archivos nuevos a este repositorio, sin tocar nada del reto:

- **`Dockerfile`** — imagen de ejecución basada en `mcr.microsoft.com/playwright/java:v1.58.0-noble`
  (JDK + navegadores Playwright preinstalados, versión alineada exactamente
  con la librería `com.microsoft.playwright:playwright:1.58.0` que resuelve
  `ui-tests/build.gradle`).
- **`docker-compose.tests.yml`** — compose *secundario* que define dos
  servicios (`api-tests`, `ui-tests`) para correr cada módulo de forma
  aislada y reproducible.

## Prerrequisito: el sistema bajo prueba debe estar corriendo

Estos contenedores **no** levantan el API/Frontend/Postgres del reto — se
conectan a la red Docker que ya crea `automation-testing-artefacts` cuando
lo levantas normalmente:

```bash
cd ../automation-testing-artefacts
docker-compose up --build -d
curl http://localhost:8080/health   # debe responder {"status":"UP",...}
```

## Ejecutar la suite completa en Docker

Desde la raíz de **este** repositorio (`RetoAutomationIntermedia`):

```bash
docker compose -f docker-compose.tests.yml up --build --abort-on-container-exit
```

Esto construye la imagen una vez (compartida por ambos servicios) y corre
`api-tests` y `ui-tests` en paralelo, cada uno dentro de su propio
contenedor, conectados a la red del reto por su nombre de servicio interno
(`http://api:8080`, `http://frontend`) — **sin depender de `localhost`**,
lo cual es justamente la diferencia entre correr la suite en el host y
correrla dentro de Docker.

Para correr un solo módulo:

```bash
docker compose -f docker-compose.tests.yml run --rm api-tests
docker compose -f docker-compose.tests.yml run --rm ui-tests
```

Los reportes Serenity quedan en el host igual que en una corrida local
(el código fuente se monta como volumen, no se copia dentro de la imagen):

```
api-tests/build/reports/tests/test/index.html
ui-tests/build/reports/tests/test/index.html
```

## Si tu carpeta del reto no se llama "automation-testing-artefacts"

Docker Compose nombra la red por defecto como `<nombre_de_carpeta>_default`.
Si tu checkout tiene otro nombre, verifica la red real y sobreescribe la
variable de entorno antes de correr:

```bash
docker network ls | grep default
SUT_NETWORK_NAME=<nombre_real_de_la_red> docker compose -f docker-compose.tests.yml up --build
```

(o edita el valor por defecto en el archivo `.env` de este repositorio).

## Limpieza

```bash
docker compose -f docker-compose.tests.yml down -v   # contenedores + caché de Gradle
cd ../automation-testing-artefacts && docker-compose down -v   # sistema bajo prueba
```

## Notas de diseño

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
  JDK 17 y todas las dependencias; las siguientes son notablemente más
  rápidas.
- Se reutiliza la misma imagen para `api-tests` y `ui-tests` (una sola
  build) aunque `api-tests` no necesite navegadores — simplifica el
  mantenimiento a cambio de una imagen algo más pesada; se consideró
  aceptable dado que no es la imagen de producción del sistema, solo la
  de ejecución de pruebas.
