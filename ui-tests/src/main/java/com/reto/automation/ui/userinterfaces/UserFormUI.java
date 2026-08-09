package com.reto.automation.ui.userinterfaces;

import net.serenitybdd.screenplay.playwright.Target;

/**
 * Localizadores del formulario de registro de usuarios, basados en
 * data-testid estables (CA-UI-02).
 */
public class UserFormUI {

    public static final Target NAME_INPUT = Target.the("Campo nombre")
            .locatedBy("[data-testid='user-name-input']");

    public static final Target EMAIL_INPUT = Target.the("Campo correo")
            .locatedBy("[data-testid='user-email-input']");

    public static final Target ROLE_SELECT = Target.the("Selector de rol")
            .locatedBy("[data-testid='user-role-select']");

    public static final Target SUBMIT_BTN = Target.the("Botón enviar")
            .locatedBy("[data-testid='submit-user-btn']");
}
