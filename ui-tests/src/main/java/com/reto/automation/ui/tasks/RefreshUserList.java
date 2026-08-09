package com.reto.automation.ui.tasks;

import com.reto.automation.ui.userinterfaces.HomePageUI;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.Tasks;
import net.serenitybdd.screenplay.playwright.interactions.Click;

public class RefreshUserList implements Task {

    public static RefreshUserList triggered() {
        return Tasks.instrumented(RefreshUserList.class);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(Click.on(HomePageUI.REFRESH_BTN));
    }
}
