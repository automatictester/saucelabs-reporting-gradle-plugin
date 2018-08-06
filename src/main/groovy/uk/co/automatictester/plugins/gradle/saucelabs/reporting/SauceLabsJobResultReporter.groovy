package uk.co.automatictester.plugins.gradle.saucelabs.reporting

import com.saucelabs.saucerest.SauceREST
import groovy.json.JsonSlurper
import org.gradle.api.GradleException
import uk.co.automatictester.plugins.gradle.saucelabs.reporting.extension.SaucelabsReportingExtension

class SauceLabsJobResultReporter {

    SauceREST sauceLabsRestClient
    ActionOnFailure actionOnFailure

    SauceLabsJobResultReporter(SaucelabsReportingExtension config) {
        String user = config.user
        String key = config.key
        sauceLabsRestClient = new SauceREST(user, key)
        actionOnFailure = config.actionOnFailure
    }

    void updateResult(JUnitTestReport junitTestReport) {
        Map<String, Boolean> sauceLabsJobUpdates = [:]
        boolean passed = junitTestReport.passed
        sauceLabsJobUpdates.put('passed', passed)
        junitTestReport.log()

        String sauceLabsJobId = junitTestReport.sessionId
        sauceLabsRestClient.updateJobInfo(sauceLabsJobId, sauceLabsJobUpdates)
        checkIfUpdateSuccessful(junitTestReport)
    }

    void checkIfUpdateSuccessful(JUnitTestReport jUnitTestReport) {
        String sauceLabsJobId = sauceLabsRestClient.getJobInfo(jUnitTestReport.sessionId)
        Boolean sauceLabsJobResult = new JsonSlurper().parseText(sauceLabsJobId).passed
        compareResults(sauceLabsJobResult, jUnitTestReport)
    }

    void compareResults(Boolean sauceLabsJobResult, JUnitTestReport junitTestReport) {
        String sauceLabsJobId = junitTestReport.sessionId
        String junitTestReportFile = junitTestReport.filename
        boolean junitTestPassed = junitTestReport.passed

        if (sauceLabsJobResult != junitTestPassed) {
            String message = createMessage(sauceLabsJobId, junitTestReportFile, sauceLabsJobResult, junitTestPassed)
            throwExceptionOrLogWarning(message)
        }
    }

    String createMessage(String sauceLabsJobId, String junitTestReportFile, Boolean sauceLabsJobResult, boolean junitTestPassed) {
        """

SauceLabs job '${sauceLabsJobId}' for ${junitTestReportFile} was not updated
Status in Sauce Labs: ${sauceLabsJobResult}
Expected status: ${junitTestPassed}

"""
    }

    void throwExceptionOrLogWarning(String message) {
        switch (actionOnFailure) {
            case ActionOnFailure.ERROR:
                throw new GradleException(message)
            case ActionOnFailure.WARNING:
                println message
                break
        }
    }
}
