@CA-API-04 @BUG-API-PERF
Feature: Latencia intencional al crear usuarios (bug intencional)

  El endpoint POST /api/users demora intencionalmente 2.5 segundos antes de
  responder, para evaluar el manejo de timeouts explícitos por parte del
  cliente/consumidor.

  @Negative
  Scenario: La creación de un usuario supera el tiempo de respuesta esperado
    Given el servicio API está disponible
    When se crea un usuario con nombre "Latencia Test", correo "latencia.test@example.com" y rol "QA Automation" midiendo el tiempo de respuesta
    Then el tiempo de respuesta es de al menos 2500 milisegundos
