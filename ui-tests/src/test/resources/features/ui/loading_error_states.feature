@CA-UI-03 @CA-API-03
Feature: Feedback visual ante errores del backend

  Cuando el backend responde con el error intencional intermitente
  (aproximadamente el 25% de las solicitudes), la interfaz debe informar al
  usuario mediante un banner de error en lugar de fallar en silencio.

  @Negative
  Scenario: El candidato ve una alerta cuando el backend falla intermitentemente
    Given que el candidato está viendo la aplicación web
    When refresca el listado repetidamente hasta que el backend responda con un error intermitente
    Then la interfaz muestra una alerta de error visible para el usuario
