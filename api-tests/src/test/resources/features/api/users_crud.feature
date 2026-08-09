@CA-API-02
Feature: Gestión de usuarios (CRUD)

  Como equipo de QA
  quiero validar las operaciones CRUD de /api/users
  para asegurar la correcta gestión de candidatos/usuarios.

  @HappyPath
  Scenario: Crear un nuevo usuario con datos válidos
    Given el servicio API está disponible
    When se crea un usuario con nombre "Ana Torres", correo "ana.torres@example.com" y rol "QA Automation"
    Then el usuario es creado exitosamente
    And el usuario creado conserva el nombre "Ana Torres" y el correo "ana.torres@example.com"

  @HappyPath
  Scenario: Consultar el listado de usuarios existentes
    Given el servicio API está disponible
    When se consulta el listado de usuarios
    Then la respuesta contiene usuarios registrados

  @Negative
  Scenario: Consultar un usuario que no existe
    Given el servicio API está disponible
    When se consulta el usuario con id "999999"
    Then el servicio responde que el usuario no fue encontrado

  @Edge
  Scenario: Eliminar un usuario que no existe
    Given el servicio API está disponible
    When se elimina el usuario con id "999999"
    Then el servicio responde que el usuario no fue encontrado

  @DataVariation
  Scenario Outline: Crear usuarios con distintos roles válidos
    Given el servicio API está disponible
    When se crea un usuario con nombre "<nombre>", correo "<correo>" y rol "<rol>"
    Then el usuario es creado exitosamente

    Examples:
      | nombre        | correo                    | rol       |
      | Carla Ruiz    | carla.ruiz@example.com    | QA Manual |
      | Diego Paredes | diego.paredes@example.com | SDET      |
      | Julia Nuñez   | julia.nunez@example.com   | DevOps    |
