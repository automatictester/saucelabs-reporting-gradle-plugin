package load.src.test.groovy

class T01Test extends BaseTest {

    def setupSpec() {
        startSession()
    }

    def cleanupSpec() {
        stopSession()
    }

    def 'Pass again'() {
        expect:
        driver.get('http://google.com')
    }
}
