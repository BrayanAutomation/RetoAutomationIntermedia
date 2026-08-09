package com.reto.automation.ui.tasks;

import com.reto.automation.ui.userinterfaces.UserFormUI;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.Tasks;
import net.serenitybdd.screenplay.playwright.interactions.Click;

public class SubmitUserForm implements Task {

    public static SubmitUserForm now() {
        return Tasks.instrumented(SubmitUserForm.class);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(Click.on(UserFormUI.SUBMIT_BTN));
    }
}
