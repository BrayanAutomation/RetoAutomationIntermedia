package com.reto.automation.api.interactions;

import lombok.AllArgsConstructor;
import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Interaction;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.Tasks;

import java.util.ArrayList;
import java.util.List;

/**
 * Interacción reutilizable de bajo nivel: repite un Performable N veces y
 * guarda en la memoria del actor la lista de códigos de estado obtenidos.
 * Soporta la validación estadística de fallos intermitentes (CA-API-03).
 */
@AllArgsConstructor
public class RepeatRequest implements Interaction {

    public static final String MEMORY_KEY = "statusCodesFromRepeatedCalls";

    private final Performable request;
    private final int times;

    public static RepeatRequest of(Performable request, int times) {
        return Tasks.instrumented(RepeatRequest.class, request, times);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        List<Integer> statusCodes = new ArrayList<>();
        for (int i = 0; i < times; i++) {
            actor.attemptsTo(request);
            statusCodes.add(SerenityRest.lastResponse().statusCode());
        }
        actor.remember(MEMORY_KEY, statusCodes);
    }
}
