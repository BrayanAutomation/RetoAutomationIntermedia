package com.reto.automation.ui.stepsdefinitions;

import com.reto.automation.ui.questions.TableIsVisible;
import com.reto.automation.ui.tasks.OpenApp;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;

import static net.serenitybdd.screenplay.actors.OnStage.theActorInTheSpotlight;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Solo orquestación: delega toda la lógica en Tasks/Questions.
 */
public class AppLoadStepDefinition {

    @Given("que el candidato abre la aplicación web")
    public void queElCandidatoAbreLaAplicacionWeb() {
        theActorInTheSpotlight().attemptsTo(OpenApp.home());
    }

    @Then("visualiza el listado de usuarios registrados")
    public void visualizaElListadoDeUsuariosRegistrados() {
        boolean tablaVisible = TableIsVisible.onScreen().answeredBy(theActorInTheSpotlight());
        assertThat(tablaVisible).isTrue();
    }
}
