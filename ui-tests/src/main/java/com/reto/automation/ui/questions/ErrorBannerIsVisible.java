package com.reto.automation.ui.questions;

import com.microsoft.playwright.Page;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;

import static com.reto.automation.ui.userinterfaces.HomePageUI.ERROR_ALERT;

public class ErrorBannerIsVisible implements Question<Boolean> {

    public static ErrorBannerIsVisible onScreen() {
        return new ErrorBannerIsVisible();
    }

    @Override
    public Boolean answeredBy(Actor actor) {
        Page page = BrowseTheWebWithPlaywright.as(actor).getCurrentPage();
        return page.locator(ERROR_ALERT.asSelector()).isVisible();
    }
}
