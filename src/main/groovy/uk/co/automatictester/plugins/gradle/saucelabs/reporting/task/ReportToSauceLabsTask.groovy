package uk.co.automatictester.plugins.gradle.saucelabs.reporting.task

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import uk.co.automatictester.plugins.gradle.saucelabs.reporting.JUnitReportHandler
import uk.co.automatictester.plugins.gradle.saucelabs.reporting.JUnitReport
import uk.co.automatictester.plugins.gradle.saucelabs.reporting.SauceLabsJobResultReporter
import uk.co.automatictester.plugins.gradle.saucelabs.reporting.extension.SaucelabsReportingExtension

class ReportToSauceLabsTask extends DefaultTask {

    SaucelabsReportingExtension config

    @TaskAction
    void reportToSauceLabs() {
        config = project.extensions.findByType(SaucelabsReportingExtension)
        String testResultsDir = config.testResultsDir
        String filenamePattern = config.filenamePattern

        List<String> junitReportFiles = JUnitReportHandler.getJUnitReportFiles(testResultsDir, filenamePattern)
        processJUnitReports(junitReportFiles)
    }

    void processJUnitReports(List<String> junitReportFiles) {
        SauceLabsJobResultReporter sauceLabsJobResultReporter = new SauceLabsJobResultReporter(config)
        junitReportFiles.each { junitReportFile ->
            JUnitReport junitReport = new JUnitReport(junitReportFile)
            sauceLabsJobResultReporter.updateResult(junitReport)
        }
    }
}
