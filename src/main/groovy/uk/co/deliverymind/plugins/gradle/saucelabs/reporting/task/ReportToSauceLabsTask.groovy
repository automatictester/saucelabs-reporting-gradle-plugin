package uk.co.deliverymind.plugins.gradle.saucelabs.reporting.task

import groovy.io.FileType
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import uk.co.deliverymind.plugins.gradle.saucelabs.reporting.JUnitTestReport
import uk.co.deliverymind.plugins.gradle.saucelabs.reporting.SessionHandler
import uk.co.deliverymind.plugins.gradle.saucelabs.reporting.extension.SaucelabsReportingExtension

// TODO: think about https://wiki.saucelabs.com/display/DOCS/Rate+Limits+for+the+Sauce+Labs+REST+API
// TODO: think about edge cases and exception handling
// TODO: add basic assumption to readme: one Sauce Labs session per test class

class ReportToSauceLabsTask extends DefaultTask {

    SaucelabsReportingExtension cfg = project.extensions.findByType(SaucelabsReportingExtension.class)

    @TaskAction
    void reportToSauceLabs() {
        List<String> files = getAllFiles(cfg.testResultsDir)
        List<String> junitReportFiles = filterFiles(files, cfg.filenamePattern)

        junitReportFiles.each {
            JUnitTestReport testReport = new JUnitTestReport(it)
            new SessionHandler(cfg).updateSessionResult(testReport)
        }
    }

    // TODO: extract to FileHandler class
    static List<String> getAllFiles(String directory) {
        def files = []
        File dir = new File(directory)
        dir.eachFileRecurse(FileType.FILES) { file ->
            files << file.path
        }
        files
    }

    static List<String> filterFiles(List<String> files, String pattern) {
        files.findAll { it =~ pattern }
    }
}
