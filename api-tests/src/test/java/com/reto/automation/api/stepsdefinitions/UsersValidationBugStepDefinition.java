package com.reto.automation.api.stepsdefinitions;

import com.reto.automation.api.models.UserRequest;
import com.reto.automation.api.questions.TheResponse;
import com.reto.automation.api.tasks.CreateUser;
import com.reto.automation.api.tasks.UpdateUser;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static com.reto.automation.api.tasks.CreateUser.CREATED_USER_ID;
import static net.serenitybdd.screenplay.actors.OnStage.theActorInTheSpotlight;

/**
 * Solo orquestación: delega toda la lógica en Tasks/Questions.
 * Estos escenarios documentan el defecto BUG-API-002 (ver users_validation_bug.feature).
 */
public class UsersValidationBugStepDefinition {

    @And("existe un usuario registrado")
    public void existeUnUsuarioRegistrado() {
        theActorInTheSpotlight().attemptsTo(CreateUser.withData(UserRequest.builder()
                .name("Usuario Base")
                .email("usuario.base@example.com")
                .role("QA Automation")
                .status("ACTIVE")
                .build()));
    }

    @When("se actualiza el usuario con un correo inválido {string}")
    public void seActualizaElUsuarioConUnCorreoInvalido(String correoInvalido) {
        Long userId = theActorInTheSpotlight().recall(CREATED_USER_ID);
        theActorInTheSpotlight().attemptsTo(UpdateUser.withId(userId, UserRequest.builder()
                .name("Usuario Base")
                .email(correoInvalido)
                .role("QA Automation")
                .status("ACTIVE")
                .build()));
    }

    @When("se actualiza el usuario dejando el nombre vacío")
    public void seActualizaElUsuarioDejandoElNombreVacio() {
        Long userId = theActorInTheSpotlight().recall(CREATED_USER_ID);
        theActorInTheSpotlight().attemptsTo(UpdateUser.withId(userId, UserRequest.builder()
                .name("")
                .email("usuario.base@example.com")
                .role("QA Automation")
                .status("ACTIVE")
                .build()));
    }

    @Then("el servicio debería rechazar la actualización con un error {int}")
    public void elServicioDeberiaRechazarLaActualizacionConUnError(int expectedStatusCode) {
        theActorInTheSpotlight().should(TheResponse.hasStatusCode(expectedStatusCode));
    }
}
