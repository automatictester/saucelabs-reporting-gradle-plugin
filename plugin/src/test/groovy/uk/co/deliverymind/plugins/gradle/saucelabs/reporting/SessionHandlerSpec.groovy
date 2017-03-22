package uk.co.deliverymind.plugins.gradle.saucelabs.reporting

import groovy.ui.SystemOutputInterceptor
import org.gradle.api.GradleException
import spock.lang.Specification
import spock.lang.Unroll
import uk.co.deliverymind.plugins.gradle.saucelabs.reporting.extension.SaucelabsReportingExtension

class SessionHandlerSpec extends Specification {

    static final String SESSION_ID = 'xyz'
    static final String FILENAME = 'TEST-SampleTest.xml'

    SystemOutputInterceptor interceptor
    String out

    def setup() {
        out = ''
        interceptor = new SystemOutputInterceptor({ out += it; false })
        interceptor.start()
    }

    def cleanup() {
        interceptor.stop()
    }

    @Unroll("Should detect results inconsistency correctly: #sauceResult - #reportResult")
    def "Should detect results inconsistency correctly: #sauceResult - #reportResult"() {
        given: 'Mocks are in place'
        SaucelabsReportingExtension cfg = Stub()
        JUnitTestReport testReport = Stub()

        SessionHandler sessionHandler = new SessionHandler(cfg)

        and: 'JUnit test report stub is configured'
        testReport.passed >> reportResult
        testReport.sessionId >> SESSION_ID
        testReport.filename >> FILENAME

        and: 'Plugin extension is configured'
        cfg.actionOnFailure >> ActionOnFailure.WARNING

        when: 'Sauce session status is compared with session status in JUnit report'
        sessionHandler.handleSessionStatusUpdate(sauceResult, testReport)

        then: 'Inconsistency is correctly handled'
        if (isInconsistent) {
            assert out.contains(message)
        } else {
            assert out == ''
        }

        where:
        sauceResult | reportResult | isInconsistent | message
        true        | true         | false          | null
        true        | false        | true           | "\nSession '${SESSION_ID}' for ${FILENAME} was not updated\nStatus in Sauce Labs: true\nExpected status: false\n"
        false       | true         | true           | "\nSession '${SESSION_ID}' for ${FILENAME} was not updated\nStatus in Sauce Labs: false\nExpected status: true\n"
        false       | false        | false          | null
        null        | true         | true           | "\nSession '${SESSION_ID}' for ${FILENAME} was not updated\nStatus in Sauce Labs: null\nExpected status: true\n"
        null        | false        | true           | "\nSession '${SESSION_ID}' for ${FILENAME} was not updated\nStatus in Sauce Labs: null\nExpected status: false\n"
    }

    def 'Should handle results inconsistency correctly for actionOnFailure set to ActionOnFailure.WARNING'() {
        given: 'Mocks are in place'
        SaucelabsReportingExtension cfg = Stub()
        JUnitTestReport testReport = Stub()
        SessionHandler sessionHandler = new SessionHandler(cfg)

        and: 'JUnit test report stub is configured'
        testReport.passed >> false
        testReport.sessionId >> SESSION_ID
        testReport.filename >> FILENAME

        and: 'Expected message is set'
        String MESSAGE = "\nSession '${SESSION_ID}' for ${FILENAME} was not updated\nStatus in Sauce Labs: true\nExpected status: false\n"

        and: 'Plugin extension is configured'
        cfg.actionOnFailure >> ActionOnFailure.WARNING

        when: 'Sauce session status is compared with session status in JUnit report'
        sessionHandler.handleSessionStatusUpdate(true, testReport)

        then: 'Message is sent to STDOUT as expected'
        out.contains(MESSAGE)
    }

    def 'Should handle results inconsistency correctly for actionOnFailure set to ActionOnFailure.ERROR'() {
        given: 'Mocks are in place'
        SaucelabsReportingExtension cfg = Stub()
        JUnitTestReport testReport = Stub()
        SessionHandler sessionHandler = new SessionHandler(cfg)

        and: 'JUnit test report stub is configured'
        testReport.passed >> false

        and: 'Plugin extension is configured'
        cfg.actionOnFailure >> ActionOnFailure.ERROR

        when: 'Sauce session status is compared with session status in JUnit report'
        sessionHandler.handleSessionStatusUpdate(true, testReport)

        then: 'Expected action takes place'
        GradleException e = thrown()
    }
}
