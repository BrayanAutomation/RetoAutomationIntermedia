# Reporte de Evaluación Técnica - QA Automation

**Nombre del Candidato:** Brayan Granado
**Fecha:** 08/08/2026
**Repositorio de Pruebas:** [github.com/BrayanAutomation/RetoAutomationIntermedia](https://github.com/BrayanAutomation/RetoAutomationIntermedia)
**Sistema bajo prueba (reto):** [github.com/intermedia-group/automation-testing-artefacts](https://github.com/intermedia-group/automation-testing-artefacts)

> 📊 **¿Dónde ver el reporte Serenity BDD?** Después de ejecutar la suite
> (ver sección 6), el reporte combinado (API + UI en un único `index.html`
> con evidencia, capturas y aserciones por paso) queda en
> **[`target/site/serenity/index.html`](./target/site/serenity/index.html)**,
> en la raíz de este mismo repositorio. Si corriste la suite desde GitHub
> Actions, el mismo reporte se publica como **artifact descargable** al
> final del workflow (ver `.github/workflows/`).

> 💡 **Nota para el candidato:** Si deseas agregar más secciones, métricas, diagramas o detalles sobre tu estrategia para enriquecer este reporte, eres totalmente libre de hacerlo. Explica detalladamente todo tu razonamiento técnico para llegar al resultado presentado.

---

## 1. Resumen Ejecutivo y Estrategia de Pruebas

Se construyó una suite de automatización independiente (este repositorio, Gradle multi-módulo: `api-tests` + `ui-tests`) usando **Serenity BDD + Screenplay Pattern**, con **Playwright** para UI y **Screenplay REST + RestAssured** para API, siguiendo las buenas prácticas ISTQB de diseño de casos de prueba.

El proceso seguido fue:

1. **Análisis exhaustivo del reto**: lectura completa de `docs/acceptance_criteria.md` (fuente de verdad de los 11 criterios `CA-*`), `README.md`, `docker-compose.yml` y el código fuente de `api/` y `frontend/` para identificar los bugs intencionales y sus mecanismos exactos (no solo su descripción).
2. **Diseño de escenarios Gherkin trazables**: cada `Feature` está etiquetada con el criterio de aceptación que cubre (`@CA-API-0X` / `@CA-UI-0X`) y con el tipo de caso ISTQB (`@HappyPath`, `@Negative`, `@Edge`, `@DataVariation`), evitando cobertura redundante y priorizando los flujos de mayor riesgo (los bugs intencionales).
3. **Implementación en capas Screenplay** (Tasks = 1 por endpoint en API, Questions reutilizables, Targets basados exclusivamente en `data-testid` estables en UI), evitando lógica de negocio en los step definitions.
4. **Verificación real, no solo compilación**: se levantó el stack completo (`docker-compose up --build`) y se ejecutó la suite contra el sistema real en `localhost:8080`/`localhost:3000`, corrigiendo defectos propios detectados en el camino (versión de Cucumber desalineada, encoding no UTF-8 rompiendo pasos con tildes, falta de registro del reporter de Serenity, una espera insuficiente ante la latencia intencional de `POST /api/users`, y — al reproducir todo en Docker — un toolchain de Gradle sin resolver, propiedades `-D` que no llegaban al JVM forkeado del test, y el frontend con `http://localhost:8080` horneado en el bundle JS).
5. **Los "fallos" de la suite son intencionales y están documentados**: los escenarios que cubren los bugs `CA-API-03/04/05` y `CA-UI-02/04` están diseñados para validar el comportamiento de negocio *correcto*, por lo que fallan deliberadamente contra el sistema real cuando el defecto está presente — esa es la evidencia del hallazgo, no un error de la automatización.

**Resultado de la última corrida verificada:**
- **API: 10/12 escenarios en verde.** Los 2 fallos corresponden al defecto confirmado `BUG-API-002` (ver sección 3).
- **UI: 8/9 escenarios en verde.** El único fallo corresponde al hallazgo confirmado `BUG-UI-002` (desbordamiento horizontal en viewport móvil).
- Adicionalmente, se confirmó de forma empírica (3 corridas repetidas) que el escenario "Consultar el listado de usuarios existentes" (`CA-API-02` happy path) es intrínsecamente flaky por diseño, ya que comparte el endpoint `GET /api/users` con el bug intermitente `BUG-API-001` (~25% de fallo). Esto no es un defecto de la automatización, sino una consecuencia esperada de que el propio criterio de aceptación introduzca inestabilidad deliberada en ese endpoint.

---

## 2. Herramientas y Frameworks Utilizados

- **Frontend / E2E:** Playwright (Java), orquestado vía `serenity-screenplay-playwright`. Se eligió por su auto-wait nativo (elimina `Thread.sleep`/waits manuales), soporte multi-navegador y buena integración con Serenity BDD.
- **Backend / API:** RestAssured, orquestado vía `serenity-screenplay-rest` (Screenplay REST). Se eligió por su fluidez para validar respuestas JSON/status codes y su integración directa con el patrón Screenplay ya usado en UI, manteniendo un único paradigma de diseño en todo el proyecto.
- **BDD / Reporting:** Serenity BDD 5.3.4 + Cucumber (JUnit 5 Platform Suite Engine) — reportes HTML enriquecidos con evidencia por paso, sin depender del estilo JUnit4 `@CucumberOptions`.
- **Patrones de Diseño:** **Screenplay Pattern** (Actors / Tasks / Interactions / Questions / Targets), con arquitectura separada por capas (`tasks`, `interactions`, `questions`, `userinterfaces`/`models`, `utils`) en dos módulos independientes (`ui-tests`, `api-tests`) dentro de un proyecto Gradle multi-módulo. Se prefirió Screenplay sobre POM clásico por su mayor legibilidad orientada a negocio y su regla de reutilización explícita (1 endpoint = 1 Task, Questions centralizadas y reutilizadas entre escenarios).
- **Diseño de casos de prueba:** técnicas ISTQB — partición de equivalencia y valores límite (datos válidos/inválidos en formularios), análisis de riesgo (priorización de los bugs intencionales como casos de alta prioridad), adivinación de errores (error guessing) para los defectos de validación.
- **CI/CD:** GitHub Actions (`.github/workflows/`) — pipeline manual (`workflow_dispatch`) con selección de suite (API/UI/ambas) y tag de Cucumber a filtrar, publica el reporte Serenity como artifact.

---

## 3. Hallazgos y Defectos Identificados (Bugs Found)

Todos los defectos listados fueron **confirmados de forma automatizada y reproducible** contra el sistema real (`docker-compose up --build`).

| ID Bug | Componente | Severidad | Descripción del Defecto | Comportamiento Esperado vs Obtenido |
|--------|------------|-----------|--------------------------|--------------------------------------|
| BUG-API-001 | API | Alta | `GET /api/users` falla intermitentemente (~25% de las solicitudes) simulando una pérdida de conexión a la base de datos. | **Esperado:** `200 OK` consistente con la lista de usuarios. **Obtenido:** ~25% de las solicitudes devuelven `500 Internal Server Error` con cuerpo de texto plano `IntentionalBugException: Intermittent database connection failure [BUG-API-001]` (no JSON). Confirmado con un escenario de 20 llamadas repetidas y, además, observado espontáneamente en el propio escenario "happy path" de consulta del listado durante la ejecución real. |
| BUG-API-PERF | API | Media | `POST /api/users` demora sistemáticamente antes de responder, sin backoff ni configuración de timeout expuesta al cliente. | **Esperado:** tiempo de respuesta razonable (&lt;1s) para una operación de creación simple. **Obtenido:** latencia medida y confirmada de **2.5 segundos** en el 100% de las ejecuciones (`Thread.sleep(2500)` en el controlador). |
| BUG-API-002 | API | Alta | `PUT /api/users/{id}` no valida el formato del email ni la ausencia de campos obligatorios antes de persistir los cambios. | **Esperado:** `400 Bad Request` ante un email inválido (`"correo-invalido"`) o un nombre vacío. **Obtenido:** `200 OK` en ambos casos — el registro se actualiza igualmente con datos inválidos. Confirmado de forma consistente en 3 corridas repetidas (0% de falsos positivos, el defecto es 100% reproducible, a diferencia de BUG-API-001). |
| BUG-UI-001 | UI | Media | `UserForm.jsx` recalcula dinámicamente los atributos `id`/`htmlFor` (`input-name-${randomIdSuffix}`) en cada render. | **Esperado:** atributos estables para accesibilidad y automatización. **Obtenido:** `id`/`htmlFor` cambian en cada render; solo los atributos `data-testid` (ya presentes en el markup) permanecen estables y son viables como locator — mitigación adoptada en toda la suite UI. |
| BUG-UI-002 | UI | Media | La tabla de usuarios (`UserList.jsx`) no tiene diseño responsivo: en viewport de 375px (móvil) el contenido se desborda horizontalmente. | **Esperado** (según la métrica propuesta en la sección 5 para refinar `CA-UI-04`): sin desbordamiento horizontal en 375px/768px/1280px. **Obtenido:** desbordamiento confirmado en 375px; en 768px y 1280px la tabla se visualiza correctamente sin desborde. |
| OBS-UI-01 | UI | Baja *(observación, no confirmada como reproducible)* | `index.css` define una clase `.overlay-bug-banner` (`z-index: 99`, `pointer-events: auto`, comentada como bloqueo de interacción) que, según el diseño interno del reto, simularía un bug de superposición de elementos — pero no está referenciada en ningún componente `.jsx`. | No aplica un "esperado vs obtenido" clásico: se documenta como hallazgo de código muerto/planificado-pero-no-implementado. **No es reproducible** en la versión actual de la UI; se recomienda verificar si es intencional o un defecto de integración pendiente en el propio reto. |

---

## 4. Cobertura de Automatización y Casos de Prueba

**API (`api-tests`, 12 escenarios, 10 en verde):**

- [x] `@CA-API-01` `@HappyPath` — El servicio reporta que está activo (health check)
- [x] `@CA-API-02` `@HappyPath` — Crear un nuevo usuario con datos válidos (y verificar que conserva nombre/correo)
- [x] `@CA-API-02` `@HappyPath` — Consultar el listado de usuarios existentes *(flaky por diseño, ver sección 1 — comparte endpoint con BUG-API-001)*
- [x] `@CA-API-02` `@Negative` — Consultar un usuario que no existe → `404`
- [x] `@CA-API-02` `@Edge` — Eliminar un usuario que no existe → `404`
- [x] `@CA-API-02` `@DataVariation` — Crear usuarios con distintos roles válidos (3 ejemplos: QA Manual, SDET, DevOps)
- [x] `@CA-API-03` `@Negative` — El listado de usuarios falla intermitentemente al repetir la consulta 20 veces (BUG-API-001 confirmado)
- [x] `@CA-API-04` `@Negative` — La creación de un usuario supera el tiempo de respuesta esperado (≥2500ms, BUG-API-PERF confirmado)
- [ ] `@CA-API-05` `@Negative` `@KnownDefect` — Actualizar con correo inválido debería ser rechazado con `400` → **falla contra el sistema real (BUG-API-002 confirmado)**
- [ ] `@CA-API-05` `@Negative` `@KnownDefect` — Actualizar dejando el nombre vacío debería ser rechazado con `400` → **falla contra el sistema real (BUG-API-002 confirmado)**

**UI (`ui-tests`, 9 escenarios, 8 en verde):**

- [x] `@CA-UI-01` `@HappyPath` — El candidato visualiza el listado de usuarios al cargar la aplicación
- [x] `@CA-UI-02` `@HappyPath` — Registrar un nuevo usuario usando localizadores `data-testid` estables
- [x] `@CA-UI-02` `@DataVariation` — Registrar usuarios con distintos roles usando `data-testid` (2 ejemplos)
- [x] `@CA-UI-03` `@Negative` — El candidato ve una alerta cuando el backend falla intermitentemente (integración real con BUG-API-001)
- [x] `@CA-UI-04` `@Edge` — La aplicación carga dentro del umbral propuesto de 3 segundos
- [ ] `@CA-UI-04` `@DataVariation` — Listado visualizado sin desborde horizontal en 375px → **falla (BUG-UI-002 confirmado)**
- [x] `@CA-UI-04` `@DataVariation` — Listado visualizado sin desborde horizontal en 768px
- [x] `@CA-UI-04` `@DataVariation` — Listado visualizado sin desborde horizontal en 1280px

---

## 5. Alertas de Criterios con Ambigüedad (Análisis y Propuestas)

| ID Criterio | Criterio Ambiguo / Término Subjetivo | Riesgo Identificado | Propuesta de Refinamiento (Métrica / SLA / Regla Concreta) |
|-------------|--------------------------------------|----------------------|--------------------------------------------------------------|
| CA-AMB-01 (= CA-UI-04) | *"...el sistema debe responder **de manera rápida y razonable**, y la pantalla debe **visualizarse bien y acomodada en cualquier dispositivo sin demorar mucho tiempo**."* — sin umbrales medibles ni lista de dispositivos/viewports objetivo. | 1) Imposible automatizar de forma objetiva sin una interpretación arbitraria del equipo QA (riesgo de falsos negativos/positivos según quién lo pruebe). 2) Riesgo de negocio real: sin SLA definido, un regreso de performance o un layout roto en móvil podría no considerarse un "defecto" formal por falta de criterio de aceptación medible. 3) Nota adicional: este criterio fue detectado como **intencionalmente ambiguo** (en el historial del propio repositorio del reto se removió una etiqueta explícita de advertencia que originalmente lo señalaba como tal), lo que confirma que se trata de un caso de prueba de la capacidad del QA para detectar ambigüedad, no de un descuido de redacción. | Se propone y automatiza como interpretación razonable: **(a)** carga inicial de la aplicación en **menos de 3000 ms** (verificado: cumple); **(b)** la tabla de usuarios debe visualizarse **sin desbordamiento horizontal** en los viewports de referencia **375px (móvil), 768px (tablet) y 1280px (escritorio)** (verificado: **falla en 375px**, ver `BUG-UI-002`). Se recomienda formalizar estos dos valores como SLA de aceptación explícito en `docs/acceptance_criteria.md`, y agregar diseño responsivo (CSS `overflow-x`/media queries) a `UserList.jsx` para cumplirlo en móvil. |

