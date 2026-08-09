@CA-UI-04 @Ambiguous
Feature: Rendimiento y visualización responsiva (criterio ambiguo)

  # ADVERTENCIA DE AMBIGÜEDAD: CA-UI-04 exige que el sistema "responda de
  # manera rápida y razonable" y que la pantalla "se visualice bien y
  # acomodada en cualquier dispositivo sin demorar mucho tiempo", sin
  # definir umbrales medibles. Para poder automatizarlo, este equipo QA
  # propone las siguientes métricas concretas como interpretación razonable
  # del criterio (ver docs/context/api-reference-context.md y
  # REPORT_TEMPLATE.md §5 para la propuesta formal de refinamiento):
  #   - Carga inicial de la aplicación en menos de 3 segundos.
  #   - La tabla de usuarios permanece visible y sin desbordamiento horizontal
  #     en viewports de 375px (móvil), 768px (tablet) y 1280px (escritorio).

  @Edge
  Scenario: La aplicación carga dentro del umbral propuesto de 3 segundos
    When el candidato abre la aplicación web midiendo el tiempo de carga
    Then la aplicación carga en menos de 3000 milisegundos

  @DataVariation
  Scenario Outline: El listado de usuarios se visualiza correctamente en distintos tamaños de pantalla
    Given que el candidato configura la ventana a un ancho de <ancho> píxeles
    When abre la aplicación web
    Then el listado de usuarios se visualiza sin desbordamiento horizontal

    Examples:
      | ancho |
      | 375   |
      | 768   |
      | 1280  |
