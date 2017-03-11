package uk.co.deliverymind.plugins.gradle.saucelabs.reporting

import groovy.io.FileType

class JUnitReportHandler {

    static List<String> getJUnitReports(String directory, String pattern) {
        List<String> allFiles = getAllFiles(directory)
        getFilteredFiles(allFiles, pattern)
    }

    static List<String> getAllFiles(String directory) {
        def files = []
        File dir = new File(directory)
        dir.absoluteFile.eachFileRecurse(FileType.FILES) { file ->
            files << file.path
        }
        files
    }

    static List<String> getFilteredFiles(List<String> files, String pattern) {
        files.findAll { it =~ pattern }
    }
}
