package com.reto.automation.ui.stepsdefinitions;

import com.reto.automation.ui.questions.NoHorizontalOverflow;
import com.reto.automation.ui.tasks.OpenApp;
import com.reto.automation.ui.tasks.ResizeViewport;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static net.serenitybdd.screenplay.actors.OnStage.theActorInTheSpotlight;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Solo orquestación: delega toda la lógica en Tasks/Questions.
 * Cubre CA-UI-04 (criterio ambiguo) con las métricas concretas propuestas
 * por el equipo QA (ver responsiveness_ambiguous.feature).
 */
public class ResponsivenessStepDefinition {

    private long tiempoDeCargaMillis;

    @When("el candidato abre la aplicación web midiendo el tiempo de carga")
    public void elCandidatoAbreLaAplicacionWebMidiendoElTiempoDeCarga() {
        long inicio = System.currentTimeMillis();
        theActorInTheSpotlight().attemptsTo(OpenApp.home());
        tiempoDeCargaMillis = System.currentTimeMillis() - inicio;
    }

    @Then("la aplicación carga en menos de {int} milisegundos")
    public void laAplicacionCargaEnMenosDeMilisegundos(int umbralMillis) {
        assertThat(tiempoDeCargaMillis).isLessThan(umbralMillis);
    }

    @Given("que el candidato configura la ventana a un ancho de {int} píxeles")
    public void queElCandidatoConfiguraLaVentanaAUnAnchoDePixeles(int ancho) {
        theActorInTheSpotlight().attemptsTo(ResizeViewport.to(ancho, 800));
    }

    @When("abre la aplicación web")
    public void abreLaAplicacionWeb() {
        theActorInTheSpotlight().attemptsTo(OpenApp.home());
    }

    @Then("el listado de usuarios se visualiza sin desbordamiento horizontal")
    public void elListadoDeUsuariosSeVisualizaSinDesbordamientoHorizontal() {
        boolean sinDesborde = NoHorizontalOverflow.onTheCurrentViewport().answeredBy(theActorInTheSpotlight());
        assertThat(sinDesborde).isTrue();
    }
}
