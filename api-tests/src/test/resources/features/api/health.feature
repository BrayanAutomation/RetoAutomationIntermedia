@CA-API-01
Feature: Disponibilidad del servicio API

  Como equipo de QA
  quiero verificar el health check del backend
  para confirmar que el servicio está disponible antes de ejecutar el resto de pruebas.

  @HappyPath
  Scenario: El servicio reporta que está activo
    Given el servicio API está disponible
    When se consulta el estado de salud del servicio
    Then el servicio responde que está activo
