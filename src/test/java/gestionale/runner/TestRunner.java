package gestionale.runner;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PUBLISH_QUIET_PROPERTY_NAME;

@Suite
@IncludeEngines("cucumber")                // i test li esegue Cucumber, non JUnit "normale"
@SelectClasspathResource("features")       // cartella src/test/resources/features
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "gestionale")   // dove cercare steps e hooks
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME,
        value = "pretty, html:target/cucumber-report.html, junit:target/cucumber-junit.xml")
@ConfigurationParameter(key = PLUGIN_PUBLISH_QUIET_PROPERTY_NAME, value = "true")  // niente banner pubblicitario
public class TestRunner {
    // Vuota: tutto è configurato dalle annotazioni
}