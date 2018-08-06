package uk.co.automatictester.plugins.gradle.saucelabs.reporting

import groovy.io.FileType

class JUnitReportHandler {

    static List<String> getJUnitReportFiles(String directory, String pattern) {
        List<String> allJUnitReportFiles = getAllJUnitReportFilesRecursivelyFrom(directory)
        getJUnitReportFilesMatchingPattern(allJUnitReportFiles, pattern)
    }

    static List<String> getAllJUnitReportFilesRecursivelyFrom(String directory) {
        List<String> junitReportFiles = []
        File directoryToSearch = new File(directory)
        directoryToSearch.absoluteFile.eachFileRecurse(FileType.FILES) { junitReportFile ->
            junitReportFiles << junitReportFile.path
        }
        junitReportFiles
    }

    static List<String> getJUnitReportFilesMatchingPattern(List<String> files, String pattern) {
        files.findAll { it =~ pattern }
    }
}
