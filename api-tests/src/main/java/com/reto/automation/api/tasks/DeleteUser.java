package com.reto.automation.api.tasks;

import lombok.AllArgsConstructor;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.Tasks;
import net.serenitybdd.screenplay.rest.interactions.Delete;

/**
 * 1 endpoint = 1 Task: DELETE /api/users/{id}.
 */
@AllArgsConstructor
public class DeleteUser implements Task {

    private final long userId;

    public static DeleteUser withId(long userId) {
        return Tasks.instrumented(DeleteUser.class, userId);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(Delete.from("/api/users/" + userId));
    }
}
