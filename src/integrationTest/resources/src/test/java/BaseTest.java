import java.net.MalformedURLException;
import java.net.URL;

import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.Reporter;

public class BaseTest {

    protected static RemoteWebDriver driver;
    private String sessionInfo;
    private boolean loggedSessionInfo = false;

    protected void logSessionInfo() {
        if (!loggedSessionInfo) {
            Reporter.log(sessionInfo, true);
            loggedSessionInfo = true;
        }
    }

    protected void startSession() {
        URL url = getUrl();
        DesiredCapabilities caps = DesiredCapabilities.firefox();
        driver = new RemoteWebDriver(url, caps);
        String sessionId = driver.getSessionId().toString();
        sessionInfo = String.format("SauceOnDemandSessionID=%s", sessionId);
    }

    protected void stopSession() {
        driver.quit();
    }

    private URL getUrl() {
        String user = System.getProperty("SL_USER");
        String key = System.getProperty("SL_KEY");
        String urlString = String.format("https://%s:%s@ondemand.saucelabs.com:443/wd/hub", user, key);
        URL url;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        return url;
    }
}
