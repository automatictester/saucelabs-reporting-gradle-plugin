import org.testng.annotations.*;

public class PassTest extends BaseTest {

    @BeforeClass
    public void setupSpec() {
        startSession();
    }

    @AfterClass
    public void cleanupSpec() {
        stopSession();
    }

    @Test
    public void passAgain() {
        logSessionInfo();
        driver.get("http://google.com");
    }
}
