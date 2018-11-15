package uk.co.automatictester.plugins.gradle.saucelabs.reporting;

import org.gradle.api.GradleException;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import uk.co.automatictester.plugins.gradle.saucelabs.reporting.enums.ActionOnFailure;
import uk.co.automatictester.plugins.gradle.saucelabs.reporting.extension.SaucelabsReportingExtension;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class SauceLabsJobResultReporterTest extends ConsoleOutputTest {

    private static final String SESSION_ID = "xyz";
    private static final String FILENAME = "TEST-SampleTest.xml";

    @BeforeMethod
    public void setup() {
        configureStream();
    }

    @AfterMethod
    public void cleanup() {
        revertStream();
    }

    @Test(dataProvider = "input")
    public void detectResultInconsistency(Boolean sauceResult, Boolean reportResult, Boolean isInconsistent, String message) {
        SaucelabsReportingExtension cfg = mock(SaucelabsReportingExtension.class);
        cfg.actionOnFailure = ActionOnFailure.WARNING;

        JunitReport junitReport = mock(JunitReport.class);
        when(junitReport.isPassed()).thenReturn(reportResult);
        when(junitReport.getSessionId()).thenReturn(SESSION_ID);
        when(junitReport.getFilename()).thenReturn(FILENAME);

        SauceLabsJobResultReporter sessionHandler = new SauceLabsJobResultReporter(cfg);
        sessionHandler.compareResults(sauceResult, junitReport);

        if (isInconsistent) {
            assertTrue(out.toString().contains(message));
        } else {
            assertEquals(out.toString(), "");
        }
        out.reset();
    }

    @DataProvider(name = "input")
    public Object[][] arrays() {
        return new Object[][]{
                {true, true, false, null},
                {true, false, true, String.format("\nSauceLabs job '%s' for %s was not updated\nStatus in Sauce Labs: true\nExpected status: false\n", SESSION_ID, FILENAME)},
                {false, true, true, String.format("\nSauceLabs job '%s' for %s was not updated\nStatus in Sauce Labs: false\nExpected status: true\n", SESSION_ID, FILENAME)},
                {false, false, false, null},
                {null, true, true, String.format("\nSauceLabs job '%s' for %s was not updated\nStatus in Sauce Labs: null\nExpected status: true\n", SESSION_ID, FILENAME)},
                {null, false, true, String.format("\nSauceLabs job '%s' for %s was not updated\nStatus in Sauce Labs: null\nExpected status: false\n", SESSION_ID, FILENAME)},
        };
    }

    @Test
    public void logWarningOnResultInconsistency() {
        SaucelabsReportingExtension cfg = mock(SaucelabsReportingExtension.class);
        cfg.actionOnFailure = ActionOnFailure.WARNING;

        JunitReport junitReport = mock(JunitReport.class);
        when(junitReport.isPassed()).thenReturn(false);
        when(junitReport.getSessionId()).thenReturn(SESSION_ID);
        when(junitReport.getFilename()).thenReturn(FILENAME);

        SauceLabsJobResultReporter reporter = new SauceLabsJobResultReporter(cfg);
        reporter.compareResults(true, junitReport);

        String message = String.format("\nSauceLabs job '%s' for %s was not updated\nStatus in Sauce Labs: true\nExpected status: false\n",
                SESSION_ID, FILENAME);
        assertTrue(out.toString().contains(message));
    }

    @Test(expectedExceptions = GradleException.class)
    public void throwErrorOnResultInconsistency() {
        SaucelabsReportingExtension cfg = mock(SaucelabsReportingExtension.class);
        cfg.actionOnFailure = ActionOnFailure.ERROR;

        JunitReport junitReport = mock(JunitReport.class);
        when(junitReport.isPassed()).thenReturn(false);

        SauceLabsJobResultReporter reporter = new SauceLabsJobResultReporter(cfg);
        reporter.compareResults(true, junitReport);
    }
}
