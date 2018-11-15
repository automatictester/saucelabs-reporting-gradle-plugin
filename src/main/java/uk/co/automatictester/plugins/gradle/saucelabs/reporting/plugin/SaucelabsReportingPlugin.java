package uk.co.automatictester.plugins.gradle.saucelabs.reporting.plugin;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import uk.co.automatictester.plugins.gradle.saucelabs.reporting.extension.SaucelabsReportingExtension;
import uk.co.automatictester.plugins.gradle.saucelabs.reporting.task.ReportToSauceLabsTask;

public class SaucelabsReportingPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        project.getExtensions()
                .create("saucelabsReportingSettings", SaucelabsReportingExtension.class);
        project.getTasks()
                .create("reportToSauceLabs", ReportToSauceLabsTask.class)
                .setDescription("Report test results to Sauce Labs");
    }
}
