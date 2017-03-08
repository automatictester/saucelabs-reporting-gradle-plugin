package uk.co.deliverymind.plugins.gradle.saucelabs.reporting.extension

import uk.co.deliverymind.plugins.gradle.saucelabs.reporting.ActionOnFailure

class SaucelabsReportingExtension {

    // TODO: test with non-default values
    String filenamePattern = /TEST-(.)*\.xml/
    String testResultsDir
    String user
    String key
    ActionOnFailure actionOnFailure = ActionOnFailure.WARNING
}
