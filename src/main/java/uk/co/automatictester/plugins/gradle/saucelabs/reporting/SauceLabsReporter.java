package uk.co.automatictester.plugins.gradle.saucelabs.reporting;

import com.saucelabs.saucerest.SauceREST;
import org.gradle.api.GradleException;
import org.json.JSONException;
import org.json.JSONObject;
import uk.co.automatictester.plugins.gradle.saucelabs.reporting.enums.ActionOnFailure;
import uk.co.automatictester.plugins.gradle.saucelabs.reporting.extension.SaucelabsReportingExtension;
import uk.co.automatictester.plugins.gradle.saucelabs.reporting.junit.JunitReport;

import java.util.HashMap;
import java.util.Map;

public class SauceLabsReporter {

    private SauceREST sauceLabsRestClient;
    private ActionOnFailure actionOnFailure;

    public SauceLabsReporter(SaucelabsReportingExtension config) {
        String user = config.user;
        String key = config.key;
        sauceLabsRestClient = new SauceREST(user, key);
        actionOnFailure = config.actionOnFailure;
    }

    public void updateResult(JunitReport report) {
        Map<String, Object> sauceLabsJobUpdates = new HashMap<>();
        boolean passed = report.isPassed();
        sauceLabsJobUpdates.put("passed", passed);
        report.log();

        String sauceLabsJobId = report.getSessionId();
        sauceLabsRestClient.updateJobInfo(sauceLabsJobId, sauceLabsJobUpdates);
        checkIfUpdateSuccessful(report);
    }

    private void checkIfUpdateSuccessful(JunitReport report) {
        String jobInfo = sauceLabsRestClient.getJobInfo(report.getSessionId());
        String passed;
        try {
            JSONObject jsonObject = new JSONObject(jobInfo);
            passed = jsonObject.get("passed").toString();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        Boolean sauceLabsJobPassed = passed.equals("true");
        compareResults(sauceLabsJobPassed, report);
    }

    void compareResults(Boolean sauceLabsJobPassed, JunitReport report) {
        String sauceLabsJobId = report.getSessionId();
        String junitTestReportFile = report.getFilename();
        boolean junitTestPassed = report.isPassed();

        if (isResultDifferent(sauceLabsJobPassed, junitTestPassed)) {
            String message = createMessage(sauceLabsJobId, junitTestReportFile, sauceLabsJobPassed, junitTestPassed);
            throwExceptionOrLogWarning(message);
        }
    }

    private boolean isResultDifferent(Boolean sauceLabsJobPassed, boolean junitTestPassed) {
        if (sauceLabsJobPassed == null) return true;
        return sauceLabsJobPassed != junitTestPassed;
    }

    private String createMessage(String sauceLabsJobId, String junitTestReportFile, Boolean sauceLabsJobPassed, boolean junitTestPassed) {
        return String.format("\n\nSauceLabs job '%s' for %s was not updated\nStatus in Sauce Labs: %s\nExpected status: %s",
                sauceLabsJobId, junitTestReportFile, sauceLabsJobPassed, junitTestPassed);
    }

    private void throwExceptionOrLogWarning(String message) {
        switch (actionOnFailure) {
            case ERROR:
                throw new GradleException(message);
            case WARNING:
                System.out.println(message);
        }
    }
}
