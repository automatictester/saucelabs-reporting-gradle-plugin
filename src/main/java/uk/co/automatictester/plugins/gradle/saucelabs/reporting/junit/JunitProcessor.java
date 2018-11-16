package uk.co.automatictester.plugins.gradle.saucelabs.reporting.junit;

import uk.co.automatictester.plugins.gradle.saucelabs.reporting.SauceLabsReporter;
import uk.co.automatictester.plugins.gradle.saucelabs.reporting.extension.SaucelabsReportingExtension;

import java.util.List;

public class JunitProcessor {

    private JunitProcessor() {}

    public static void process(SaucelabsReportingExtension config) {
        List<String> files = JunitFileCollector.getFiles(config);
        processFiles(files, config);
    }

    private static void processFiles(List<String> files, SaucelabsReportingExtension config) {
        SauceLabsReporter reporter = new SauceLabsReporter(config);
        files.forEach(file -> {
            JunitReport report = JunitReader.read(file);
            reporter.updateResult(report);
        });
    }
}
