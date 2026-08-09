package com.reto.automation.api.interactions;

import lombok.AllArgsConstructor;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Interaction;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.Tasks;

/**
 * Interacción reutilizable de bajo nivel: ejecuta cualquier Performable y
 * guarda en la memoria del actor el tiempo transcurrido (ms), para poder
 * validar reglas de latencia (p. ej. CA-API-04) desde cualquier Task.
 */
@AllArgsConstructor
public class MeasureResponseTime implements Interaction {

    public static final String MEMORY_KEY = "responseTimeMillis";

    private final Performable request;

    public static MeasureResponseTime of(Performable request) {
        return Tasks.instrumented(MeasureResponseTime.class, request);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        long start = System.currentTimeMillis();
        actor.attemptsTo(request);
        actor.remember(MEMORY_KEY, System.currentTimeMillis() - start);
    }
}
