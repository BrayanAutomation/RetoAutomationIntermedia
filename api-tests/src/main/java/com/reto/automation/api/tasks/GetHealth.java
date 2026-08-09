package com.reto.automation.api.tasks;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.Tasks;
import net.serenitybdd.screenplay.rest.interactions.Get;

/**
 * 1 endpoint = 1 Task: GET /health.
 */
public class GetHealth implements Task {

    public static GetHealth request() {
        return Tasks.instrumented(GetHealth.class);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(Get.resource("/health"));
    }
}
