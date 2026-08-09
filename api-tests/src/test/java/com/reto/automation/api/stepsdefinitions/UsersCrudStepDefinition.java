package com.reto.automation.api.stepsdefinitions;

import com.reto.automation.api.models.UserRequest;
import com.reto.automation.api.models.UserResponse;
import com.reto.automation.api.questions.TheCreatedUser;
import com.reto.automation.api.questions.TheResponse;
import com.reto.automation.api.tasks.CreateUser;
import com.reto.automation.api.tasks.DeleteUser;
import com.reto.automation.api.tasks.GetUserById;
import com.reto.automation.api.tasks.ListUsers;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static net.serenitybdd.screenplay.actors.OnStage.theActorInTheSpotlight;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Solo orquestación: delega toda la lógica en Tasks/Questions.
 */
public class UsersCrudStepDefinition {

    @When("se crea un usuario con nombre {string}, correo {string} y rol {string}")
    public void seCreaUnUsuarioConNombreCorreoYRol(String nombre, String correo, String rol) {
        theActorInTheSpotlight().attemptsTo(CreateUser.withData(UserRequest.builder()
                .name(nombre)
                .email(correo)
                .role(rol)
                .status("ACTIVE")
                .build()));
    }

    @Then("el usuario es creado exitosamente")
    public void elUsuarioEsCreadoExitosamente() {
        theActorInTheSpotlight().should(TheResponse.hasStatusCode(201));
    }

    @And("el usuario creado conserva el nombre {string} y el correo {string}")
    public void elUsuarioCreadoConservaElNombreYElCorreo(String nombreEsperado, String correoEsperado) {
        UserResponse creado = TheCreatedUser.fromTheLastResponse().answeredBy(theActorInTheSpotlight());
        assertThat(creado.getName()).isEqualTo(nombreEsperado);
        assertThat(creado.getEmail()).isEqualTo(correoEsperado);
    }

    @When("se consulta el listado de usuarios")
    public void seConsultaElListadoDeUsuarios() {
        theActorInTheSpotlight().attemptsTo(ListUsers.request());
    }

    @Then("la respuesta contiene usuarios registrados")
    public void laRespuestaContieneUsuariosRegistrados() {
        theActorInTheSpotlight().should(TheResponse.bodyContainsUsers());
    }

    @When("se consulta el usuario con id {string}")
    public void seConsultaElUsuarioConId(String userId) {
        theActorInTheSpotlight().attemptsTo(GetUserById.withId(Long.parseLong(userId)));
    }

    @When("se elimina el usuario con id {string}")
    public void seEliminaElUsuarioConId(String userId) {
        theActorInTheSpotlight().attemptsTo(DeleteUser.withId(Long.parseLong(userId)));
    }

    @Then("el servicio responde que el usuario no fue encontrado")
    public void elServicioRespondeQueElUsuarioNoFueEncontrado() {
        theActorInTheSpotlight().should(TheResponse.hasStatusCode(404));
    }
}
