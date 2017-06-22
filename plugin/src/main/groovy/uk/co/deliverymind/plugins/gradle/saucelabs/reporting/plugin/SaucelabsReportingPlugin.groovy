package uk.co.deliverymind.plugins.gradle.saucelabs.reporting.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import uk.co.deliverymind.plugins.gradle.saucelabs.reporting.extension.SaucelabsReportingExtension
import uk.co.deliverymind.plugins.gradle.saucelabs.reporting.task.ReportToSauceLabsTask

class SaucelabsReportingPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.extensions.create('saucelabsReportingSettings', SaucelabsReportingExtension)
        project.tasks.create('reportToSauceLabs', ReportToSauceLabsTask) {
            description = 'Report test results to Sauce Labs'
        }
    }
}
