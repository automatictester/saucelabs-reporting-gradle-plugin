package uk.co.automatictester.plugins.gradle.saucelabs.reporting.junit;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.util.Properties;

public class JunitReader {

    private JunitReader() {}

    public static JunitReport read(String file) {
        Element element = getRootElement(file);
        String sessionId = getSauceLabsSessionId(element);
        boolean passed = getPassed(element);
        return new JunitReport(file, sessionId, passed);
    }

    private static Element getRootElement(String file) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(file);
            return doc.getDocumentElement();
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean getPassed(Element element) {
        int failures = getAttributeValue(element, "failures");
        int errors = getAttributeValue(element, "errors");
        return failures + errors == 0;
    }

    private static int getAttributeValue(Element element, String key) {
        String value = element.getAttribute(key);
        return Integer.parseInt(value);
    }

    private static String getSauceLabsSessionId(Element element) {
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
}
