package com.reto.automation.ui.questions;

import com.microsoft.playwright.Page;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;

import static com.reto.automation.ui.userinterfaces.HomePageUI.USERS_TABLE;

public class TableIsVisible implements Question<Boolean> {

    public static TableIsVisible onScreen() {
        return new TableIsVisible();
    }

    @Override
    public Boolean answeredBy(Actor actor) {
        Page page = BrowseTheWebWithPlaywright.as(actor).getCurrentPage();
        return page.locator(USERS_TABLE.asSelector()).isVisible();
    }
}
