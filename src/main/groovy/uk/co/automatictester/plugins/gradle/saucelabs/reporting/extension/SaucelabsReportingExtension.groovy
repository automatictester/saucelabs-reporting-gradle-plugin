package uk.co.automatictester.plugins.gradle.saucelabs.reporting.extension

import uk.co.automatictester.plugins.gradle.saucelabs.reporting.enums.ActionOnFailure

class SaucelabsReportingExtension {

    String filenamePattern = /TEST-(.)*\.xml/
    String testResultsDir
    String user
    String key
    ActionOnFailure actionOnFailure = ActionOnFailure.WARNING
}