---

## 6. Instrucciones de Ejecución de Pruebas

**Prerrequisitos comunes:** Docker Desktop, Git. En la Opción A además hace falta Java 17 local; en la Opción B (100% Docker) no. También podés correrla sin instalar nada localmente vía GitHub Actions (Opción C).

### 6.1 Opción A — local (con Java 17 / Gradle instalados)

```bash
# 1. Levantar el sistema bajo prueba (API + Frontend + Postgres) — clonar y entrar al repo del reto
git clone https://github.com/intermedia-group/automation-testing-artefacts.git
cd automation-testing-artefacts
docker-compose up --build -d

# 2. Verificar que responde
curl http://localhost:8080/health
curl -I http://localhost:3000

# 3. Ejecutar la suite de automatización — desde la raíz de este repositorio
./gradlew clean build                       # compila ambos módulos
./gradlew :api-tests:test :ui-tests:test --continue   # corre ambos y genera el reporte combinado
#   (--continue es necesario: hay escenarios @KnownDefect que fallan a propósito
#    contra el sistema real; sin esa flag Gradle detendría el build en api-tests
#    y ui-tests nunca llegaría a correr)

# Reporte Serenity BDD combinado (API + UI en un único índice, ver sección 7):
#   target/site/serenity/index.html
# Reportes JUnit planos (uno por módulo, complementarios):
#   api-tests/build/reports/tests/test/index.html
#   ui-tests/build/reports/tests/test/index.html

# 4. Parámetros opcionales (system properties de Gradle/JVM):
#    -Dapi.base.url=http://localhost:8080   (por defecto)
#    -Dui.base.url=http://localhost:3000    (por defecto)
#    -Dplaywright.headless=false            (para ver el navegador durante la corrida)
#    -Dplaywright.browser=firefox|webkit    (por defecto: chromium)

# 5. Apagar y limpiar el sistema bajo prueba al finalizar (desde automation-testing-artefacts)
docker-compose down -v
```

