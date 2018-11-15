import org.testng.annotations.*;

public class PassFailTest extends BaseTest {

    @BeforeClass
    public void setupSpec() {
        startSession();
    }

    @AfterClass
    public void cleanupSpec() {
        stopSession();
    }

    @Test
    public void pass() {
        logSessionInfo();
        driver.get("http://google.com");
    }

    @Test
    public void fail() {
        logSessionInfo();
        driver.get("htttp://google.com");
    }
}
