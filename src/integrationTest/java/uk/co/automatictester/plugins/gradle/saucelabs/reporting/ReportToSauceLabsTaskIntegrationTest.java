package uk.co.automatictester.plugins.gradle.saucelabs.reporting;

import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.BuildTask;
import org.gradle.testkit.runner.GradleRunner;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import uk.co.automatictester.plugins.gradle.saucelabs.reporting.extension.SaucelabsReportingExtension;
import uk.co.automatictester.plugins.gradle.saucelabs.reporting.junit.JunitFileCollector;
import uk.co.automatictester.plugins.gradle.saucelabs.reporting.junit.JunitReader;
import uk.co.automatictester.plugins.gradle.saucelabs.reporting.junit.JunitReport;
import uk.co.automatictester.plugins.gradle.saucelabs.reporting.util.SauceLabsJob;

import java.io.File;
import java.util.List;

import static org.gradle.testkit.runner.TaskOutcome.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class ReportToSauceLabsTaskIntegrationTest {

    private static final String CLEAN = ":clean";
    private static final String TEST = ":test";
    private static final String REPORT_TO_SAUCELABS = ":reportToSauceLabs";

    @Test(dataProvider = "input")
    public void testEndToEndFunctionality(String version) {
        GradleRunner runner = GradleRunner.create()
                .withGradleVersion(version)
                .withProjectDir(new File("src/integrationTest/resources"))
                .withPluginClasspath();

        BuildResult cleanResult = runner
                .withArguments(CLEAN)
                .build();
        System.out.print(cleanResult.getOutput());
        assertTrue(isTaskSuccessOrUpToDate(cleanResult.task(CLEAN)));

        BuildResult testResult = runner
                .withArguments(TEST)
                .buildAndFail();
        System.out.print(testResult.getOutput());
        assertEquals(testResult.task(TEST).getOutcome(), FAILED);
        assertTrue(testResult.getOutput().contains("3 tests completed, 1 failed"));

        BuildResult reportResult = runner
                .withArguments(REPORT_TO_SAUCELABS)
                .build();
        System.out.print(reportResult.getOutput());
        assertEquals(reportResult.task(REPORT_TO_SAUCELABS).getOutcome(), SUCCESS);

        SaucelabsReportingExtension config = new SaucelabsReportingExtension();
        config.testResultsDir = "src/integrationTest/resources/build/test-results";
        config.filenamePattern = "(.)*TEST-(.)*\\.xml";

        List<String> files = JunitFileCollector.getFiles(config);
        files.forEach(file -> {
            JunitReport report = JunitReader.read(file);
            SauceLabsJob.deleteJob(report);
        });
    }

    @DataProvider(name = "input")
    public Object[][] arrays() {
        return new Object[][]{
                {"2.14.1"},
                {"3.5.1"},
                {"4.10.2"},
        };
    }

    private static boolean isTaskSuccessOrUpToDate(BuildTask task) {
        return (task.getOutcome() == SUCCESS) || (task.getOutcome() == UP_TO_DATE);
    }
}
