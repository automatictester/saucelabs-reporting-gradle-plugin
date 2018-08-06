package uk.co.automatictester.plugins.gradle.saucelabs.reporting

import spock.lang.Specification
import spock.lang.Unroll

class JUnitReportSpec extends Specification {

    @Unroll
    void "should set test status to #passed - failures: #failures, errors: #errors"() {
        when: 'JUnit test report is loaded'
        String junitResultFile = "src/test/resources/unit/TEST-FakeTest-${failures}${errors}.xml"
        JUnitReport junitTestReport = new JUnitReport(junitResultFile)

        then: 'Status is set accordingly'
        junitTestReport.passed == passed

        and: 'All other values also match'
        junitTestReport.filename == junitResultFile
        junitTestReport.sessionId == 'xyz'

        where:
        failures | errors | passed
        0        | 0      | true
        0        | 1      | false
        1        | 0      | false
        1        | 1      | false
    }
}
