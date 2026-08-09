package com.reto.automation.ui.tasks;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.Tasks;
import net.serenitybdd.screenplay.playwright.interactions.Open;

import static com.reto.automation.ui.utils.UiConstants.BASE_URL;

public class OpenApp implements Task {

    public static OpenApp home() {
        return Tasks.instrumented(OpenApp.class);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(Open.url(BASE_URL));
    }
}
