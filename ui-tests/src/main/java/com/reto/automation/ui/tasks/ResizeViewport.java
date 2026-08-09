package com.reto.automation.ui.tasks;

import lombok.AllArgsConstructor;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.Tasks;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;

@AllArgsConstructor
public class ResizeViewport implements Task {

    private final int width;
    private final int height;

    public static ResizeViewport to(int width, int height) {
        return Tasks.instrumented(ResizeViewport.class, width, height);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        BrowseTheWebWithPlaywright.as(actor).getCurrentPage().setViewportSize(width, height);
    }
}
