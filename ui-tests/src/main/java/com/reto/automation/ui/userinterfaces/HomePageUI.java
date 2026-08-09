package com.reto.automation.ui.userinterfaces;

import net.serenitybdd.screenplay.playwright.Target;

/**
 * Localizadores de la página principal (listado de usuarios), siempre
 * basados en atributos data-testid estables (ver CA-UI-02: id/htmlFor
 * cambian dinámicamente en cada render y NUNCA deben usarse como locator).
 */
public class HomePageUI {

    public static final Target USERS_TABLE = Target.the("Tabla de usuarios")
            .locatedBy("[data-testid='users-table']");

    public static final Target REFRESH_BTN = Target.the("Botón refrescar")
            .locatedBy("[data-testid='refresh-btn']");

    public static final Target ERROR_ALERT = Target.the("Banner de error")
            .locatedBy("[data-testid='error-alert']");
}