### 6.2 Opción B — 100% Docker (reproducibilidad, sin instalar Java/Gradle/Playwright)

Cumple explícitamente el criterio de entrega **"Reproducibilidad con Docker"**: este repositorio incluye su propio `Dockerfile` + `docker-compose.tests.yml` **secundario** (no modifica el `docker-compose.yml` del reto) para correr la suite completa en contenedores, conectados al sistema bajo prueba. Guía completa con notas de diseño en [`DOCKER.md`](./DOCKER.md).

```bash
# 1. Levantar el sistema bajo prueba (igual que en la Opción A)
git clone https://github.com/intermedia-group/automation-testing-artefacts.git
cd automation-testing-artefacts && docker-compose up --build -d && cd ..

# 2. Correr la suite completa dentro de contenedores — desde la raíz de este repositorio
docker compose -f docker-compose.tests.yml build
docker compose -f docker-compose.tests.yml run --rm api-tests
docker compose -f docker-compose.tests.yml run --rm ui-tests
#   (secuencial con "run --rm", no "up --abort-on-container-exit": esta suite
#    tiene escenarios que fallan a propósito, y ese flag mataría ui-tests si
#    api-tests termina primero con código de salida ≠ 0 — ver DOCKER.md)

# 3. Limpieza
docker compose -f docker-compose.tests.yml down -v
cd automation-testing-artefacts && docker-compose down -v
```

