package uk.co.automatictester.plugins.gradle.saucelabs.reporting;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.time.LocalTime;
import java.util.Properties;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_TIME;

public class JunitReport {
    public String filename; // TODO: public -> private
    public String sessionId;
    public boolean passed;

    public JunitReport(String file) {
        Element element = getRootElement(file);
        setPassed(element);
        sessionId = getSauceLabsSessionId(element);
        filename = file;
    }

    private Element getRootElement(String file) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(file);
            return doc.getDocumentElement();
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void setPassed(Element element) {
        int failures = getAttributeValue(element, "failures");
        int errors = getAttributeValue(element, "errors");
        passed = failures + errors == 0;
    }

    private int getAttributeValue(Element element, String key) {
        String value = element.getAttribute(key);
        return Integer.parseInt(value);
    }

    private String getSauceLabsSessionId(Element element) {
        Element systemOutElement = (Element) element.getElementsByTagName("system-out").item(0);
        String systemOutCData = systemOutElement.getTextContent();
        StringReader stringReader = new StringReader(systemOutCData);
        Properties properties = new Properties();
        try {
            properties.load(stringReader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return properties.getProperty("SauceOnDemandSessionID");
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
}
