package com.reto.automation.api.questions;

import net.serenitybdd.screenplay.rest.questions.ResponseConsequence;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;

/**
 * Consequences de Screenplay-REST reutilizables para validar la última
 * respuesta HTTP del actor. Centraliza las aserciones para no duplicarlas
 * en cada step definition (motor de reutilización).
 */
public final class TheResponse {

    private TheResponse() {
    }

    public static ResponseConsequence hasStatusCode(int expectedStatusCode) {
        return ResponseConsequence.seeThatResponse("tiene el código de estado " + expectedStatusCode,
                response -> response.statusCode(expectedStatusCode));
    }

    public static ResponseConsequence isHealthy() {
        return ResponseConsequence.seeThatResponse("reporta el servicio activo", response -> response
                .statusCode(200)
                .body("status", equalTo("UP"))
                .body("service", equalTo("qa-api-challenges")));
    }

    public static ResponseConsequence bodyContainsUsers() {
        return ResponseConsequence.seeThatResponse("contiene una lista de usuarios", response -> response
                .statusCode(200)
                .body("$", not(empty())));
    }

    public static ResponseConsequence containsIntentionalBugMarker() {
        return ResponseConsequence.seeThatResponse("expone el marcador del bug intencional BUG-API-001", response -> response
                .statusCode(500)
                .body(containsString("BUG-API-001")));
    }
}
