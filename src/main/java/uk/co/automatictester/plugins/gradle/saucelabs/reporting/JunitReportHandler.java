package uk.co.automatictester.plugins.gradle.saucelabs.reporting;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class JunitReportHandler {

    public static List<String> getJunitFiles(String directory, String pattern) {
        List<String> allJunitReportFiles = getFilesRecursivelyFrom(directory);
        return getJunitFilesMatchingPattern(allJunitReportFiles, pattern);
    }

    private static List<String> getFilesRecursivelyFrom(String dir) {
        List<String> files;
        try {
            files = Files.walk(Paths.get(dir))
                    .filter(Files::isRegularFile)
                    .map(Path::toAbsolutePath)
                    .map(Path::toString)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return files;
    }

    private static List<String> getJunitFilesMatchingPattern(List<String> files, String pattern) {
        return files.stream()
                .filter(file -> file.matches(pattern))
                .collect(Collectors.toList());
    }
}
