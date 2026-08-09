@CA-UI-01
Feature: Carga de la interfaz web

  Como candidato QA
  quiero que la aplicación cargue y muestre los usuarios existentes
  para confirmar que el frontend está correctamente integrado con el backend.

  @HappyPath
  Scenario: El candidato visualiza el listado de usuarios al cargar la aplicación
    Given que el candidato abre la aplicación web
    Then visualiza el listado de usuarios registrados
