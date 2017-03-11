package uk.co.deliverymind.plugins.gradle.saucelabs.reporting.it

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import spock.lang.Specification
import spock.lang.Unroll
import uk.co.deliverymind.plugins.gradle.saucelabs.reporting.JUnitReportHandler
import uk.co.deliverymind.plugins.gradle.saucelabs.reporting.JUnitTestReport
import uk.co.deliverymind.plugins.gradle.saucelabs.reporting.SessionHandler
import uk.co.deliverymind.plugins.gradle.saucelabs.reporting.extension.SaucelabsReportingExtension
import uk.co.deliverymind.plugins.gradle.saucelabs.reporting.helpers.SessionCleanupHelper

import static org.gradle.internal.impldep.org.hamcrest.MatcherAssert.assertThat
import static org.gradle.internal.impldep.org.hamcrest.core.Is.is
import static org.gradle.testkit.runner.TaskOutcome.FAILED
import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

class ReportToSauceLabsTaskIntegrationSpec extends Specification {

    @Unroll("Should report results back to Sauce Labs - Gradle #version")
    def "Should report results back to Sauce Labs - Gradle #version"() {

        given: 'There is a project configured to use saucelabs-reporting plugin'
        GradleRunner runner = GradleRunner.create()
                .withGradleVersion(version)
                .withProjectDir(new File('src/test/resources'))
                .withPluginClasspath()

        when: 'clean task is run'
        BuildResult cleanResult = runner
                .withArguments(':clean')
                .build()
        println cleanResult.output

        then: 'clean task ends with status success'
        assertThat(cleanResult.task(':clean').getOutcome(), is(SUCCESS))

        when: 'test task is run'
        BuildResult testResult = runner
                .withArguments(':test')
                .buildAndFail()
        println testResult.output

        then: 'test task ends with status failed'
        assertThat(testResult.task(':test').getOutcome(), is(FAILED))

        when: 'reportToSauceLabs task is run'
        BuildResult reportResult = runner
                .withArguments(':reportToSauceLabs')
                .build()
        println reportResult.output

        then: 'reportToSauceLabs task ends with status success'
        assertThat(reportResult.task(':reportToSauceLabs').getOutcome(), is(SUCCESS))

        cleanup: 'Sauce Labs sessions created by this integration test are deleted'
        List<String> junitReportFiles = JUnitReportHandler.getJUnitReports('src/test/resources/build/test-results', /TEST-(.)*\.xml/)

        junitReportFiles.each {
            JUnitTestReport testReport = new JUnitTestReport(it)
            SessionCleanupHelper.deleteSession(testReport)
        }

        where: 'Gradle versions'
        version << ['2.8', '2.14', '3.0', '3.4.1']
    }
}
