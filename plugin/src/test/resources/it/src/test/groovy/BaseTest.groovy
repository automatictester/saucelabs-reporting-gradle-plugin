package it.src.test.groovy

import org.openqa.selenium.remote.DesiredCapabilities
import org.openqa.selenium.remote.RemoteWebDriver
import spock.lang.Specification

class BaseTest extends Specification {

    static RemoteWebDriver driver

    def startSession() {
        String user = System.properties.SL_USER
        String key = System.properties.SL_KEY
        String urlString = "https://${user}:${key}@ondemand.saucelabs.com:443/wd/hub"
        URL url = new URL(urlString)

        DesiredCapabilities caps = DesiredCapabilities.firefox()
        driver = new RemoteWebDriver(url, caps)
        String sessionId = driver.getSessionId()
        println("SauceOnDemandSessionID=${sessionId}")
    }

    def stopSession() {
        driver.quit()
    }
}
