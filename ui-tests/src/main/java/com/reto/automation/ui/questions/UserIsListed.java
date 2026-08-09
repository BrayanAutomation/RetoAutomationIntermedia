package com.reto.automation.ui.questions;

import com.microsoft.playwright.Page;
import lombok.AllArgsConstructor;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;

import static com.reto.automation.ui.userinterfaces.HomePageUI.USERS_TABLE;

@AllArgsConstructor
public class UserIsListed implements Question<Boolean> {

    private final String email;

    public static UserIsListed byEmail(String email) {
        return new UserIsListed(email);
    }

    @Override
    public Boolean answeredBy(Actor actor) {
        Page page = BrowseTheWebWithPlaywright.as(actor).getCurrentPage();
        return page.locator(USERS_TABLE.asSelector() + " >> text=" + email).count() > 0;
    }
}
