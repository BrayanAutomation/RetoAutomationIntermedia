package com.reto.automation.api.runners;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;

/**
 * Runner dedicado a users_intermittent_error.feature (CA-API-03, BUG-API-001).
 * Sigue el mismo patrón que RegresionApiTest.
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/api/users_intermittent_error.feature")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "com.reto.automation.api.stepsdefinitions")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "net.serenitybdd.cucumber.core.plugin.SerenityReporter")
public class UsersIntermittentErrorTest {
}
