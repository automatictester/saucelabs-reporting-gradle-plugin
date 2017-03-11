package uk.co.deliverymind.plugins.gradle.saucelabs.reporting.helpers

import com.saucelabs.saucerest.SauceREST
import uk.co.deliverymind.plugins.gradle.saucelabs.reporting.JUnitTestReport
import uk.co.deliverymind.plugins.gradle.saucelabs.reporting.extension.SaucelabsReportingExtension

class SessionCleanupHelper {

    static String sauceUser = System.properties.SL_USER
    static String sauceKey = System.properties.SL_KEY

    static void deleteSession(JUnitTestReport testReport) {
        testReport.log()
        SauceREST restClient = new SauceREST(sauceUser, sauceKey)
        restClient.deleteJob(testReport.sessionId)
    }
}
