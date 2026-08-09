package com.reto.automation.ui.stepsdefinitions;

import com.reto.automation.ui.questions.ErrorBannerIsVisible;
import com.reto.automation.ui.tasks.OpenApp;
import com.reto.automation.ui.tasks.RefreshUserList;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static net.serenitybdd.screenplay.actors.OnStage.theActorInTheSpotlight;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Solo orquestación: delega toda la lógica en Tasks/Questions.
 */
public class LoadingErrorStatesStepDefinition {

    private boolean errorObservado;

    @Given("que el candidato está viendo la aplicación web")
    public void queElCandidatoEstaViendoLaAplicacionWeb() {
        theActorInTheSpotlight().attemptsTo(OpenApp.home());
    }

    @When("refresca el listado repetidamente hasta que el backend responda con un error intermitente")
    public void refrescaElListadoRepetidamenteHastaQueElBackendRespondaConUnErrorIntermitente() {
        errorObservado = false;
        int intentos = 20;
        for (int i = 0; i < intentos && !errorObservado; i++) {
            theActorInTheSpotlight().attemptsTo(RefreshUserList.triggered());
            errorObservado = ErrorBannerIsVisible.onScreen().answeredBy(theActorInTheSpotlight());
        }
    }

    @Then("la interfaz muestra una alerta de error visible para el usuario")
    public void laInterfazMuestraUnaAlertaDeErrorVisibleParaElUsuario() {
        assertThat(errorObservado).isTrue();
    }
}
