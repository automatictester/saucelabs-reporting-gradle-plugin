package uk.co.automatictester.plugins.gradle.saucelabs.reporting;

import org.testng.annotations.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class JunitReportHandlerTest {

    @Test
    public void testListAllJunitReportsRecursively() {
        String dir = "src/test/resources/report-handler";
        List<String> expectedReports = new ArrayList<>();

        expectedReports.add(new File(String.format("%s/subdir/TEST-DTest.xml", dir)).getAbsolutePath());
        expectedReports.add(new File(String.format("%s/subdir/TEST-uk.co.automatictester.CTest.xml", dir)).getAbsolutePath());
        expectedReports.add(new File(String.format("%s/TEST-ATest.xml", dir)).getAbsolutePath());
        expectedReports.add(new File(String.format("%s/TEST-BTest.xml", dir)).getAbsolutePath());

        List<String> foundReports = JunitReportHandler.getJunitFiles(dir, "(.)*TEST-(.)*\\.xml");
        assertTrue(foundReports.containsAll(expectedReports));
        assertEquals(foundReports.size(), 4);
    }
}
