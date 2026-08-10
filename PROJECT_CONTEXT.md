# Contexto completo del proyecto — RetoAutomationIntermedia

> Este documento existe para que **Claude** (o cualquier persona que retome
> el proyecto) tenga el contexto completo de qué se hizo, por qué, en qué
> orden, y qué problemas reales se encontraron y cómo se resolvieron —
> sin tener que releer todo el historial de commits o la conversación
> original. Está escrito cronológicamente, por fases, con los hallazgos
> técnicos no obvios destacados aparte al final.

---

## 0. Origen y objetivo

Este repositorio es la solución a un reto técnico de QA Automation. El
punto de partida fue un archivo `Context.md` (fuera de este repo, en
`C:\Users\Brayan7\Context.md`) con instrucciones estrictas: analizar el
reto y dos repos de referencia ANTES de escribir código, construir una
solución Screenplay Pattern completa (UI con Playwright, API con
Screenplay REST + RestAssured), y crear 4 skills de Claude Code para
poder extender la suite en el futuro sin perder las convenciones.

**Tres repositorios relacionados** (todos hermanos en disco, bajo
`Documentos\Automatizacion\reto intermedia\`):

| Repo | Rol | Ubicación / URL |
|---|---|---|
| `automation-testing-artefacts` | El reto en sí (sistema bajo prueba: React + Spring Boot + Postgres, orquestado con `docker-compose.yml`) | Local + [github.com/intermedia-group/automation-testing-artefacts](https://github.com/intermedia-group/automation-testing-artefacts) (público, no tocado salvo `REPORT_TEMPLATE.md`) |
| **`RetoAutomationIntermedia`** | **Este repo** — la solución de automatización | Local + [github.com/BrayanAutomation/RetoAutomationIntermedia](https://github.com/BrayanAutomation/RetoAutomationIntermedia) (público, del candidato) |
| `AutomarizacionWebLaTinka` / `deuna-aut-py-onboarding-api` | Repos de referencia (solo lectura, nunca modificados) usados para extraer convenciones Screenplay reales de proyectos productivos | Local, fuera de alcance de este repo |

**Candidato:** Brayan Granado (`brayan.sus7@gmail.com`).

---

## 1. Fase de análisis (antes de escribir código)

Se exploraron a fondo 4 repos antes de tocar una sola línea:
1. `automation-testing-artefacts` → extrajo `docs/acceptance_criteria.md`
   (11 criterios `CA-*`), identificó los bugs intencionales exactos en el
   código fuente (no solo la descripción), y un detalle no documentado:
   el historial de git del reto muestra que `CA-UI-04` fue **deliberadamente
   despojado** de su etiqueta de advertencia de ambigüedad (commit
   `"integrate ambiguous criteria naturally without explicitly labeling it
   as ambiguous"`) — es una trampa intencional para evaluar si el QA la
   detecta solo.
2. `AutomarizacionWebLaTinka` → patrones UI reales (Serenity 5.3.4 +
   Screenplay-Playwright, estructura de paquetes, `SetUp.java` con
   `Cast.whereEveryoneCan(...)`, runners JUnit5 Platform Suite).
3. `deuna-aut-py-onboarding-api` → patrones API reales (Serenity 4.0.21 +
   Screenplay-REST, `Api.java` genérico, `Verificar.java` con
   `ResponseConsequence`, aunque con anti-patrones deliberadamente
   evitados en este proyecto — ver sección 3).
4. `RetoAutomationIntermedia` (este repo) tal como estaba entonces: un
   esqueleto Gradle multi-módulo casi vacío con clases stub apuntando a
   `example.com`.

Resultado documentado en `docs/context/ui-reference-context.md` y
`docs/context/api-reference-context.md` — **fuente de verdad de
convenciones**, se citan desde los 4 skills.

---

## 2. Arquitectura construida

**Gradle multi-módulo**: `api-tests` + `ui-tests`, cada uno con
`com.reto.automation.{api|ui}` y las capas Screenplay estándar (`tasks`,
`interactions`, `questions`, `models`, `utils`, y `userinterfaces` solo en
UI). Detalle completo, con tablas y diagramas Mermaid, en
[`README.md`](./README.md) — no se duplica aquí.

**Reglas aplicadas de forma consistente:**
- 1 endpoint = 1 Task (API).
- Locators SIEMPRE `data-testid`, nunca `id`/`htmlFor` (el reto los hace
  dinámicos a propósito, `CA-UI-02`).
- Step definitions = solo orquestación, cero lógica de negocio.
- Questions centralizadas y reutilizadas (`TheResponse` en API cubre casi
  todas las validaciones HTTP).
- Los escenarios que cubren bugs intencionales (`CA-API-03/04/05`,
  `CA-UI-02/04`) validan el comportamiento **correcto** de negocio, no el
  buggy — por eso fallan a propósito contra el sistema real. Eso es la
  evidencia del hallazgo, no un error de la automatización.

**21 escenarios Gherkin** (12 API + 9 UI) en 9 `.feature`, tageados con
`@CA-*` (trazabilidad) y `@HappyPath`/`@Negative`/`@Edge`/`@DataVariation`
(ISTQB). Detalle en `README.md` sección "Escenarios y trazabilidad".

**Runners** (todos JUnit5 Platform Suite + `@ConfigurationParameter` con
`GLUE_PROPERTY_NAME` + `PLUGIN_PROPERTY_NAME=net.serenitybdd.cucumber.core.plugin.SerenityReporter`):
- `RegresionApiTest` / `RegresionUiTest`: corren todo el módulo. El
  **usuario** les agregó después `@ConfigurationParameter(FILTER_TAGS_PROPERTY_NAME,
  "@HappyPath")` — quedan fijos a HappyPath, no se debe tocar esa
  intención sin que lo pida explícitamente.
- Un runner dedicado por cada `.feature` (`HealthTest`, `UsersCrudTest`,
  `AppLoadTest`, etc. — 5 en API, 4 en UI) — sin tag fijo, corren su
  feature completa.
- `CiApiTest` / `CiUiTest`: agregados específicamente para el pipeline de
  CI y para regresiones "completas y limpias" ad-hoc — sin tag fijo,
  cubren TODA la carpeta de features de su módulo, respetan
  `-Dcucumber.filter.tags` si se pasa. **Estos son los que hay que usar
  cuando se pida "una regresión completa" o "el reporte completo", para
  no duplicar escenarios con el runner de regresión ni con los
  dedicados.**

**4 skills de Claude Code** en `.claude/skills/` (`gherkin-scenario-writer`,
`istqb-test-analyst`, `ui-screenplay-implementer`, `api-screenplay-implementer`) —
encadenados, cada uno cita `docs/context/*.md` como fuente de patrones.
Detalle en `README.md`.

---

## 3. Bugs confirmados del sistema bajo prueba

Documentados con severidad y evidencia completa en [`REPORT.md`](./REPORT.md)
sección 3 (y en `automation-testing-artefacts/REPORT_TEMPLATE.md`, la
copia "oficial" del reto). Resumen:

| ID | Severidad | Resumen |
|---|---|---|
| `BUG-API-001` | Alta | `GET /api/users` falla ~25% (500), intermitente |
| `BUG-API-PERF` | Media | `POST /api/users` demora 2.5s sistemáticamente |
| `BUG-API-002` | Alta | `PUT /api/users/{id}` no valida datos inválidos |
| `BUG-UI-001` | Media | `id`/`htmlFor` dinámicos en el formulario |
| `BUG-UI-002` | Media | Tabla sin diseño responsivo (desborde en 375px) — **descubierto por nosotros**, no estaba en la descripción original del criterio ambiguo `CA-UI-04` |
| `OBS-UI-01` | Baja (no confirmado) | `.overlay-bug-banner` en CSS, nunca referenciada en JSX — código muerto/no reproducible |

---

## 4. Bugs propios encontrados y corregidos durante la verificación

**Muy importante para el futuro**: casi nada de esto se detecta leyendo
código o compilando — todos se encontraron *ejecutando de verdad* contra
el sistema real. La política seguida en todo el proyecto fue "no declarar
nada terminado sin correrlo". Lista completa, en orden cronológico:

1. **`UndefinedStepException` en todos los escenarios** — causa real:
   `compileJava`/`compileTestJava` no forzaban UTF-8, y el compilador leía
   los `.java` con encoding por defecto de Windows (Cp1252), corrompiendo
   los literales de los pasos `@Given("...está...")`. Fix:
   `compileJava.options.encoding = 'UTF-8'` en `build.gradle`.

2. **`NullPointerException: No BaseStepListener has been registered`** —
   con Cucumber + JUnit5 Platform Suite (no el runner JUnit4 clásico de
   Serenity), hace falta registrar el plugin de Serenity explícitamente:
   `@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value =
   "net.serenitybdd.cucumber.core.plugin.SerenityReporter")` en cada
   runner. Sin esto, Screenplay no tiene dónde reportar y explota.

3. **`cucumber-junit-platform-engine` en versión distinta a
   `cucumber-core`** (7.18.0 declarado vs 7.34.2 resuelto vía
   `serenity-cucumber`) — causaba fallos sutiles de descubrimiento de
   steps. Fix: alinear versiones explícitamente.

4. **Espera insuficiente tras crear un usuario (UI)** — `POST /api/users`
   tiene latencia intencional de 2.5s; el check de "¿aparece en la
   tabla?" no esperaba, era un `.count()` instantáneo. Fix:
   `page.waitForSelector(..., timeout)` en `UserIsListed`. El timeout se
   subió de 6s a **15s** más adelante al verificar en Docker (ver #7).

5. **Gradle 9 no auto-descarga el JDK del toolchain** sin resolver
   explícito. Fix: `org.gradle.toolchains.foojay-resolver-convention` en
   `settings.gradle`. Solo se manifestó dentro de Docker (la imagen trae
   JDK 21, el proyecto pide 17); en local ya había JDK 17 instalado y
   nunca se notó.

6. **Las propiedades `-D` de la CLI de Gradle NO llegan solas al JVM
   forkeado del task `test`** — son procesos separados. Este bug existía
   desde el principio de forma silenciosa: `-Dapi.base.url=...` nunca
   tuvo efecto real, pero pasaba desapercibido porque el default del
   código (`http://localhost:8080`) coincidía con lo que se quería usar en
   local. Se destapó recién en Docker, donde hacía falta un valor
   distinto (`http://api:8080`) y los contenedores fallaban con
   `ConnectException` apuntando a sí mismos. Fix: reenvío explícito en
   `build.gradle` (`forwardedProperties` — lista blanca: `api.base.url`,
   `ui.base.url`, `playwright.headless`, `playwright.browser`,
   `cucumber.filter.tags`).

7. **El frontend del reto tiene `http://localhost:8080` horneado en el
   bundle JS de Vite** (`VITE_API_URL` es build-time, no runtime). Con el
   navegador de Playwright corriendo dentro de un contenedor con red
   bridge propia, el fetch interno del propio React nunca llegaba al API
   real — la página cargaba pero la tabla nunca recibía datos, sin
   importar cuánto se esperara (se descartó timing subiendo el wait a
   15s antes de encontrar la causa real). Fix: el servicio `ui-tests` de
   `docker-compose.tests.yml` usa `network_mode: host` en vez de la red
   bridge del reto, apuntando a `http://localhost:3000` — así el
   navegador comparte el `localhost` real del host, igual que en una
   corrida nativa. `api-tests` no tiene este problema (no hay navegador
   de por medio).

8. **`aggregate` marcado UP-TO-DATE incorrectamente entre invocaciones
   separadas de Gradle** — el plugin de Serenity no declara bien como
   input la carpeta compartida de evidencia (redirigida ahí vía
   `-Dserenity.outputDirectory`). Si `api-tests` y `ui-tests` corren en
   comandos `./gradlew` SEPARADOS, la segunda invocación puede saltarse
   la regeneración del `index.html`, dejando el reporte con datos viejos
   **sin ningún error visible** (se detectó comparando el timestamp del
   `index.html` contra el de los `.json` de evidencia más recientes). Fix:
   `tasks.named('aggregate') { outputs.upToDateWhen { false } }` en el
   `build.gradle` raíz — fuerza que siempre recompile.

**Lección operativa para el futuro**: si alguna vez el reporte Serenity
"parece" no reflejar la última corrida, sospechar primero de este punto
8 (comparar timestamps) antes de asumir que los tests no corrieron.

---

## 5. Reproducibilidad con Docker

`Dockerfile` (imagen `mcr.microsoft.com/playwright/java:v1.58.0-noble`,
versión de navegador alineada con la librería Java resuelta por Gradle) +
`docker-compose.tests.yml` (secundario, servicios `api-tests`/`ui-tests`).
Guía completa y notas de diseño (incluyendo los problemas #5, #6, #7 de la
sección 4 explicados con más detalle) en [`DOCKER.md`](./DOCKER.md).

Comando recomendado (NO usar `up --abort-on-container-exit`: mata el
segundo contenedor si el primero termina con código ≠ 0, y esta suite
tiene fallos intencionales):
```bash
docker compose -f docker-compose.tests.yml build
docker compose -f docker-compose.tests.yml run --rm api-tests
docker compose -f docker-compose.tests.yml run --rm ui-tests
```

---

## 6. CI/CD — GitHub Actions

`.github/workflows/tests.yml` — manual (`workflow_dispatch`), dos inputs:
`suite` (`api`/`ui`/`both`) y `tag` (Cucumber tag libre, vacío = todos).
Hace checkout de este repo **y** de `intermedia-group/automation-testing-artefacts`
(público) como sistema bajo prueba, lo levanta con `docker compose`,
corre `CiApiTest`/`CiUiTest` (los runners sin tag fijo, ver sección 2) con
el tag reenviado dinámicamente, y publica el reporte Serenity + JUnit como
artifacts. Nunca se pudo ejecutar realmente dentro de GitHub Actions desde
esta sesión (sin acceso a esa infraestructura) — **queda pendiente de
confirmar en una corrida real la primera vez que se dispare.**

---

## 7. Reportes y documentación

| Archivo | Qué es |
|---|---|
| `README.md` | Documentación técnica del proyecto: arquitectura, stack, diagramas, cómo correr |
| `REPORT.md` | Reporte de evaluación del reto (estrategia, bugs, métricas, skills) — copia adaptada de `automation-testing-artefacts/REPORT_TEMPLATE.md`, con rutas propias de este repo |
| `DOCKER.md` | Guía de reproducibilidad Docker + los 3 problemas reales encontrados (puntos 5-7 de la sección 4) |
| `PROJECT_CONTEXT.md` | Este archivo |
| `docs/context/*.md` | Convenciones extraídas de los repos de referencia (fuente para los skills) |
| `target/site/serenity/index.html` | Reporte Serenity BDD combinado (generado, no versionado — ver `.gitignore`) |

---

## 8. Empaquetado (.zip)

Último criterio de entrega del reto ("Reproducibilidad"/"Empaquetado").
Se generó `RetoAutomationIntermedia-BrayanGranado.zip` (~27MB) en
`Documentos\Automatizacion\reto intermedia\` (**fuera de este repo, no
versionado** — es un artefacto de entrega, no algo que deba vivir en git).
Contiene los 89 archivos versionados de este repo + el reporte Serenity
generado (`target/site/serenity/`) en el momento de la última regresión
completa. Si se pide "el zip" de nuevo, hay que **regenerarlo** (no reusar
el viejo) después de correr una regresión fresca — el script usado está en
el historial de esta sesión, no quedó guardado como archivo reutilizable
en el repo.

---

## 9. Estado actual / pendientes conocidos

- El pipeline de GitHub Actions nunca se disparó de verdad — falta esa
  primera corrida de confirmación.
- El stack de Docker del reto puede haber quedado corriendo localmente
  entre sesiones (se dejó así deliberadamente cuando el usuario lo había
  levantado él mismo) — verificar con `docker ps` antes de asumir su
  estado.
- `RegresionApiTest`/`RegresionUiTest` tienen `@HappyPath` fijo por
  decisión explícita del usuario — no quitarlo sin que lo pida.
- Cualquier escenario nuevo debería usar el flujo de skills (sección 2) y
  terminar verificado contra el sistema real antes de darse por hecho,
  siguiendo el mismo estándar aplicado en todo este proyecto.
