package com.reto.automation.api.stepsdefinitions;

import com.reto.automation.api.interactions.RepeatRequest;
import com.reto.automation.api.questions.AtLeastOneCallFailedWithServerError;
import com.reto.automation.api.tasks.ListUsers;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static net.serenitybdd.screenplay.actors.OnStage.theActorInTheSpotlight;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Solo orquestación: delega toda la lógica en Tasks/Interactions/Questions.
 */
public class UsersIntermittentErrorStepDefinition {

    @When("se consulta el listado de usuarios de forma repetida {int} veces")
    public void seConsultaElListadoDeUsuariosDeFormaRepetidaNVeces(int veces) {
        theActorInTheSpotlight().attemptsTo(RepeatRequest.of(ListUsers.request(), veces));
    }

    @Then("al menos una de las respuestas fue un error 500 por fallo de conexión intencional")
    public void alMenosUnaDeLasRespuestasFueUnError500PorFalloDeConexionIntencional() {
        boolean algunaFallo = AtLeastOneCallFailedWithServerError.amongTheRepeatedCalls()
                .answeredBy(theActorInTheSpotlight());
        assertThat(algunaFallo).isTrue();
    }
}
