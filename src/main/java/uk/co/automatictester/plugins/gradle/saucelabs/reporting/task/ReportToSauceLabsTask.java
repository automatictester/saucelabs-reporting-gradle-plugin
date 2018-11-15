package uk.co.automatictester.plugins.gradle.saucelabs.reporting.task;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import uk.co.automatictester.plugins.gradle.saucelabs.reporting.JunitReport;
import uk.co.automatictester.plugins.gradle.saucelabs.reporting.JunitReportHandler;
import uk.co.automatictester.plugins.gradle.saucelabs.reporting.SauceLabsJobResultReporter;
import uk.co.automatictester.plugins.gradle.saucelabs.reporting.extension.SaucelabsReportingExtension;

import java.util.List;

public class ReportToSauceLabsTask extends DefaultTask {

    SaucelabsReportingExtension config;

    @TaskAction
    public void reportToSauceLabs() {
        config = getProject().getExtensions().findByType(SaucelabsReportingExtension.class);
        String testResultsDir = config.testResultsDir;
        String filenamePattern = config.filenamePattern;

        List<String> junitReportFiles = JunitReportHandler.getJunitFiles(testResultsDir, filenamePattern);
        processJUnitReports(junitReportFiles);
    }

    private void processJUnitReports(List<String> junitReportFiles) {
        SauceLabsJobResultReporter sauceLabsJobResultReporter = new SauceLabsJobResultReporter(config);
        junitReportFiles.forEach(junitReportFile -> {
            JunitReport junitReport = new JunitReport(junitReportFile);
            sauceLabsJobResultReporter.updateResult(junitReport);
        });
    }
}
