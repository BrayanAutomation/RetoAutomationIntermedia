package com.reto.automation.ui.tasks;

import com.reto.automation.ui.interactions.SelectDropdownOption;
import com.reto.automation.ui.models.NewUser;
import com.reto.automation.ui.userinterfaces.UserFormUI;
import lombok.AllArgsConstructor;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.Tasks;
import net.serenitybdd.screenplay.playwright.interactions.Enter;

@AllArgsConstructor
public class FillUserForm implements Task {

    private final NewUser newUser;

    public static FillUserForm withData(NewUser newUser) {
        return Tasks.instrumented(FillUserForm.class, newUser);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(
                Enter.theValue(newUser.getName()).into(UserFormUI.NAME_INPUT),
                Enter.theValue(newUser.getEmail()).into(UserFormUI.EMAIL_INPUT),
                SelectDropdownOption.withVisibleText(newUser.getRole(), UserFormUI.ROLE_SELECT)
        );
    }
}
