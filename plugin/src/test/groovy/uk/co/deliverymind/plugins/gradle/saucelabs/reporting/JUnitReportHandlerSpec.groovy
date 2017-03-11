package uk.co.deliverymind.plugins.gradle.saucelabs.reporting

import spock.lang.Specification

class JUnitReportHandlerSpec extends Specification {

    def "Should return list of all JUnit reports recursively"() {
        given:
        String DIR = 'plugin/src/test/resources/report-handler'

        and:
        def expectedJUnitReports = [
                new File("${DIR}/subdir/TEST-DTest.xml").absolutePath,
                new File("${DIR}/subdir/TEST-uk.co.deliverymind.CTest.xml").absolutePath,
                new File("${DIR}/TEST-ATest.xml").absolutePath,
                new File("${DIR}/TEST-BTest.xml").absolutePath
        ]

        when:
        def foundJUnitReports = JUnitReportHandler.getJUnitReports(DIR, /TEST-(.)*\.xml/)

        then:
        foundJUnitReports.containsAll(expectedJUnitReports)
        foundJUnitReports.size() == 4
    }
}
