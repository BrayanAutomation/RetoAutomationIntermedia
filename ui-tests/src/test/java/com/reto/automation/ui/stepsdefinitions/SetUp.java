package com.reto.automation.ui.stepsdefinitions;

import com.microsoft.playwright.BrowserType;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import net.serenitybdd.screenplay.actors.Cast;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;

import static net.serenitybdd.screenplay.actors.OnStage.drawTheCurtain;
import static net.serenitybdd.screenplay.actors.OnStage.setTheStage;
import static net.serenitybdd.screenplay.actors.OnStage.theActorCalled;

/**
 * Prepara el escenario (Cast) Screenplay + Playwright antes de cada
 * escenario. El navegador y el modo headless son parametrizables vía
 * propiedades de sistema (-Dplaywright.browser, -Dplaywright.headless).
 */
public class SetUp {

    @Before
    public void setStage() {
        String browser = System.getProperty("playwright.browser", "chromium");
        boolean headless = Boolean.parseBoolean(System.getProperty("playwright.headless", "false"));

        BrowserType.LaunchOptions launchOptions = new BrowserType.LaunchOptions()
                .setHeadless(headless);

        setTheStage(Cast.whereEveryoneCan(BrowseTheWebWithPlaywright
                .withOptions(launchOptions)
                .withBrowserType(browser)));

        theActorCalled("Candidato QA");
    }

    @After
    public void tearDown() {
        drawTheCurtain();
    }
}
