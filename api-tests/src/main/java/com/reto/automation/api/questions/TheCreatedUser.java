package com.reto.automation.api.questions;

import com.reto.automation.api.models.UserResponse;
import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;

/**
 * Deserializa la última respuesta HTTP como UserResponse, para poder
 * inspeccionar campos concretos del usuario creado/actualizado.
 */
public class TheCreatedUser implements Question<UserResponse> {

    public static TheCreatedUser fromTheLastResponse() {
        return new TheCreatedUser();
    }

    @Override
    public UserResponse answeredBy(Actor actor) {
        return SerenityRest.lastResponse().as(UserResponse.class);
    }
}
