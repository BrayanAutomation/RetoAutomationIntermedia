package com.reto.automation.api.questions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;

import java.util.List;

import static com.reto.automation.api.interactions.RepeatRequest.MEMORY_KEY;

/**
 * Evalúa, sobre los códigos de estado guardados por
 * {@link com.reto.automation.api.interactions.RepeatRequest}, si al menos
 * una de las respuestas fue un 500 (falla intermitente intencional CA-API-03).
 */
public class AtLeastOneCallFailedWithServerError implements Question<Boolean> {

    public static AtLeastOneCallFailedWithServerError amongTheRepeatedCalls() {
        return new AtLeastOneCallFailedWithServerError();
    }

    @Override
    public Boolean answeredBy(Actor actor) {
        List<Integer> statusCodes = actor.recall(MEMORY_KEY);
        return statusCodes.stream().anyMatch(code -> code == 500);
    }
}
