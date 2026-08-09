package com.reto.automation.ui.interactions;

import com.microsoft.playwright.Page;
import lombok.AllArgsConstructor;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Interaction;
import net.serenitybdd.screenplay.Tasks;
import net.serenitybdd.screenplay.playwright.Target;
import net.serenitybdd.screenplay.playwright.abilities.BrowseTheWebWithPlaywright;

/**
 * Interacción reutilizable de bajo nivel: selecciona una opción de un
 * &lt;select&gt; por su texto visible usando la API nativa de Playwright.
 */
@AllArgsConstructor
public class SelectDropdownOption implements Interaction {

    private final Target dropdown;
    private final String visibleText;

    public static SelectDropdownOption withVisibleText(String visibleText, Target dropdown) {
        return Tasks.instrumented(SelectDropdownOption.class, dropdown, visibleText);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        Page page = BrowseTheWebWithPlaywright.as(actor).getCurrentPage();
        page.locator(dropdown.asSelector()).selectOption(visibleText);
    }
}
