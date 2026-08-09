@CA-API-05 @BUG-API-002
Feature: Validación de datos al actualizar usuarios (bug intencional)

  El endpoint PUT /api/users/{id} debería rechazar datos inválidos con un
  400 Bad Request, pero actualmente los procesa y guarda devolviendo 200 OK.
  Estos escenarios documentan el defecto esperando el comportamiento correcto
  esperado por negocio; su fallo contra el sistema real evidencia el bug.

  @Negative @KnownDefect
  Scenario: Actualizar un usuario con un correo inválido debería ser rechazado
    Given el servicio API está disponible
    And existe un usuario registrado
    When se actualiza el usuario con un correo inválido "correo-invalido"
    Then el servicio debería rechazar la actualización con un error 400

  @Negative @KnownDefect
  Scenario: Actualizar un usuario dejando el nombre vacío debería ser rechazado
    Given el servicio API está disponible
    And existe un usuario registrado
    When se actualiza el usuario dejando el nombre vacío
    Then el servicio debería rechazar la actualización con un error 400
