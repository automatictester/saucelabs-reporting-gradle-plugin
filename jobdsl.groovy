#!groovy

folder('saucelabs-reporting-gradle-plugin')

def gitCreds = '11be7a79-8034-407b-8351-dbd1d3342c24'

pipelineJob('saucelabs-reporting-gradle-plugin/build') {
    concurrentBuild(false)
    properties {
        buildDiscarder {
            strategy {
                logRotator {
                    numToKeepStr('10')
                    daysToKeepStr(null)
                    artifactDaysToKeepStr(null)
                    artifactNumToKeepStr(null)
                }
            }
        }
        githubProjectProperty {
            projectUrlStr('https://github.com/deliverymind/saucelabs-reporting-gradle-plugin/')
        }
        rebuildSettings {
            autoRebuild(true)
            rebuildDisabled(false)
        }
    }
    parameters {
        stringParam('RELEASE_VERSION', '1.0.0', '')
        stringParam('SNAPSHOT_VERSION', '1.0.1-SNAPSHOT', '')
        stringParam('REPO_URL', 'git@github.com:deliverymind/saucelabs-reporting-gradle-plugin.git', 'Or local path, e.g.: "file:///Users/username/git/saucelabs-reporting-gradle-plugin"')
        stringParam('GIT_CREDENTIALS_ID', gitCreds, '')
        stringParam('GIT_BRANCH', 'master', '')
        stringParam('SL_USER', '', '')
        nonStoredPasswordParam('SL_KEY', '')
        stringParam('TEST_ONLY', 'true', '')
        stringParam('DRY_RUN', 'true', '')
    }
    definition {
        cpsScm {
            scm {
                git {
                    branch('*/master')
                    remote {
                        credentials(gitCreds)
                        url('$REPO_URL')
                    }
                }
            }
            scriptPath('Jenkinsfile')
        }
    }
}
