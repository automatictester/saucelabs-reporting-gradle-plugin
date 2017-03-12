class SampleSeleniumTest extends BaseTest {

    def setupSpec() {
        startSession()
    }

    def cleanupSpec() {
        stopSession()
    }

    def 'Sample Selenium test which only purpose is to create Sauce Labs session and JUnit XML report, for further update with this plugin'() {
        expect:
        BaseTest.driver.get('http://google.com')
    }
}
