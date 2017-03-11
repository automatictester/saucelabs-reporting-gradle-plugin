class PassFailTest extends BaseTest {

    def setupSpec() {
        startSession()
    }

    def cleanupSpec() {
        stopSession()
    }

    def 'Pass'() {
        expect:
        BaseTest.driver.get('http://google.com')
    }

    def 'Fail'() {
        expect:
        BaseTest.driver.get('htttp://google.com')
    }
}
