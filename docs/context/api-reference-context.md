# Contexto de referencia — Arquitectura API (Screenplay REST + RestAssured)

> Extraído por análisis del repositorio de referencia
> `deuna-aut-py-onboarding-api` (Gradle Kotlin DSL, Serenity BDD 4.0.21,
> Screenplay + Screenplay-REST + RestAssured transitivo). Este documento es
> la fuente de patrones para el skill `api-screenplay-implementer` y para
> cualquier ejecución futura que deba extender `api-tests` en este proyecto.
> **No modifica el repo de referencia**, solo documenta lo aprendido de él.
> Nota: pese al nombre de la carpeta (sufijo "py"), el proyecto es 100% Java.

## Stack y dependencias

- Gradle, Serenity BDD **4.0.21**: `serenity-core`, `serenity-junit`,
  `serenity-screenplay`, `serenity-screenplay-rest`, `serenity-cucumber`,
  `serenity-ensure`. RestAssured **no** se declara explícito: llega
  transitivo vía `serenity-screenplay-rest`.
- En este proyecto se fijan ambos módulos (`serenity-screenplay` y
  `serenity-screenplay-rest`) explícitamente a `${serenityCoreVersion}`
  (5.3.4) para evitar el desalineamiento de versiones detectado en el
  esqueleto inicial (`serenity-screenplay-rest:4.2.34` contra un core 5.3.4).

## Imports correctos

```java
import net.serenitybdd.screenplay.rest.abilities.CallAnApi;
import net.serenitybdd.screenplay.rest.interactions.Get;   // .resource(path)
import net.serenitybdd.screenplay.rest.interactions.Post;  // .to(path).with(...)
import net.serenitybdd.screenplay.rest.interactions.Put;   // .to(path).with(...)
import net.serenitybdd.screenplay.rest.interactions.Delete;// .from(path)
import net.serenitybdd.screenplay.rest.questions.ResponseConsequence;
import net.serenitybdd.rest.SerenityRest; // acceso a la última respuesta cruda
```

## Inicialización del actor (patrón `@Before`/`@After`)

```java
@Before
public void init() {
    OnStage.setTheStage(new OnlineCast());
    OnStage.theActorCalled("Usuario").can(CallAnApi.at(baseUrl));
}
```

Mejora aplicada en este proyecto respecto a la referencia: la ability
`CallAnApi` se asigna **una sola vez** por escenario (a nivel de actor en el
hook), no en cada llamada individual como hacía el wrapper genérico `Api.post/get`
de la referencia (`OnStage.theActorInTheSpotlight().can(CallAnApi.at(baseURL))`
repetido en cada método). La URL base se centraliza en
`utils/ApiConstants` parametrizable vía `-Dapi.base.url`.

## Estructura de paquetes obligatoria (`com.reto.automation.api`)

```
tasks         -> 1 endpoint = 1 Task
interactions  -> acciones reutilizables de bajo nivel (repetir llamadas, medir tiempo)
questions     -> validaciones de respuesta (ResponseConsequence / Question<T>)
models        -> request/response POJOs (Lombok)
utils         -> constantes/config (base URL, headers)
```

Regla explícita del reto: **1 endpoint = 1 Task**. La referencia mezclaba
varias llamadas en un único step gigante (`creaUnUsuario`) — se evita
deliberadamente ese anti-patrón en este proyecto.

## Patrones de capa (ejemplos reales de la referencia)

**Task genérico (wrapper reutilizado por todos los endpoints)**:
```java
public static Performable post(String baseURL, String path, String body, Map<String, Object> headers) {
    return Task.where(actor -> {
        OnStage.theActorInTheSpotlight().can(CallAnApi.at(baseURL));
        OnStage.theActorInTheSpotlight().attemptsTo(
                Post.to(path).with(req -> req.headers(headers).body(body).relaxedHTTPSValidation()));
    });
}
```
En este proyecto se prefiere **un Task nombrado por endpoint** (`CreateUser`,
`UpdateUser`, `DeleteUser`, ...) en vez de un único wrapper genérico, para
cumplir la regla "1 endpoint = 1 Task" y mejorar la legibilidad en el reporte
Serenity.

**Question (ResponseConsequence, consumida con `actor.should(...)`)**:
```java
public static ResponseConsequence respuestaServicioCreateUser() {
    return ResponseConsequence.seeThatResponse("Campos Esperados", response -> response
            .statusCode(200)
            .body("data.syncronizeUser.accessToken", not(blankOrNullString())));
}
// uso:
OnStage.theActorInTheSpotlight().should(Verificar.respuestaServicioCreateUser());
```
Este patrón se replica en `com.reto.automation.api.questions.TheResponse`,
centralizando las aserciones reutilizables (motor de reutilización: no
duplicar Questions por escenario).

**Modelos**: la referencia solo tiene mappers JDBI de base de datos, no
POJOs de request/response (los bodies se arman como JSON crudo vía
FreeMarker). Este proyecto sí define `UserRequest`/`UserResponse` como
POJOs Lombok (`@Data @Builder @NoArgsConstructor @AllArgsConstructor`),
alineado con la regla explícita de Context.md ("Requests/Responses → Models"),
ya que el API bajo prueba no usa GraphQL/plantillas sino JSON REST simple.

## Step Definitions

Solo orquestación, delegan en Tasks/Questions:
```java
@Given("Crea un usuario")
public void creaUnUsuario() {
    llamoAlServicio("SYNCRONIZE_USER");
    OnStage.theActorInTheSpotlight().should(Verificar.respuestaServicioCreateUser());
}
```
Evitar el anti-patrón detectado: mega-steps que encadenan 10+ llamadas de
servicio con lógica de negocio embebida. Cada step debe ser atómico y
delegar en un único Task/Question por vez.

## Endpoints del reto bajo prueba (no de la referencia)

Fuente de verdad: `docs/acceptance_criteria.md` del repo del reto.
- `GET /health` → 200, sin bugs.
- `GET /api/users` → ~25% de las veces 500 texto plano
  `IntentionalBugException...[BUG-API-001]` (CA-API-03).
- `POST /api/users` → siempre demora 2.5s antes de `201` (CA-API-04), sin
  validación de campos.
- `GET /api/users/{id}` → 200 o 404, sin bug.
- `PUT /api/users/{id}` → sin validar `email`/campos vacíos, devuelve `200`
  en vez de `400` (CA-API-05, BUG-API-002).
- `DELETE /api/users/{id}` → 204 o 404, sin bug.

## CI/CD

La referencia usa Azure Pipelines con VPN corporativa y múltiples secretos
inyectados; no aplica a este proyecto (SUT local vía Docker Compose). Para
este proyecto, la ejecución reproducible es `docker-compose up --build` del
reto + `./gradlew :api-tests:test :ui-tests:test` apuntando a
`localhost:8080`/`localhost:3000`.
