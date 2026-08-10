package com.reto.automation.ui.runners;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;

/**
 * Runner dedicado a user_form_dynamic_locators.feature (CA-UI-02, BUG-UI-001).
 * Sigue el mismo patrón que RegresionUiTest.
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/ui/user_form_dynamic_locators.feature")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "com.reto.automation.ui.stepsdefinitions")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "net.serenitybdd.cucumber.core.plugin.SerenityReporter")
public class UserFormDynamicLocatorsTest {
}
