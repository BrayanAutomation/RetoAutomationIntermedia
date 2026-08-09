package com.reto.automation.api.stepsdefinitions;

import com.reto.automation.api.interactions.MeasureResponseTime;
import com.reto.automation.api.models.UserRequest;
import com.reto.automation.api.questions.TheMeasuredResponseTime;
import com.reto.automation.api.tasks.CreateUser;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static net.serenitybdd.screenplay.actors.OnStage.theActorInTheSpotlight;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Solo orquestación: delega toda la lógica en Tasks/Interactions/Questions.
 */
public class UsersLatencyStepDefinition {

    @When("se crea un usuario con nombre {string}, correo {string} y rol {string} midiendo el tiempo de respuesta")
    public void seCreaUnUsuarioMidiendoElTiempoDeRespuesta(String nombre, String correo, String rol) {
        theActorInTheSpotlight().attemptsTo(MeasureResponseTime.of(CreateUser.withData(UserRequest.builder()
                .name(nombre)
                .email(correo)
                .role(rol)
                .status("ACTIVE")
                .build())));
    }

    @Then("el tiempo de respuesta es de al menos {int} milisegundos")
    public void elTiempoDeRespuestaEsDeAlMenosMilisegundos(int minimoEsperadoMillis) {
        Long tiempoMedido = TheMeasuredResponseTime.inMillis().answeredBy(theActorInTheSpotlight());
        assertThat(tiempoMedido).isGreaterThanOrEqualTo(minimoEsperadoMillis);
    }
}
