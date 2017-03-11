package uk.co.deliverymind.plugins.gradle.saucelabs.reporting.it

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.BuildTask
import org.gradle.testkit.runner.GradleRunner
import spock.lang.Specification
import spock.lang.Unroll
import uk.co.deliverymind.plugins.gradle.saucelabs.reporting.JUnitReportHandler
import uk.co.deliverymind.plugins.gradle.saucelabs.reporting.JUnitTestReport
import uk.co.deliverymind.plugins.gradle.saucelabs.reporting.helpers.SessionCleanupHelper

import static org.gradle.internal.impldep.org.hamcrest.MatcherAssert.assertThat
import static org.gradle.internal.impldep.org.hamcrest.core.Is.is
import static org.gradle.testkit.runner.TaskOutcome.*

class ReportToSauceLabsTaskIntegrationSpec extends Specification {

    @Unroll("Should report results back to Sauce Labs - Gradle #version")
    def "Should report results back to Sauce Labs - Gradle #version"() {

        given: 'There is a project configured to use saucelabs-reporting plugin'
        GradleRunner runner = GradleRunner.create()
                .withGradleVersion(version)
                .withProjectDir(new File('src/test/resources/it'))
                .withPluginClasspath()

        when: 'clean task is run'
        BuildResult cleanResult = runner
                .withArguments(':clean')
                .build()
        println cleanResult.output

        then: 'clean task ends with status success'
        assertThat(isTaskSuccessOrUpToDate(cleanResult.task(':clean')), is(true))

        when: 'test task is run'
        BuildResult testResult = runner
                .withArguments(':test')
                .buildAndFail()
        println testResult.output

        then: 'test task ends with status failed'
        assertThat(testResult.task(':test').outcome, is(FAILED))
        assertThat(testResult.output.contains('3 tests completed, 1 failed'), is(true))

        when: 'reportToSauceLabs task is run'
        BuildResult reportResult = runner
                .withArguments(':reportToSauceLabs')
                .build()
        println reportResult.output

        then: 'reportToSauceLabs task ends with status success'
        assertThat(reportResult.task(':reportToSauceLabs').outcome, is(SUCCESS))

        cleanup: 'Sauce Labs sessions created by this integration test are deleted'
        List<String> junitReportFiles = JUnitReportHandler.getJUnitReports('src/test/resources/it/build/test-results', /TEST-(.)*\.xml/)

        junitReportFiles.each {
            JUnitTestReport testReport = new JUnitTestReport(it)
            SessionCleanupHelper.deleteSession(testReport)
        }

        where: 'Gradle versions'
        version << ['2.8', '2.14', '3.0', '3.4.1']
    }

    boolean isTaskSuccessOrUpToDate(BuildTask task) {
        (task.outcome == SUCCESS) || (task.outcome == UP_TO_DATE)
    }
}
