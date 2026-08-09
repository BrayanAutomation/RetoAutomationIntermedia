package com.reto.automation.ui.userinterfaces;

import net.serenitybdd.screenplay.playwright.Target;

/**
 * Localizadores parametrizados de filas de la tabla de usuarios.
 * Uso: UserListUI.DELETE_BUTTON.of(userId).
 */
public class UserListUI {

    public static final Target ROW = Target.the("Fila del usuario {0}")
            .locatedBy("[data-testid='user-row-{0}']");

    public static final Target DELETE_BUTTON = Target.the("Botón eliminar usuario {0}")
            .locatedBy("[data-testid='delete-user-{0}']");
}
