@CA-UI-02 @BUG-UI-001
Feature: Registro de usuarios mediante localizadores estables

  El formulario de registro cambia dinámicamente los atributos id/htmlFor en
  cada render, por lo que la automatización debe apoyarse exclusivamente en
  atributos data-testid estables, nunca en id ni en htmlFor.

  @HappyPath
  Scenario: Registrar un nuevo usuario usando data-testid
    Given que el candidato está en el formulario de registro de usuarios
    When registra un usuario con nombre "Marco Silva", correo "marco.silva@example.com" y rol "QA Automation" usando los localizadores estables
    Then el nuevo usuario aparece en el listado

  @DataVariation
  Scenario Outline: Registrar usuarios con distintos roles usando data-testid
    Given que el candidato está en el formulario de registro de usuarios
    When registra un usuario con nombre "<nombre>", correo "<correo>" y rol "<rol>" usando los localizadores estables
    Then el nuevo usuario aparece en el listado

    Examples:
      | nombre         | correo                     | rol       |
      | Valeria Cortez | valeria.cortez@example.com | QA Manual |
      | Renzo Aguilar  | renzo.aguilar@example.com  | SDET      |
