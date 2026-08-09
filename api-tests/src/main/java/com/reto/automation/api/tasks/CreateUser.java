package com.reto.automation.api.tasks;

import com.reto.automation.api.models.UserRequest;
import lombok.AllArgsConstructor;
import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.Tasks;
import net.serenitybdd.screenplay.rest.interactions.Post;

/**
 * 1 endpoint = 1 Task: POST /api/users.
 * Recuerda el id del usuario creado (memoria del actor) para que otros
 * Tasks/Steps del mismo escenario (p. ej. UpdateUser) puedan reutilizarlo.
 */
@AllArgsConstructor
public class CreateUser implements Task {

    public static final String CREATED_USER_ID = "createdUserId";

    private final UserRequest newUser;

    public static CreateUser withData(UserRequest newUser) {
        return Tasks.instrumented(CreateUser.class, newUser);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(Post.to("/api/users").with(request -> request
                .header("Content-Type", "application/json")
                .body(newUser)));
        actor.remember(CREATED_USER_ID, SerenityRest.lastResponse().jsonPath().getLong("id"));
    }
}
