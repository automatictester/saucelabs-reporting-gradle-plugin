package uk.co.deliverymind.plugins.gradle.saucelabs.reporting.task

import com.saucelabs.saucerest.SauceREST
import groovy.io.FileType
import groovy.json.JsonSlurper
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import uk.co.deliverymind.plugins.gradle.saucelabs.reporting.JUnitTestReport
import uk.co.deliverymind.plugins.gradle.saucelabs.reporting.extension.SaucelabsReportingExtension

// TODO: think about https://wiki.saucelabs.com/display/DOCS/Rate+Limits+for+the+Sauce+Labs+REST+API
// TODO: think about edge cases and exception handling
// TODO: add basic assumption to readme: one Sauce Labs session per test class

class ReportToSauceLabsTask extends DefaultTask {

    SaucelabsReportingExtension extension = project.extensions.findByType(SaucelabsReportingExtension.class)

    @TaskAction
    void reportToSauceLabs() {
        List<String> files = getAllFiles(extension.testResultsDir)
        List<String> junitReportFiles = filterFiles(files, extension.filenamePattern)

        junitReportFiles.each {
            JUnitTestReport testReport = new JUnitTestReport(it)
            updateJobResult(testReport)
        }
    }

    void updateJobResult(JUnitTestReport testReport) {
        HashMap<String, Boolean> updates = new HashMap<String, Boolean>()
        updates.put('passed', testReport.passed)
        testReport.log()
        SauceREST restClient = new SauceREST(extension.user, extension.key)
        restClient.updateJobInfo(testReport.sessionId, updates)
        checkJobResult(testReport.sessionId, testReport.passed)
    }

    void checkJobResult(String sessionId, Boolean expectedSessionResult) {
        SauceREST restClient = new SauceREST(extension.user, extension.key)
        String sessionInfo = restClient.getJobInfo(sessionId)
        Boolean sessionResult = new JsonSlurper().parseText(sessionInfo).passed
        if (sessionResult != expectedSessionResult) {
            // TODO: implement error/warning/ignore
        }
    }

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