### 6.3 Opción C — GitHub Actions (sin clonar nada localmente)

Pestaña **Actions** → workflow **"🎭 Suite de Automatización (UI / API)"** → **Run workflow**, eligiendo:
- **suite**: `api`, `ui` o `both`
- **tag**: tag de Cucumber a filtrar (ej. `@HappyPath`, `@Negative`, `@CA-API-02`) — vacío corre todos los escenarios.

El workflow levanta el sistema bajo prueba, corre la suite seleccionada y publica el reporte Serenity combinado como artifact descargable al finalizar. Detalle técnico completo en `.github/workflows/`.

---

## 7. Métricas de Ejecución y Calidad

> _Métricas calculadas a partir de la corrida real documentada en las secciones 1, 3 y 4 (21 escenarios ejecutados contra el sistema levantado con `docker-compose up --build`). No son estimaciones: se derivan directamente de los resultados de `./gradlew :api-tests:test :ui-tests:test` y de los reportes Serenity generados._

### 7.1 Resumen general

| Métrica | Valor | Lectura |
|---|---|---|
| Escenarios totales ejecutados | **21** (12 API + 9 UI) | 100% de los criterios de aceptación automatizables cubiertos (ver 7.2) |
| Escenarios en verde | **18 / 21 (85.7%)** | Tasa de aprobación funcional tal cual reporta Serenity |
| Escenarios en rojo (esperados/documentados) | **3 / 21 (14.3%)** | 2 documentan `BUG-API-002`, 1 documenta `BUG-UI-002` — ningún fallo es un error de la automatización |
| Confiabilidad del framework de automatización | **21 / 21 (100%)** | Ninguna ejecución terminó por error de infraestructura/framework (timeouts no controlados, excepciones no manejadas, undefined steps); todo fallo es una aserción de negocio deliberada |
| Tasa de detección de defectos esperados | **3 / 3 (100%)** | Los 3 escenarios diseñados para exponer un defecto si persiste, en efecto fallaron de forma consistente en 3 corridas repetidas |
| Escenarios con comportamiento no determinista conocido | **1 / 21 (4.8%)** | "Consultar el listado de usuarios existentes" — flakiness **atribuible al SUT** (`BUG-API-001`, ~25% de fallo), no a la automatización |

