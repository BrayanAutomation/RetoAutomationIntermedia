package com.reto.automation.api.tasks;

import lombok.AllArgsConstructor;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.Tasks;
import net.serenitybdd.screenplay.rest.interactions.Get;

/**
 * 1 endpoint = 1 Task: GET /api/users/{id}.
 */
@AllArgsConstructor
public class GetUserById implements Task {

    private final long userId;

    public static GetUserById withId(long userId) {
        return Tasks.instrumented(GetUserById.class, userId);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(Get.resource("/api/users/" + userId));
    }
}
