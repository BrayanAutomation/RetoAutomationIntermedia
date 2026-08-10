package com.reto.automation.ui.runners;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;

/**
 * Runner usado por el pipeline de GitHub Actions (.github/workflows).
 * A diferencia de RegresionUiTest, NO fija ningún tag por anotación: el
 * tag a filtrar se decide en tiempo de ejecución vía la property de JVM
 * "cucumber.filter.tags" (reenviada desde -D por build.gradle). Sin esa
 * property, corre TODAS las features de UI sin filtrar.
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/ui")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "com.reto.automation.ui.stepsdefinitions")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "net.serenitybdd.cucumber.core.plugin.SerenityReporter")
public class CiUiTest {
}