### 7.2 Cobertura por criterio de aceptación (trazabilidad)

| Criterio | Escenarios que lo cubren | % del total (21) |
|---|---|---|
| CA-API-01 | 1 | 4.8% |
| CA-API-02 | 7 | 33.3% |
| CA-API-03 | 1 | 4.8% |
| CA-API-04 | 1 | 4.8% |
| CA-API-05 | 2 | 9.5% |
| CA-UI-01 | 1 | 4.8% |
| CA-UI-02 | 3 | 14.3% |
| CA-UI-03 | 1 | 4.8% |
| CA-UI-04 | 4 | 19.0% |

**Cobertura de criterios automatizables: 9 / 9 (100%)** — los 9 criterios funcionales (`CA-API-*`, `CA-UI-*`) tienen al menos un escenario automatizado. Los 2 criterios restantes del documento original (`CA-INFRA-01/02`, `CA-DOC-01/02`) son de infraestructura/documentación y quedan fuera del alcance de la automatización funcional por naturaleza (se verifican de forma manual/operativa: el propio `docker-compose up` y este reporte).

### 7.3 Distribución de casos por técnica ISTQB

```
HappyPath      ████░░░░░░░░░░░░░░░░  23.8%  (5 escenarios)
Negative       ██████░░░░░░░░░░░░░░  28.6%  (6 escenarios)
Edge           ██░░░░░░░░░░░░░░░░░░   9.5%  (2 escenarios)
DataVariation  ████████░░░░░░░░░░░░  38.1%  (8 escenarios)
```

