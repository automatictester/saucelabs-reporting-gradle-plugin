class PassFailTest extends BaseTest {

    def setupSpec() {
        startSession()
    }

    def cleanupSpec() {
        stopSession()
    }

    def 'Pass'() {
        expect:
        driver.get('http://google.com')
    }

    def 'Fail'() {
        expect:
        driver.get('htttp://google.com')
    }
}
