package com.reto.automation.ui.questions;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.TimeoutError;
import lombok.AllArgsConstructor;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;

import static com.reto.automation.ui.userinterfaces.HomePageUI.USERS_TABLE;

@AllArgsConstructor
public class UserIsListed implements Question<Boolean> {

    /**
     * POST /api/users tiene una latencia intencional de 2.5s (CA-API-04)
     * antes de que la fila nueva pueda aparecer en la tabla; se espera
     * explícitamente en vez de comprobar el DOM de forma instantánea.
     * 15s de margen (no solo 2.5s + colchón corto) porque en entornos con
     * Chromium sin aceleración de hardware y overhead de red de contenedor
     * (Docker) el ciclo completo puede tardar bastante más que en una
     * corrida local nativa — verificado empíricamente: 6000ms alcanzaba
     * siempre en Windows nativo pero fallaba de forma consistente corriendo
     * dentro de docker-compose.tests.yml.
     */
    private static final double TIMEOUT_MILLIS = 15000;

    private final String email;

    public static UserIsListed byEmail(String email) {
        return new UserIsListed(email);
    }

    @Override
    public Boolean answeredBy(Actor actor) {
        Page page = BrowseTheWebWithPlaywright.as(actor).getCurrentPage();
        String selector = USERS_TABLE.asSelector() + " >> text=" + email;
        try {
            page.waitForSelector(selector, new Page.WaitForSelectorOptions().setTimeout(TIMEOUT_MILLIS));
            return true;
        } catch (TimeoutError timeout) {
            return false;
        }
    }
}