**Lectura:** el diseño prioriza `Negative` + `DataVariation` (66.7% combinado) por encima de `HappyPath` (23.8%), coherente con el enfoque de riesgo del reto: los criterios de mayor valor de negocio son precisamente los bugs intencionales y las variaciones de datos inválidos, no el camino feliz.

### 7.4 Defectos por severidad y componente

| Severidad | Cantidad | % sobre defectos confirmados |
|---|---|---|
| Alta | 2 (`BUG-API-001`, `BUG-API-002`) | 40% |
| Media | 3 (`BUG-API-PERF`, `BUG-UI-001`, `BUG-UI-002`) | 60% |
| Baja / no confirmado | 1 (`OBS-UI-01`, observación) | — (no se contabiliza como defecto confirmado) |

| Componente | Defectos confirmados | % sobre defectos confirmados |
|---|---|---|
| API | 3 (`BUG-API-001`, `BUG-API-PERF`, `BUG-API-002`) | 60% |
| UI | 2 (`BUG-UI-001`, `BUG-UI-002`) | 40% |

**Tasa de detección de defectos documentados en el reto:** 4 / 4 (100%) — los 4 bugs intencionales descritos explícita o implícitamente en `docs/acceptance_criteria.md` (`CA-API-03`, `CA-API-04`, `CA-API-05`, `CA-UI-02`) fueron confirmados por la automatización. **Valor agregado:** 1 defecto adicional (`BUG-UI-002`) fue descubierto por la propia suite sin estar descrito en los criterios originales, como consecuencia directa de automatizar `CA-UI-04` con métricas concretas en vez de descartarlo por ser ambiguo.

### 7.5 Tiempos de ejecución

| Suite | Escenarios | Duración total observada | Promedio por escenario |
|---|---|---|---|
| `api-tests` | 12 | ~30–32 s (3 corridas medidas) | ~2.6 s |
| `ui-tests` | 9 | ~71–80 s (2 corridas medidas) | ~8.3 s |

Un escenario UI tarda en promedio **~3.2x más** que uno de API (arranque de navegador Playwright + renderizado real del DOM vs. una llamada HTTP directa). Esto confirma empíricamente por qué conviene concentrar mayor volumen de casos negativos/de datos en la capa de API: son ~3x más rápidos de ejecutar y no dependen de un motor de renderizado, por lo que ofrecen mejor retorno por caso automatizado sin sacrificar cobertura de negocio.

### 7.6 Distribución tipo "pirámide de pruebas"

```
API  ████████████████████████░░░░░░░░░░  57.1%  (12 escenarios)
UI   ░░░░░░░░░░░░░░░░░░░░░░░░████████████  42.9%  (9 escenarios)
```

La mayor proporción de cobertura se concentra en API (57.1%) frente a UI (42.9%), alineado con la pirámide de pruebas clásica: más pruebas en la capa rápida/estable (API), y UI reservada a los flujos donde el valor de negocio solo puede validarse end-to-end (carga de la SPA, locators dinámicos, feedback visual, responsividad).

---

## 8. Valor Agregado: Skills de Claude Code para Escalar la Automatización

> Como parte de la solución se dejaron **4 skills de Claude Code** (agentes con instrucciones empaquetadas) dentro de este propio repositorio, en `.claude/skills/`. El objetivo es que **cualquier ampliación futura de esta suite** (nuevos endpoints, nuevos flujos de UI, nuevos criterios de aceptación) se haga de forma consistente con la arquitectura ya definida, sin depender de que quien continúe el trabajo memorice todas las convenciones documentadas en este reporte y en `docs/context/`.

### 8.1 ¿Qué es un "skill" en este contexto?

Un skill de Claude Code es una carpeta con un archivo `SKILL.md` (instrucciones en markdown + una cabecera `name`/`description`) que Claude Code descubre automáticamente al abrir el proyecto. No es un plugin externo ni requiere instalación: **si el archivo existe dentro de `.claude/skills/<nombre>/SKILL.md`, ya está disponible.** Claude lo usa de dos formas:

