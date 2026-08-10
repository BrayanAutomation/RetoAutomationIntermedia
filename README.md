# RetoAutomationIntermedia

Framework completo UI + API con Serenity BDD + Screenplay Pattern,
Playwright para UI y Screenplay REST + RestAssured para API, construido
para el reto técnico de `automation-testing-artefacts`.

## Estructura

- `api-tests/` — módulo Gradle, capas Screenplay `com.reto.automation.api` (`tasks`, `interactions`, `questions`, `models`, `utils`), features en `src/test/resources/features/api`.
- `ui-tests/` — módulo Gradle, capas Screenplay `com.reto.automation.ui` (`tasks`, `interactions`, `questions`, `userinterfaces`, `models`, `utils`), features en `src/test/resources/features/ui`.
- `docs/context/` — contexto arquitectónico extraído de los repos de referencia (para los skills de `.claude/skills`).
- `.claude/skills/` — 4 skills de Claude Code (`gherkin-scenario-writer`, `istqb-test-analyst`, `ui-screenplay-implementer`, `api-screenplay-implementer`).

## Ejecutar la suite (local, con Java/Gradle instalados)

```bash
cd ../automation-testing-artefacts && docker-compose up --build -d   # sistema bajo prueba
./gradlew clean build
./gradlew :api-tests:test :ui-tests:test --continue
```

`--continue` es necesario si querés que corran ambos módulos aunque uno
falle: hay escenarios `@KnownDefect` que fallan a propósito contra el
sistema real (documentan bugs, ver `docs/context/`) — sin esa flag Gradle
detiene el build en el primer módulo que falla.

**Reporte Serenity BDD combinado** (API + UI en un único `index.html`):
`target/site/serenity/index.html`. Ambos módulos escriben su evidencia en
esa misma carpeta compartida (ver `serenity.outputDirectory` en
`build.gradle`) y disparan la misma tarea `aggregate` del proyecto raíz al
terminar.

## Ejecutar la suite en Docker (sin instalar Java/Gradle/Playwright)

Ver **[DOCKER.md](./DOCKER.md)** — guía completa de reproducibilidad con
`Dockerfile` + `docker-compose.tests.yml`.

```bash
docker compose -f docker-compose.tests.yml build
docker compose -f docker-compose.tests.yml run --rm api-tests
docker compose -f docker-compose.tests.yml run --rm ui-tests
```

## Parámetros disponibles

- `-Dapi.base.url` (por defecto `http://localhost:8080`)
- `-Dui.base.url` (por defecto `http://localhost:3000`)
- `-Dplaywright.headless` (por defecto `false` en local; forzado a `true` en Docker)
- `-Dplaywright.browser` (`chromium` | `firefox` | `webkit`)
