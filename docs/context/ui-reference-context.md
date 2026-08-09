# Contexto de referencia — Arquitectura UI (Screenplay + Playwright)

> Extraído por análisis del repositorio de referencia `AutomarizacionWebLaTinka`
> (Gradle, Serenity BDD 5.3.4, Screenplay + Playwright). Este documento es la
> fuente de patrones para el skill `ui-screenplay-implementer` y para
> cualquier ejecución futura que deba extender `ui-tests` en este proyecto.
> **No modifica el repo de referencia**, solo documenta lo aprendido de él.

## Stack y dependencias

- Gradle (no Maven), Java 17, Serenity BDD **5.3.4** (todos los módulos
  Serenity fijados a la misma versión vía `${serenityCoreVersion}`).
- Dependencias clave: `serenity-core`, `serenity-cucumber`, `serenity-screenplay`,
  `serenity-screenplay-playwright`, `serenity-playwright`, `serenity-junit5`,
  `serenity-ensure`, Cucumber vía **JUnit 5 Platform Suite**
  (`cucumber-junit-platform-engine`), NO el estilo JUnit4 `@CucumberOptions`.

## Imports correctos vs. prohibidos

```java
// CORRECTO
import net.serenitybdd.screenplay.playwright.interactions.*; // Click, Enter, Open...
import net.serenitybdd.screenplay.playwright.Target;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;
```

**PROHIBIDO**: Selenium, `WebDriver`, `Thread.sleep`, wrappers custom de
click/type que dupliquen lo que ya ofrece Screenplay-Playwright. Playwright
tiene auto-wait nativo: no se necesita `WaitUntil`.

## Inicialización del actor (patrón `@Before`/`@After`)

```java
@Before
public void setUp() {
    setTheStage(Cast.whereEveryoneCan(BrowseTheWebWithPlaywright
            .withOptions(launchOptions)
            .withBrowserType(browser)
            .withContextOptions(contextOptions)));
    theActorCalled("Nombre del actor");
    BrowseTheWebWithPlaywright.as(theActorInTheSpotlight())
            .getCurrentPage().navigate(baseUrl);
}

@After
public void tearDown() {
    drawTheCurtain();
}
```

Mejora aplicada en este proyecto respecto a la referencia: la URL base **no**
se hardcodea en el hook, se centraliza en `utils/UiConstants` parametrizable
vía `-Dui.base.url`.

## Estructura de paquetes obligatoria (`com.reto.automation.ui`)

```
tasks            -> acciones de negocio (Task)
interactions     -> acciones reutilizables de bajo nivel (Interaction)
questions        -> validaciones (Question<T>)
userinterfaces   -> localizadores (Target)
models           -> DTOs de datos de prueba
utils            -> constantes/config/generadores de datos
```

Evitar los defectos observados en la referencia: paquete `interations`
(typo) y `Utils` (mayúscula inicial) — en este proyecto se usan siempre
`interactions` y `utils` en minúscula.

## Patrones de capa (ejemplos reales)

**Task** — acción de negocio, factory estática vía `Tasks.instrumented`:
```java
public class JugarSeleccionandoManual implements Task {
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(Click.on(AgregarJugadaUI.BOTON_COMPRAR));
    }
    public static JugarSeleccionandoManual enTinka(String cantidad) {
        return Tasks.instrumented(JugarSeleccionandoManual.class, cantidad);
    }
}
```

**Interaction** — acción de bajo nivel reutilizable, también vía
`Tasks.instrumented` (funciona igual para `Task` e `Interaction`, ambos son
`Performable`):
```java
public class ClickConJs implements Interaction {
    public <T extends Actor> void performAs(T actor) {
        Page page = BrowseTheWebWithPlaywright.as(actor).getCurrentPage();
        page.evaluate("document.querySelector(arguments[0]).click()", locator);
    }
}
```

**Question** — validación, accede a `Page` vía la ability:
```java
public class EstadoDelBoton implements Question<Boolean> {
    public Boolean answeredBy(Actor actor) {
        Page page = BrowseTheWebWithPlaywright.as(actor).getCurrentPage();
        return page.locator(boton.asSelector()).isEnabled();
    }
}
```

**Target/UserInterfaces** — localizadores parametrizables:
```java
public static final Target NUMERO_JUGADA =
        Target.the("Número Jugada A - {0}").locatedBy("//span[@id='LJ1check_{0}']");
// uso: NUMERO_JUGADA.of("1")  /  NUMERO_JUGADA.of("1").asSelector()
```

## Step Definitions

Solo orquestación — delegan 100% en Tasks/Questions:
```java
@When("selecciona jugar Tinka, eligiendo {string} jugadas manuales...")
public void seleccionaJugar(String cantidad) {
    OnStage.theActorInTheSpotlight().attemptsTo(JugarSeleccionandoManual.enTinka(cantidad));
}
```
Nota: en módulos más nuevos de la referencia se detectaron aserciones AssertJ
directas contra `Question<Boolean>` en el propio step (`assertThat(...).isTrue()`)
en vez de usar siempre `actor.should(seeThat(...))`. Ambos son válidos en
este proyecto; se prioriza `actor.should(...)` cuando la Question retorna un
`Consequence` (p. ej. respuestas HTTP) y AssertJ directo para `Question<Boolean>`
simples evaluadas con `.answeredBy(actor)`.

## Feature files

- Organización: un archivo por módulo/dominio bajo
  `src/test/resources/.../features/`, con subcarpetas para módulos grandes.
- Etiquetas de dos niveles: tag amplio (`@E2E`) + tag específico para
  filtrado en CI (`-Ptags=...`). En este proyecto se usan tags de
  trazabilidad al criterio de aceptación (`@CA-UI-01`, etc.) más
  `@HappyPath` / `@Negative` / `@Edge` / `@DataVariation`.

## Locators: la regla crítica de este reto (CA-UI-02)

El frontend bajo prueba (`UserForm.jsx`) cambia dinámicamente `id`/`htmlFor`
en cada render. **Nunca** usar esos atributos como locator. Usar siempre los
`data-testid` estables ya presentes en el markup: `user-name-input`,
`user-email-input`, `user-role-select`, `submit-user-btn`, `users-table`,
`user-row-{id}`, `delete-user-{id}`, `refresh-btn`, `error-alert`.

## CI/CD

La referencia usa GitHub Actions, ejecución headed bajo `xvfb` (por
`setHeadless(false)` hardcodeado). En este proyecto el modo headless es
parametrizable (`-Dplaywright.headless=true|false`), headless por defecto
para poder correr en Docker/CI sin framebuffer virtual.
