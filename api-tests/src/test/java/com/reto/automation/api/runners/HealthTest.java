package com.reto.automation.api.runners;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;

/**
 * Runner dedicado a health.feature (CA-API-01).
 * Sigue el mismo patrón que RegresionApiTest; si se necesita filtrar por
 * tag en una corrida puntual, se agrega igual que ahí:
 * {@code @ConfigurationParameter(key = Constants.FILTER_TAGS_PROPERTY_NAME, value = "@HappyPath")}
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/api/health.feature")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "com.reto.automation.api.stepsdefinitions")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "net.serenitybdd.cucumber.core.plugin.SerenityReporter")
public class HealthTest {
}
