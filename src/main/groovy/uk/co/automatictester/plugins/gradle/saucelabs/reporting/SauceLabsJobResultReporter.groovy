package uk.co.automatictester.plugins.gradle.saucelabs.reporting

import com.saucelabs.saucerest.SauceREST
import groovy.json.JsonSlurper
import org.gradle.api.GradleException
import uk.co.automatictester.plugins.gradle.saucelabs.reporting.enums.ActionOnFailure
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

    void updateResult(JUnitReport junitReport) {
        Map<String, Boolean> sauceLabsJobUpdates = [:]
        boolean passed = junitReport.passed
        sauceLabsJobUpdates.put('passed', passed)
        junitReport.log()

        String sauceLabsJobId = junitReport.sessionId
        sauceLabsRestClient.updateJobInfo(sauceLabsJobId, sauceLabsJobUpdates)
        checkIfUpdateSuccessful(junitReport)
    }

    void checkIfUpdateSuccessful(JUnitReport jUnitReport) {
        String sauceLabsJobId = sauceLabsRestClient.getJobInfo(jUnitReport.sessionId)
        Boolean sauceLabsJobPassed = new JsonSlurper().parseText(sauceLabsJobId).passed
        compareResults(sauceLabsJobPassed, jUnitReport)
    }

    void compareResults(Boolean sauceLabsJobPassed, JUnitReport junitReport) {
        String sauceLabsJobId = junitReport.sessionId
        String junitTestReportFile = junitReport.filename
        boolean junitTestPassed = junitReport.passed

        if (isResultDifferent(sauceLabsJobPassed, junitTestPassed)) {
            String message = createMessage(sauceLabsJobId, junitTestReportFile, sauceLabsJobPassed, junitTestPassed)
            throwExceptionOrLogWarning(message)
        }
    }

    static boolean isResultDifferent(Boolean sauceLabsJobPassed, boolean junitTestPassed) {
        sauceLabsJobPassed != junitTestPassed
    }

    static String createMessage(String sauceLabsJobId, String junitTestReportFile, Boolean sauceLabsJobPassed, boolean junitTestPassed) {
        """

SauceLabs job '${sauceLabsJobId}' for ${junitTestReportFile} was not updated
Status in Sauce Labs: ${sauceLabsJobPassed}
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
