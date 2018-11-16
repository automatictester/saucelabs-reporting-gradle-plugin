package uk.co.automatictester.plugins.gradle.saucelabs.reporting.junit;

import java.time.LocalTime;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_TIME;

public class JunitReport {

    private String filename;
    private String sessionId;
    private boolean passed;

    public JunitReport(String filename, String sessionId, boolean passed) {
        this.filename = filename;
        this.sessionId = sessionId;
        this.passed = passed;
    }

    public void log() {
        LocalTime now = LocalTime.now();
        String time = now.format(ISO_LOCAL_TIME);
        System.out.println("Processing session:");
        System.out.println("Current time: " + time);
        System.out.println("Filename:     " + filename);
        System.out.println("SessionId:    " + sessionId);
        System.out.println("Passed:       " + passed);
    }

    public String getFilename() {
        return filename;
    }

    public String getSessionId() {
        return sessionId;
    }

    public boolean isPassed() {
        return passed;
    }
}
