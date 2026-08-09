package com.reto.automation.ui.stepsdefinitions;

import com.reto.automation.ui.models.NewUser;
import com.reto.automation.ui.questions.UserIsListed;
import com.reto.automation.ui.tasks.FillUserForm;
import com.reto.automation.ui.tasks.OpenApp;
import com.reto.automation.ui.tasks.SubmitUserForm;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static net.serenitybdd.screenplay.actors.OnStage.theActorInTheSpotlight;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Solo orquestación: delega toda la lógica en Tasks/Questions.
 */
public class UserFormStepDefinition {

    private String correoRegistrado;

    @Given("que el candidato está en el formulario de registro de usuarios")
    public void queElCandidatoEstaEnElFormularioDeRegistroDeUsuarios() {
        theActorInTheSpotlight().attemptsTo(OpenApp.home());
    }

    @When("registra un usuario con nombre {string}, correo {string} y rol {string} usando los localizadores estables")
    public void registraUnUsuarioUsandoLosLocalizadoresEstables(String nombre, String correo, String rol) {
        this.correoRegistrado = correo;
        theActorInTheSpotlight().attemptsTo(
                FillUserForm.withData(new NewUser(nombre, correo, rol)),
                SubmitUserForm.now()
        );
    }

    @Then("el nuevo usuario aparece en el listado")
    public void elNuevoUsuarioApareceEnElListado() {
        boolean listado = UserIsListed.byEmail(correoRegistrado).answeredBy(theActorInTheSpotlight());
        assertThat(listado).isTrue();
    }
}
