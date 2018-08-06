package uk.co.automatictester.plugins.gradle.saucelabs.reporting

class JUnitTestReport {
    String filename
    String sessionId
    boolean passed

    JUnitTestReport(String file) {
        Node xml = getXmlFromFile(file)
        setPassed(xml)
        sessionId = getSauceLabsSessionId(xml)
        filename = file
    }

    Node getXmlFromFile(String file) {
        XmlParser xmlParser = new XmlParser()
        xmlParser.parse(file)
    }

    void setPassed(Node node) {
        int failures = getAttributeValue(node, 'failures')
        int errors = getAttributeValue(node, 'errors')
        passed = failures + errors == 0
    }

    int getAttributeValue(Node node, String key) {
        String value = node.attribute(key).toString()
        Integer.parseInt(value)
    }

    String getSauceLabsSessionId(Node node) {
        String systemOutCData = node.get('system-out').text()
        StringReader stringReader = new StringReader(systemOutCData)
        Properties properties = new Properties()
        properties.load(stringReader)
        properties.SauceOnDemandSessionID
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
