@CA-API-03 @BUG-API-001
Feature: Resiliencia ante fallas intermitentes de conexión (bug intencional)

  El endpoint GET /api/users falla intencionalmente en aproximadamente el 25%
  de las solicitudes con un error 500 que simula la pérdida de la conexión a
  la base de datos. Este escenario documenta y evidencia ese comportamiento.

  @Negative
  Scenario: El listado de usuarios falla intermitentemente al repetir la consulta
    Given el servicio API está disponible
    When se consulta el listado de usuarios de forma repetida 20 veces
    Then al menos una de las respuestas fue un error 500 por fallo de conexión intencional
