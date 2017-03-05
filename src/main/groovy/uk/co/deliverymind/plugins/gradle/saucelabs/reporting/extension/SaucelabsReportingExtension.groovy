package uk.co.deliverymind.plugins.gradle.saucelabs.reporting.extension

class SaucelabsReportingExtension {

    // TODO: test with non-standard values
    String filenamePattern = /TEST-(.)*\.xml/
    String testResultsDir
    String user
    String key
    String actionOnFailure
}
