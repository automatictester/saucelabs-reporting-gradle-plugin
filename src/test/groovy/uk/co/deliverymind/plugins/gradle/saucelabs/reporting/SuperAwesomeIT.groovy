package uk.co.deliverymind.plugins.gradle.saucelabs.reporting

import org.openqa.selenium.WebDriver
import org.openqa.selenium.remote.DesiredCapabilities
import org.openqa.selenium.remote.RemoteWebDriver
import spock.lang.Specification

class SuperAwesomeIT extends Specification {

    WebDriver driver

    def setup() {
        Properties prop = new Properties()
        prop.load(this.getClass().getClassLoader().getResourceAsStream('saucelabs.properties'))
        String user = prop.get('user')
        String key = prop.get('key')

        DesiredCapabilities caps = DesiredCapabilities.firefox()
        driver = new RemoteWebDriver(new URL("https://${user}:${key}@ondemand.saucelabs.com:443/wd/hub"), caps)
        String sessionId = (((RemoteWebDriver) driver).getSessionId())
        println("SauceOnDemandSessionID=${sessionId}")
    }

    def 'Run simple Selenium test'() {
        expect:
        driver.get('http://google.com')
    }

    def cleanup() {
        driver.quit()
    }
}
