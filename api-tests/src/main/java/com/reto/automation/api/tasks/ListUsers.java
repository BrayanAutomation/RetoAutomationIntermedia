package com.reto.automation.api.tasks;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.Tasks;
import net.serenitybdd.screenplay.rest.interactions.Get;

/**
 * 1 endpoint = 1 Task: GET /api/users.
 */
public class ListUsers implements Task {

    public static ListUsers request() {
        return Tasks.instrumented(ListUsers.class);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(Get.resource("/api/users"));
    }
}
