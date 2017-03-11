package uk.co.deliverymind.plugins.gradle.saucelabs.reporting

import spock.lang.Specification
import spock.lang.Unroll

class JUnitTestReportSpec extends Specification {

    @Unroll("Should set test status to #passed - failures: #failures, errors: #errors")
    def "Should set test status to #passed - failures: #failures, errors: #errors"() {
        given: 'Fake JUnit test report is loaded'
        JUnitTestReport report = new JUnitTestReport('plugin/src/test/resources/unit/TEST-FakeTest.xml')

        when: 'We override test results'
        report.setPassed(failures, errors)

        then: 'Status is set accordingly'
        report.passed == passed

        and: 'All other values also match'
        report.filename == 'plugin/src/test/resources/unit/TEST-FakeTest.xml'
        report.sessionId == 'xyz'

        where:
        failures | errors | passed
        0        | 0      | true
        0        | 1      | false
        1        | 0      | false
        1        | 1      | false
    }
}
