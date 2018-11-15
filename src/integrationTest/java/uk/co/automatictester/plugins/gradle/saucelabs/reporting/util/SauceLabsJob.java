package uk.co.automatictester.plugins.gradle.saucelabs.reporting.util;

import com.saucelabs.saucerest.SauceREST;
import uk.co.automatictester.plugins.gradle.saucelabs.reporting.JunitReport;

public class SauceLabsJob {

    private static String sauceUser = System.getProperty("SL_USER");
    private static String sauceKey = System.getProperty("SL_KEY");

    private SauceLabsJob() {}

    public static void deleteJob(JunitReport junitReport) {
        junitReport.log();
        SauceREST sauceLabsRestClient = new SauceREST(sauceUser, sauceKey);
        String sessionId = junitReport.sessionId;
        sauceLabsRestClient.deleteJob(sessionId);
    }
}
