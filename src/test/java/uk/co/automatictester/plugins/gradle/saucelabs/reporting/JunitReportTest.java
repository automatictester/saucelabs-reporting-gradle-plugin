package uk.co.automatictester.plugins.gradle.saucelabs.reporting;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class JunitReportTest {

    @Test(dataProvider = "input")
    public void testReport(int failures, int errors, boolean passed) {
        String junitFile = String.format("src/test/resources/unit/TEST-FakeTest-%s%s.xml", failures, errors);
        JunitReport junitReport = new JunitReport(junitFile);
        assertEquals(junitReport.isPassed(), passed);
        assertEquals(junitReport.getFilename(), junitFile);
        assertEquals(junitReport.getSessionId(), "xyz");
    }

    @DataProvider(name = "input")
    public Object[][] arrays() {
        return new Object[][]{
                {0, 0, true},
                {1, 0, false},
                {0, 1, false},
                {1, 1, false},
        };
    }
}
