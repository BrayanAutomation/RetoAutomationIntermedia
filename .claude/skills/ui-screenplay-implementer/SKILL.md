---
name: ui-screenplay-implementer
description: Implementa o extiende las capas Screenplay de UI (Tasks, Interactions, Questions, Targets/userinterfaces, Models, Utils) en el módulo ui-tests, y sus step definitions, siguiendo la arquitectura Serenity BDD + Screenplay + Playwright de este proyecto. Úsalo cuando haya que automatizar un escenario UI ya escrito en Gherkin, o cuando falte un Task/Question/Target para un flujo del frontend.
---

# UI Screenplay Implementer

Implementas código Java en el módulo `ui-tests` de `RetoAutomationIntermedia`,
siguiendo estrictamente el patrón Screenplay con Serenity BDD + Playwright.
No escribes lógica de negocio en step definitions ni en tests.

## Contexto obligatorio a leer antes de escribir código

1. `docs/context/ui-reference-context.md` — patrones, imports correctos,
   ejemplos reales extraídos del proyecto de referencia.
2. Código ya existente en
   `ui-tests/src/main/java/com/reto/automation/ui/**` y
   `ui-tests/src/test/java/com/reto/automation/ui/**` — **motor de
   reutilización obligatorio**: antes de crear una clase nueva, busca si ya
   existe un Task/Question/Target equivalente o extensible.
3. El `.feature` que se está automatizando (o que hay que crear primero con
   el skill `gherkin-scenario-writer` si no existe).

## Estructura de paquetes obligatoria (`com.reto.automation.ui`)

```
tasks            -> acciones de negocio (Task) — ej. FillUserForm, SubmitUserForm
interactions     -> acciones reutilizables de bajo nivel (Interaction)
questions        -> validaciones (Question<T>)
userinterfaces   -> localizadores (Target), sufijo "UI"
models           -> DTOs de datos de prueba
utils            -> constantes/config/generadores de datos
```

## Reglas críticas (no negociables)

- **Imports permitidos**: `net.serenitybdd.screenplay.playwright.*` (Target,
  interactions como Click/Enter/Open, abilities.BrowseTheWebWithPlaywright).
- **PROHIBIDO**: Selenium, `WebDriver`, `Thread.sleep`, wrappers custom de
  click/type que dupliquen Screenplay-Playwright. Playwright ya auto-espera.
- **Locators**: SIEMPRE `data-testid` estables del frontend
  (`user-name-input`, `user-email-input`, `user-role-select`,
  `submit-user-btn`, `users-table`, `user-row-{id}`, `delete-user-{id}`,
  `refresh-btn`, `error-alert`). NUNCA `id`/`htmlFor` (son dinámicos, ver
  CA-UI-02) ni XPaths frágiles basados en posición/texto traducible.
- **Un Task = una acción de negocio**, factory estática vía
  `Tasks.instrumented(Clase.class, args...)` (funciona igual para `Task` e
  `Interaction`).
- **Step definitions = solo orquestación**: `actor.attemptsTo(...)` /
  `actor.should(...)` / lectura de `Question` con `.answeredBy(actor)`. Cero
  lógica de negocio, cero aserciones sobre datos crudos sin pasar por una
  `Question`.
- **No duplicar lógica**: si el Target o Task ya existe (p. ej.
  `HomePageUI.USERS_TABLE`, `OpenApp.home()`), reutilízalo.
- **Alta cohesión / bajo acoplamiento**: una clase, una responsabilidad;
  nombres semánticos en español para Tasks/Questions de negocio (consistente
  con el resto del proyecto).

## Checklist antes de dar por terminada una implementación

1. ¿Existe ya un Target para ese elemento? Si no, créalo en `userinterfaces`
   usando `data-testid`.
2. ¿Existe ya un Task para esa acción? Si no, créalo en `tasks`.
3. ¿La validación necesita una `Question` nueva o reutiliza una existente
   (`TableIsVisible`, `ErrorBannerIsVisible`, `UserIsListed`, ...)?
4. ¿El step definition solo orquesta, sin lógica embebida?
5. ¿El runner JUnit5 (`RegresionUiTest`) ya apunta a la carpeta de features
   correcta (`features/ui`) y al paquete de glue
   (`com.reto.automation.ui.stepsdefinitions`)? Si agregaste un paquete de
   steps nuevo, no hace falta tocar el runner (Cucumber descubre por paquete).
6. Ejecuta mentalmente (o sugiere al usuario ejecutar)
   `./gradlew :ui-tests:test` para verificar compilación y ejecución real.
