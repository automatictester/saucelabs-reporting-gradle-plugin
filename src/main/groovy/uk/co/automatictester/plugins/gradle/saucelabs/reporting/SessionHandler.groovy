package uk.co.automatictester.plugins.gradle.saucelabs.reporting

import com.saucelabs.saucerest.SauceREST
import groovy.json.JsonSlurper
import org.gradle.api.GradleException
import uk.co.automatictester.plugins.gradle.saucelabs.reporting.extension.SaucelabsReportingExtension

class SessionHandler {

    SaucelabsReportingExtension cfg

    SessionHandler(SaucelabsReportingExtension cfg) {
        this.cfg = cfg
    }

    void updateSessionResult(JUnitTestReport testReport) {
        Map<String, Boolean> updates = [:]
        updates.put('passed', testReport.passed)
        testReport.log()
        SauceREST restClient = new SauceREST(cfg.user, cfg.key)
        restClient.updateJobInfo(testReport.sessionId, updates)
        checkSessionResult(testReport)
    }

    void checkSessionResult(JUnitTestReport testReport) {
        SauceREST restClient = new SauceREST(cfg.user, cfg.key)
        String sessionInfo = restClient.getJobInfo(testReport.sessionId)
        Boolean currentSessionResult = new JsonSlurper().parseText(sessionInfo).passed
        handleSessionStatusUpdate(currentSessionResult, testReport)
    }

    void handleSessionStatusUpdate(Boolean currentSessionResult, JUnitTestReport testReport) {
        if (currentSessionResult != testReport.passed) {
            String message = """

Session '${testReport.sessionId}' for ${testReport.filename} was not updated
Status in Sauce Labs: ${currentSessionResult}
Expected status: ${testReport.passed}

"""
            switch (cfg.actionOnFailure) {
                case ActionOnFailure.ERROR:
                    throw new GradleException(message)
                case ActionOnFailure.WARNING:
                    println message
                    break
            }
        }
    }
}
