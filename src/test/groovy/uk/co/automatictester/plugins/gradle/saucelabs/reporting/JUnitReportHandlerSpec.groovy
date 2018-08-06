package uk.co.automatictester.plugins.gradle.saucelabs.reporting

import spock.lang.Specification

class JUnitReportHandlerSpec extends Specification {

    void "Should return list of all JUnit reports recursively"() {
        given:
        String dir = 'src/test/resources/report-handler'

        and:
        List<File> expectedJUnitReports = [
                new File("${dir}/subdir/TEST-DTest.xml").absolutePath,
                new File("${dir}/subdir/TEST-uk.co.automatictester.CTest.xml").absolutePath,
                new File("${dir}/TEST-ATest.xml").absolutePath,
                new File("${dir}/TEST-BTest.xml").absolutePath,
        ]

        when:
        List<String> foundJUnitReports = JUnitReportHandler.getJUnitReportFiles(dir, /TEST-(.)*\.xml/)

        then:
        foundJUnitReports.containsAll(expectedJUnitReports)
        foundJUnitReports.size() == 4
    }
}
