package com.reto.automation.api.tasks;

import com.reto.automation.api.models.UserRequest;
import lombok.AllArgsConstructor;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.Tasks;
import net.serenitybdd.screenplay.rest.interactions.Put;

/**
 * 1 endpoint = 1 Task: PUT /api/users/{id}.
 */
@AllArgsConstructor
public class UpdateUser implements Task {

    private final long userId;
    private final UserRequest updatedUser;

    public static UpdateUser withId(long userId, UserRequest updatedUser) {
        return Tasks.instrumented(UpdateUser.class, userId, updatedUser);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(Put.to("/api/users/" + userId).with(request -> request
                .header("Content-Type", "application/json")
                .body(updatedUser)));
    }
}
