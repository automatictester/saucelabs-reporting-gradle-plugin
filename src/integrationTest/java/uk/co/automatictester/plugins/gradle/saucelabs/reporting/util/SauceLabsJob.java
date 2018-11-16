package uk.co.automatictester.plugins.gradle.saucelabs.reporting.util;

import com.saucelabs.saucerest.SauceREST;
import uk.co.automatictester.plugins.gradle.saucelabs.reporting.junit.JunitReader;
import uk.co.automatictester.plugins.gradle.saucelabs.reporting.junit.JunitReport;

public class SauceLabsJob {

    private static final String SL_USER = System.getProperty("SL_USER");
    private static final String SL_KEY = System.getProperty("SL_KEY");
    private static final SauceREST sauceLabsRestClient = new SauceREST(SL_USER, SL_KEY);

    private SauceLabsJob() {}

    public static void deleteJob(JunitReport report) {
        report.log();
        String sessionId = report.getSessionId();
        sauceLabsRestClient.deleteJob(sessionId);
    }
}
