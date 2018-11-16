package uk.co.automatictester.plugins.gradle.saucelabs.reporting.task;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import uk.co.automatictester.plugins.gradle.saucelabs.reporting.extension.SaucelabsReportingExtension;
import uk.co.automatictester.plugins.gradle.saucelabs.reporting.junit.JunitProcessor;

public class ReportToSauceLabsTask extends DefaultTask {

    @TaskAction
    public void reportToSauceLabs() {
        SaucelabsReportingExtension config = getProject()
                .getExtensions()
                .findByType(SaucelabsReportingExtension.class);
        JunitProcessor.process(config);
    }
}
