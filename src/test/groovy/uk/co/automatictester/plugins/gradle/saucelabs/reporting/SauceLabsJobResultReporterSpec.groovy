package uk.co.automatictester.plugins.gradle.saucelabs.reporting

import groovy.ui.SystemOutputInterceptor
import org.gradle.api.GradleException
import spock.lang.Specification
import spock.lang.Unroll
import uk.co.automatictester.plugins.gradle.saucelabs.reporting.extension.SaucelabsReportingExtension

class SauceLabsJobResultReporterSpec extends Specification {

    static final String SESSION_ID = 'xyz'
    static final String FILENAME = 'TEST-SampleTest.xml'

    SystemOutputInterceptor interceptor
    String out

    void setup() {
        out = ''
        interceptor = new SystemOutputInterceptor({ out += it; false })
        interceptor.start()
    }

    void cleanup() {
        interceptor.stop()
    }

    @Unroll
    void "Should detect results inconsistency correctly: #sauceResult - #reportResult"() {
        given: 'Mocks are in place'
        SaucelabsReportingExtension cfg = Stub()
        JUnitTestReport testReport = Stub()

        SauceLabsJobResultReporter sessionHandler = new SauceLabsJobResultReporter(cfg)

        and: 'JUnit test report stub is configured'
        testReport.passed >> reportResult
        testReport.sessionId >> SESSION_ID
        testReport.filename >> FILENAME

        and: 'Plugin extension is configured'
        cfg.actionOnFailure >> ActionOnFailure.WARNING

        when: 'Sauce session status is compared with session status in JUnit report'
        sessionHandler.compareResults(sauceResult, testReport)

        then: 'Inconsistency is correctly handled'
        if (isInconsistent) {
            assert out.contains(message)
        } else {
            assert out == ''
        }

        where:
        sauceResult | reportResult | isInconsistent | message
        true        | true         | false          | null
        true        | false        | true           | "\nSauceLabs job '${SESSION_ID}' for ${FILENAME} was not updated\nStatus in Sauce Labs: true\nExpected status: false\n"
        false       | true         | true           | "\nSauceLabs job '${SESSION_ID}' for ${FILENAME} was not updated\nStatus in Sauce Labs: false\nExpected status: true\n"
        false       | false        | false          | null
        null        | true         | true           | "\nSauceLabs job '${SESSION_ID}' for ${FILENAME} was not updated\nStatus in Sauce Labs: null\nExpected status: true\n"
        null        | false        | true           | "\nSauceLabs job '${SESSION_ID}' for ${FILENAME} was not updated\nStatus in Sauce Labs: null\nExpected status: false\n"
    }

    void 'Should handle results inconsistency correctly for actionOnFailure set to ActionOnFailure.WARNING'() {
        given: 'Mocks are in place'
        SaucelabsReportingExtension cfg = Stub()
        JUnitTestReport testReport = Stub()
        SauceLabsJobResultReporter sessionHandler = new SauceLabsJobResultReporter(cfg)

        and: 'JUnit test report stub is configured'
        testReport.passed >> false
        testReport.sessionId >> SESSION_ID
        testReport.filename >> FILENAME

        and: 'Expected message is set'
        String message = "\nSauceLabs job '${SESSION_ID}' for ${FILENAME} was not updated\nStatus in Sauce Labs: true\nExpected status: false\n"

        and: 'Plugin extension is configured'
        cfg.actionOnFailure >> ActionOnFailure.WARNING

        when: 'Sauce session status is compared with session status in JUnit report'
        sessionHandler.compareResults(true, testReport)

        then: 'Message is sent to STDOUT as expected'
        out.contains(message)
    }

    void 'Should handle results inconsistency correctly for actionOnFailure set to ActionOnFailure.ERROR'() {
        given: 'Mocks are in place'
        SaucelabsReportingExtension cfg = Stub()
        JUnitTestReport testReport = Stub()

        and: 'Plugin extension is configured'
        cfg.actionOnFailure >> ActionOnFailure.ERROR
        SauceLabsJobResultReporter sessionHandler = new SauceLabsJobResultReporter(cfg)

        and: 'JUnit test report stub is configured'
        testReport.passed >> false

        when: 'Sauce session status is compared with session status in JUnit report'
        sessionHandler.compareResults(true, testReport)

        then: 'Expected action takes place'
        thrown(GradleException)
    }
}
