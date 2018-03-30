package uk.co.automatictester.plugins.gradle.saucelabs.reporting.task

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import uk.co.automatictester.plugins.gradle.saucelabs.reporting.JUnitReportHandler
import uk.co.automatictester.plugins.gradle.saucelabs.reporting.JUnitTestReport
import uk.co.automatictester.plugins.gradle.saucelabs.reporting.SessionHandler
import uk.co.automatictester.plugins.gradle.saucelabs.reporting.extension.SaucelabsReportingExtension

class ReportToSauceLabsTask extends DefaultTask {

    @TaskAction
    void reportToSauceLabs() {
        SaucelabsReportingExtension cfg = project.extensions.findByType(SaucelabsReportingExtension)
        List<String> junitReports = JUnitReportHandler.getJUnitReports(cfg.testResultsDir, cfg.filenamePattern)

        junitReports.each {
            JUnitTestReport testReport = new JUnitTestReport(it)
            new SessionHandler(cfg).updateSessionResult(testReport)
        }
    }
}
