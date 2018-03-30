package uk.co.automatictester.plugins.gradle.saucelabs.reporting

class JUnitTestReport {
    String filename
    String sessionId
    boolean passed

    JUnitTestReport(String filename) {
        Node xml = new XmlParser().parse(filename)

        String systemOutCData = xml.get('system-out').text()
        Properties prop = new Properties()
        prop.load(new StringReader(systemOutCData))

        int failures = Integer.parseInt(xml.attribute('failures').toString())
        int errors = Integer.parseInt(xml.attribute('errors').toString())

        this.sessionId = prop.SauceOnDemandSessionID
        this.filename = filename
        setPassed(failures, errors)
    }

    void setPassed(int failures, int errors) {
        passed = failures + errors == 0
    }

    void log() {
        String time = new Date().format('HH:mm:ss:SSS')

        println 'Processing session:'
        println "Current time: ${time}"
        println "Filename:     ${filename}"
        println "SessionId:    ${sessionId}"
        println "Passed:       ${passed}"
    }
}
