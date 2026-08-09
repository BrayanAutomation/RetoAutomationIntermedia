package com.reto.automation.api.questions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;

import static com.reto.automation.api.interactions.MeasureResponseTime.MEMORY_KEY;

/**
 * Recupera de la memoria del actor el tiempo (ms) de la última petición
 * medida con {@link com.reto.automation.api.interactions.MeasureResponseTime}.
 */
public class TheMeasuredResponseTime implements Question<Long> {

    public static TheMeasuredResponseTime inMillis() {
        return new TheMeasuredResponseTime();
    }

    @Override
    public Long answeredBy(Actor actor) {
        return actor.recall(MEMORY_KEY);
    }
}
