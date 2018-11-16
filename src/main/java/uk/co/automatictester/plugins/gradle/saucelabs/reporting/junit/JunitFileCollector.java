package uk.co.automatictester.plugins.gradle.saucelabs.reporting.junit;

import uk.co.automatictester.plugins.gradle.saucelabs.reporting.extension.SaucelabsReportingExtension;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class JunitFileCollector {

    public static List<String> getFiles(SaucelabsReportingExtension config) {
        String testResultsDir = config.testResultsDir;
        String filenamePattern = config.filenamePattern;
        return getMatchingFilesRecursively(testResultsDir, filenamePattern);
    }

    private static List<String> getMatchingFilesRecursively(String dir, String pattern) {
        List<String> files;
        try {
            files = Files.walk(Paths.get(dir))
                    .filter(Files::isRegularFile)
                    .filter(file -> file.toString().matches(pattern))
                    .map(Path::toAbsolutePath)
                    .map(Path::toString)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return files;
    }
}
