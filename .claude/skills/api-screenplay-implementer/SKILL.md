---
name: api-screenplay-implementer
description: Implementa o extiende las capas Screenplay de API (Tasks, Interactions, Questions, Models, Utils) en el módulo api-tests, y sus step definitions, siguiendo la arquitectura Serenity BDD + Screenplay REST + RestAssured de este proyecto. Úsalo cuando haya que automatizar un escenario de API ya escrito en Gherkin, o cuando falte un Task/Question para un endpoint.
---

# API Screenplay Implementer

Implementas código Java en el módulo `api-tests` de `RetoAutomationIntermedia`,
siguiendo estrictamente el patrón Screenplay con Serenity BDD + Screenplay
REST + RestAssured. No escribes lógica de negocio en step definitions.

## Contexto obligatorio a leer antes de escribir código

1. `docs/context/api-reference-context.md` — patrones, imports correctos,
   ejemplos reales extraídos del proyecto de referencia.
2. Código ya existente en
   `api-tests/src/main/java/com/reto/automation/api/**` y
   `api-tests/src/test/java/com/reto/automation/api/**` — **motor de
   reutilización obligatorio**: antes de crear una clase nueva, busca si ya
   existe un Task/Question equivalente (p. ej. `TheResponse.hasStatusCode(int)`
   sirve para casi cualquier validación de código de estado).
3. El `.feature` que se está automatizando (o que hay que crear primero con
   el skill `gherkin-scenario-writer` si no existe).
4. Los endpoints y bugs intencionales documentados en
   `docs/acceptance_criteria.md` del repo del reto y en
   `docs/context/api-reference-context.md`.

## Estructura de paquetes obligatoria (`com.reto.automation.api`)

```
tasks         -> 1 endpoint = 1 Task (GetHealth, ListUsers, CreateUser, ...)
interactions  -> acciones reutilizables de bajo nivel (repetir llamadas, medir tiempo)
questions     -> validaciones de respuesta (ResponseConsequence / Question<T>)
models        -> request/response POJOs (Lombok @Data @Builder)
utils         -> constantes/config (base URL vía ApiConstants, nunca hardcode)
```

## Reglas críticas (no negociables)

- **Imports permitidos**: `net.serenitybdd.screenplay.rest.*` (Get/Post/Put/Delete,
  CallAnApi, ResponseConsequence), `net.serenitybdd.rest.SerenityRest` para
  acceder a la última respuesta cruda cuando se necesite algo que
  `ResponseConsequence` no cubre (medir tiempo, repetir llamadas, extraer un
  id para recordarlo).
- **1 endpoint = 1 Task**. No mezclar varias llamadas HTTP en un solo Task
  ni en un solo step (anti-patrón detectado y evitado en el proyecto de
  referencia).
- **Ability una sola vez por escenario**: `theActorCalled("Usuario").can(CallAnApi.at(BASE_URL))`
  se otorga en el `@Before` (`Hooks`), no se reasigna en cada Task.
- **Requests/Responses → Models**: cualquier body enviado o inspeccionado en
  detalle debe modelarse como POJO en `models` (`UserRequest`/`UserResponse`),
  no como `Map<String,Object>` suelto ni JSON crudo en el step.
- **Validaciones → Questions**: nunca uses `assertThat` directo sobre un
  `SerenityRest.lastResponse()` crudo si ya existe (o se puede crear) una
  `Question`/`ResponseConsequence` reutilizable en `questions/TheResponse`.
  Excepción aceptada: `Question<Boolean>`/`Question<Long>` simples evaluadas
  con `.answeredBy(actor)` y afirmadas con AssertJ en el step (patrón ya
  usado para latencia y fallos intermitentes).
- **Bugs intencionales**: al automatizar CA-API-03/04/05, el escenario debe
  validar el comportamiento de **negocio correcto esperado** (p. ej. `400`
  en vez de `200`), documentando así el defecto cuando el test falla contra
  el sistema real. No "ajustes" la aserción al comportamiento buggy para que
  el test pase en verde — eso oculta el hallazgo.
- **Step definitions = solo orquestación**, igual que en UI.

## Checklist antes de dar por terminada una implementación

1. ¿Existe ya un Task para ese endpoint? Si no, créalo en `tasks` (nombre =
   verbo + recurso, ej. `UpdateUser`).
2. ¿La validación reutiliza `TheResponse` o necesita un método nuevo ahí?
3. ¿El modelo de request/response ya existe en `models`? Si no, créalo con
   Lombok (`@Data @Builder @NoArgsConstructor @AllArgsConstructor`).
4. ¿El step definition solo orquesta, sin lógica embebida?
5. ¿El runner JUnit5 (`RegresionApiTest`) apunta a `features/api` y al
   paquete `com.reto.automation.api.stepsdefinitions`? No hace falta tocarlo
   al agregar step definitions nuevas en ese mismo paquete.
6. Ejecuta mentalmente (o sugiere al usuario ejecutar)
   `./gradlew :api-tests:test` para verificar compilación y ejecución real
   contra `http://localhost:8080` (con el reto levantado vía
   `docker-compose up --build`).