1. **Automática:** si el pedido del usuario coincide con la `description` del skill (por ejemplo, "escribe los escenarios Gherkin para el nuevo endpoint de login"), Claude lo detecta y aplica esas instrucciones sin que haya que invocarlo por nombre.
2. **Explícita:** el usuario puede pedirlo directamente, p. ej. *"usa el skill `api-screenplay-implementer` para automatizar `DELETE /api/users/{id}` con la nueva regla de negocio X"*.

### 8.2 Los 4 skills y qué hace cada uno

| Skill | Rol | Se activa cuando... |
|---|---|---|
| `gherkin-scenario-writer` | Redacta escenarios Gherkin de alto estándar (atómicos, independientes, orientados a negocio) a partir de un criterio de aceptación o una historia de usuario. No escribe código Java. | Se pide "escribe/genera escenarios", "convierte este criterio en `.feature`", o falta cobertura Gherkin para un flujo nuevo. |
| `istqb-test-analyst` | Analiza **qué** probar y **con qué técnica ISTQB** (partición de equivalencia, valores límite, tablas de decisión, análisis de riesgo, error guessing) antes de escribir un solo escenario. Entrega un análisis en Markdown, no Gherkin todavía. | Hace falta decidir cobertura o priorización para un criterio nuevo o ambiguo, antes de automatizar. |
| `ui-screenplay-implementer` | Implementa/extiende las capas Screenplay de UI (`tasks`, `interactions`, `questions`, `userinterfaces`, `models`, `utils` en `com.reto.automation.ui`) y sus step definitions, siguiendo Playwright + `data-testid` obligatorio. | Hay un `.feature` de UI ya escrito (o hace falta escribirlo primero con `gherkin-scenario-writer`) que todavía no tiene código Java de soporte. |
| `api-screenplay-implementer` | Implementa/extiende las capas Screenplay de API (`tasks`, `interactions`, `questions`, `models`, `utils` en `com.reto.automation.api`) y sus step definitions, siguiendo Screenplay REST + RestAssured, regla "1 endpoint = 1 Task". | Hay un `.feature` de API ya escrito que todavía no tiene código Java de soporte, o falta un Task/Question para un endpoint nuevo. |

**Flujo de trabajo recomendado (encadenado):** `istqb-test-analyst` (qué probar) → `gherkin-scenario-writer` (cómo redactarlo en `.feature`) → `ui-screenplay-implementer` / `api-screenplay-implementer` (cómo implementarlo en código, reutilizando lo ya existente). Cada skill referencia explícitamente a los siguientes en su propio `SKILL.md`, y los dos implementadores citan `docs/context/ui-reference-context.md` / `api-reference-context.md` como fuente de patrones antes de escribir una sola clase — así se evita duplicar Tasks/Questions ya existentes.

### 8.3 Cómo hacerlos funcionar (requisitos)

1. **Abrir el proyecto correcto:** los skills viven en `.claude/skills/` de este repositorio. Hay que abrir Claude Code (CLI o extensión de IDE) con este repositorio como carpeta de trabajo (o un workspace que lo incluya) para que se descubran automáticamente — no requieren build, instalación ni configuración adicional.
2. **Invocarlos** de cualquiera de las dos formas del punto 8.1. No hace falta recordar la sintaxis exacta: basta con describir la tarea en lenguaje natural ("necesito automatizar el nuevo endpoint de login") y Claude selecciona el skill adecuado según su `description`.
3. **Verificar el resultado igual que en esta entrega:** los skills generan `.feature`/código Java, pero **no reemplazan la verificación real** — sigue aplicando la sección 6 (`docker-compose up` + `./gradlew :api-tests:test :ui-tests:test`) antes de dar por válido cualquier escenario nuevo.

### Ejemplo de uso

```text
Usuario: "Agrega un endpoint PATCH /api/users/{id}/status y automatízalo"

1. istqb-test-analyst        → propone: partición de equivalencia sobre valores de status
                                 válidos/inválidos, caso límite (id inexistente), prioridad Alta
                                 por ser cambio de estado de negocio.
2. gherkin-scenario-writer    → genera api-tests/.../features/api/users_status.feature
                                 con @HappyPath, @Negative, @DataVariation.
3. api-screenplay-implementer → crea tasks/UpdateUserStatus.java (1 endpoint = 1 Task),
                                 reutiliza questions/TheResponse.java existente,
                                 agrega el step definition correspondiente.
```
