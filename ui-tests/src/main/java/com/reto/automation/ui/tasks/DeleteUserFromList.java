package com.reto.automation.ui.tasks;

import com.reto.automation.ui.userinterfaces.UserListUI;
import lombok.AllArgsConstructor;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.Tasks;
import net.serenitybdd.screenplay.playwright.interactions.Click;

@AllArgsConstructor
public class DeleteUserFromList implements Task {

    private final String userId;

    public static DeleteUserFromList withId(String userId) {
        return Tasks.instrumented(DeleteUserFromList.class, userId);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(Click.on(UserListUI.DELETE_BUTTON.of(userId)));
    }
}
