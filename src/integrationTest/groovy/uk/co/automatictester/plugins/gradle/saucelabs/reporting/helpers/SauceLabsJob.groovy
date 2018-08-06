package uk.co.automatictester.plugins.gradle.saucelabs.reporting.helpers

import com.saucelabs.saucerest.SauceREST
import uk.co.automatictester.plugins.gradle.saucelabs.reporting.JUnitReport

class SauceLabsJob {

    static String sauceUser = System.properties.SL_USER
    static String sauceKey = System.properties.SL_KEY

    static void deleteJob(JUnitReport junitReport) {
        junitReport.log()
        SauceREST sauceLabsRestClient = new SauceREST(sauceUser, sauceKey)
        String sessionId = junitReport.sessionId
        sauceLabsRestClient.deleteJob(sessionId)
    }
}
