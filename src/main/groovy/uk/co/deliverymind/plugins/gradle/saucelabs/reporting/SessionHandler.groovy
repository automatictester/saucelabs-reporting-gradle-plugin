package uk.co.deliverymind.plugins.gradle.saucelabs.reporting

import com.saucelabs.saucerest.SauceREST
import groovy.json.JsonSlurper
import org.gradle.api.GradleException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import uk.co.deliverymind.plugins.gradle.saucelabs.reporting.extension.SaucelabsReportingExtension

class SessionHandler {

    Logger logger = LoggerFactory.getLogger(SessionHandler.class)
    SaucelabsReportingExtension cfg

    SessionHandler(SaucelabsReportingExtension cfg) {
        this.cfg = cfg
    }

    void updateSessionResult(JUnitTestReport testReport) {
        HashMap<String, Boolean> updates = new HashMap<String, Boolean>()
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
            // TODO: enum
            String message = "\nSession '${testReport.sessionId}' for ${testReport.filename} was not updated\nStatus in Sauce Labs: ${currentSessionResult}\nExpected status: ${testReport.passed}\n"
            if (cfg.actionOnFailure == 'warning') {
                logger.warn(message)
            } else if (cfg.actionOnFailure == 'error') {
                throw new GradleException(message)
            } else if (cfg.actionOnFailure == 'quiet') {
                println message
            }
        }
    }
}
