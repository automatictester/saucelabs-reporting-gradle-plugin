package uk.co.automatictester.plugins.gradle.saucelabs.reporting.extension;

import uk.co.automatictester.plugins.gradle.saucelabs.reporting.enums.ActionOnFailure;

public class SaucelabsReportingExtension {

    public String filenamePattern = "(.)*TEST-(.)*\\.xml";
    public String testResultsDir;
    public String user;
    public String key;
    public ActionOnFailure actionOnFailure = ActionOnFailure.WARNING;
}
