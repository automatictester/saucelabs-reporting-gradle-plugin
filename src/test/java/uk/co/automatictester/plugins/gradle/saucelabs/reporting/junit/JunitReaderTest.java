package uk.co.automatictester.plugins.gradle.saucelabs.reporting.junit;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class JunitReaderTest {

    @Test(dataProvider = "input")
    public void testReport(int failures, int errors, boolean passed) {
        String junitFile = String.format("src/test/resources/unit/TEST-FakeTest-%s%s.xml", failures, errors);
        JunitReport report = JunitReader.read(junitFile);
        assertEquals(report.isPassed(), passed);
        assertEquals(report.getFilename(), junitFile);
        assertEquals(report.getSessionId(), "xyz");
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
