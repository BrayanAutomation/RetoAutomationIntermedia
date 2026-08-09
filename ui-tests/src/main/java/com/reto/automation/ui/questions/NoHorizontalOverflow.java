package com.reto.automation.ui.questions;

import com.microsoft.playwright.Page;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;

public class NoHorizontalOverflow implements Question<Boolean> {

    public static NoHorizontalOverflow onTheCurrentViewport() {
        return new NoHorizontalOverflow();
    }

    @Override
    public Boolean answeredBy(Actor actor) {
        Page page = BrowseTheWebWithPlaywright.as(actor).getCurrentPage();
        Object result = page.evaluate(
                "document.documentElement.scrollWidth <= document.documentElement.clientWidth + 1");
        return Boolean.TRUE.equals(result);
    }
}
