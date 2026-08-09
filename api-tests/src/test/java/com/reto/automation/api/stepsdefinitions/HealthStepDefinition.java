package com.reto.automation.api.stepsdefinitions;

import com.reto.automation.api.questions.TheResponse;
import com.reto.automation.api.tasks.GetHealth;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static net.serenitybdd.screenplay.actors.OnStage.theActorInTheSpotlight;

/**
 * Solo orquestación: delega toda la lógica en Tasks/Questions.
 */
public class HealthStepDefinition {

    @Given("el servicio API está disponible")
    public void elServicioApiEstaDisponible() {
        // El servicio se asume disponible; la comprobación efectiva ocurre en los pasos When/Then de cada escenario.
    }

    @When("se consulta el estado de salud del servicio")
    public void seConsultaElEstadoDeSaludDelServicio() {
        theActorInTheSpotlight().attemptsTo(GetHealth.request());
    }

    @Then("el servicio responde que está activo")
    public void elServicioRespondeQueEstaActivo() {
        theActorInTheSpotlight().should(TheResponse.isHealthy());
    }
}
