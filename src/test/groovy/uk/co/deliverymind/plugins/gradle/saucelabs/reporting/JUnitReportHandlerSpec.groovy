package uk.co.deliverymind.plugins.gradle.saucelabs.reporting

import spock.lang.Specification

class JUnitReportHandlerSpec extends Specification {

    def "Should return list of all JUnit reports recursively"() {

        given:
        def expectedJUnitReports = [
                'src/test/resources/report-handler/subdir/TEST-DTest.xml',
                'src/test/resources/report-handler/subdir/TEST-uk.co.deliverymind.CTest.xml',
                'src/test/resources/report-handler/TEST-ATest.xml',
                'src/test/resources/report-handler/TEST-BTest.xml'
        ]

        when:
        def foundJUnitReports = JUnitReportHandler.getJUnitReports('src/test/resources/report-handler', /TEST-(.)*\.xml/)

        then:
        foundJUnitReports.containsAll(expectedJUnitReports)
        foundJUnitReports.size() == 4
    }
}
