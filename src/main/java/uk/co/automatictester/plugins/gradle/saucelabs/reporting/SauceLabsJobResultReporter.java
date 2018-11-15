package uk.co.automatictester.plugins.gradle.saucelabs.reporting;

import com.saucelabs.saucerest.SauceREST;
import org.gradle.api.GradleException;
import org.json.JSONException;
import org.json.JSONObject;
import uk.co.automatictester.plugins.gradle.saucelabs.reporting.enums.ActionOnFailure;
import uk.co.automatictester.plugins.gradle.saucelabs.reporting.extension.SaucelabsReportingExtension;

import java.util.HashMap;
import java.util.Map;

public class SauceLabsJobResultReporter {

    private SauceREST sauceLabsRestClient;
    private ActionOnFailure actionOnFailure;

    public SauceLabsJobResultReporter(SaucelabsReportingExtension config) {
        String user = config.user;
        String key = config.key;
        sauceLabsRestClient = new SauceREST(user, key);
        actionOnFailure = config.actionOnFailure;
    }

    public void updateResult(JunitReport junitReport) {
        Map<String, Object> sauceLabsJobUpdates = new HashMap<>();
        boolean passed = junitReport.isPassed();
        sauceLabsJobUpdates.put("passed", passed);
        junitReport.log();

        String sauceLabsJobId = junitReport.getSessionId();
        sauceLabsRestClient.updateJobInfo(sauceLabsJobId, sauceLabsJobUpdates);
        checkIfUpdateSuccessful(junitReport);
    }

    private void checkIfUpdateSuccessful(JunitReport junitReport) {
        String jobInfo = sauceLabsRestClient.getJobInfo(junitReport.getSessionId());
        String passed;
        try {
            JSONObject jsonObject = new JSONObject(jobInfo);
            passed = jsonObject.get("passed").toString();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        Boolean sauceLabsJobPassed = passed.equals("true");
        compareResults(sauceLabsJobPassed, junitReport);
    }

    public void compareResults(Boolean sauceLabsJobPassed, JunitReport junitReport) {
        String sauceLabsJobId = junitReport.getSessionId();
        String junitTestReportFile = junitReport.getFilename();
        boolean junitTestPassed = junitReport.isPassed();

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
