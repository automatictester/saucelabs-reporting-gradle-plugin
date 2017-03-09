package uk.co.deliverymind.plugins.gradle.saucelabs.reporting.task

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import uk.co.deliverymind.plugins.gradle.saucelabs.reporting.JUnitReportHandler
import uk.co.deliverymind.plugins.gradle.saucelabs.reporting.JUnitTestReport
import uk.co.deliverymind.plugins.gradle.saucelabs.reporting.SessionHandler
import uk.co.deliverymind.plugins.gradle.saucelabs.reporting.extension.SaucelabsReportingExtension

// TODO: think about https://wiki.saucelabs.com/display/DOCS/Rate+Limits+for+the+Sauce+Labs+REST+API
// TODO: think about edge cases and exception handling
// TODO: add basic assumption to readme: one Sauce Labs session per test class

class ReportToSauceLabsTask extends DefaultTask {

    @TaskAction
    void reportToSauceLabs() {
        SaucelabsReportingExtension cfg = project.extensions.findByType(SaucelabsReportingExtension.class)
        List<String> junitReports = JUnitReportHandler.getJUnitReports(cfg.testResultsDir, cfg.filenamePattern)

        junitReports.each {
            JUnitTestReport testReport = new JUnitTestReport(it)
            new SessionHandler(cfg).updateSessionResult(testReport)
        }
    }
}
