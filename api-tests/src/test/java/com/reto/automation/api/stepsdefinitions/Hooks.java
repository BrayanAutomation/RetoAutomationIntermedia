package com.reto.automation.api.stepsdefinitions;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import net.serenitybdd.screenplay.actors.OnlineCast;
import net.serenitybdd.screenplay.rest.abilities.CallAnApi;

import static com.reto.automation.api.utils.ApiConstants.BASE_URL;
import static net.serenitybdd.screenplay.actors.OnStage.drawTheCurtain;
import static net.serenitybdd.screenplay.actors.OnStage.setTheStage;
import static net.serenitybdd.screenplay.actors.OnStage.theActorCalled;

/**
 * Prepara el reparto (Cast) Screenplay antes de cada escenario y otorga al
 * actor la habilidad de llamar al API una única vez (a diferencia del
 * proyecto de referencia, que la reasignaba en cada llamada).
 */
public class Hooks {

    @Before
    public void setStage() {
        setTheStage(new OnlineCast());
        theActorCalled("Usuario").can(CallAnApi.at(BASE_URL));
    }

    @After
    public void tearDown() {
        drawTheCurtain();
    }
}
