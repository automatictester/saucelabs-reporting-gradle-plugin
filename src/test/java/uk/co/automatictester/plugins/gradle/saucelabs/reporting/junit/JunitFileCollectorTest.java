package uk.co.automatictester.plugins.gradle.saucelabs.reporting.junit;

import org.testng.annotations.Test;
import uk.co.automatictester.plugins.gradle.saucelabs.reporting.extension.SaucelabsReportingExtension;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class JunitFileCollectorTest {

    @Test
    public void testListAllJunitReportsRecursively() {
        String dir = "src/test/resources/report-handler";
        List<String> expectedReports = new ArrayList<>();

        expectedReports.add(new File(String.format("%s/subdir/TEST-DTest.xml", dir)).getAbsolutePath());
        expectedReports.add(new File(String.format("%s/subdir/TEST-CTest.xml", dir)).getAbsolutePath());
        expectedReports.add(new File(String.format("%s/TEST-ATest.xml", dir)).getAbsolutePath());
        expectedReports.add(new File(String.format("%s/TEST-BTest.xml", dir)).getAbsolutePath());

        SaucelabsReportingExtension config = new SaucelabsReportingExtension();
        config.testResultsDir = dir;
        config.filenamePattern = "(.)*TEST-(.)*\\.xml";

        List<String> foundReports = JunitFileCollector.getFiles(config);
        assertTrue(foundReports.containsAll(expectedReports));
        assertEquals(foundReports.size(), 4);
    }
}
