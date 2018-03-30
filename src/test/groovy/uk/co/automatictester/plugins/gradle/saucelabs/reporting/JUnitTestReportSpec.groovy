package uk.co.automatictester.plugins.gradle.saucelabs.reporting

import spock.lang.Specification
import spock.lang.Unroll

class JUnitTestReportSpec extends Specification {

    @Unroll
    void "should set test status to #passed - failures: #failures, errors: #errors"() {
        given: 'Fake JUnit test report is loaded'
        String junitTestReport = 'src/test/resources/unit/TEST-FakeTest.xml'
        JUnitTestReport report = new JUnitTestReport(junitTestReport)

        when: 'We override test results'
        report.setPassed(failures, errors)

        then: 'Status is set accordingly'
        report.passed == passed

        and: 'All other values also match'
        report.filename == junitTestReport
        report.sessionId == 'xyz'

        where:
        failures | errors | passed
        0        | 0      | true
        0        | 1      | false
        1        | 0      | false
        1        | 1      | false
    }
}
