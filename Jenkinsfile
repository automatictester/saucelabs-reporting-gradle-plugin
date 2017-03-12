#!groovy

def purge() {
    sh 'rm -rf ~/.m2/repository/uk/co/deliverymind/'
}

def setReleaseVersion() {
    sh "(cd plugin; sed -i -e \"/sauceReportingGradlePluginVersion=/ s/=.*/=${RELEASE_VERSION}/\" gradle.properties)"
    sh "(cd plugin; cat gradle.properties)"
    sh "(cd plugin-it; sed -i -e \"/sauceReportingGradlePluginVersion=/ s/=.*/=${RELEASE_VERSION}/\" gradle.properties)"
    sh "(cd plugin-it; cat gradle.properties)"
    sh "git add -A; git commit -m 'Release version bump'"
}

def setSnapshotVersion() {
    sh "(cd plugin; sed -i -e \"/sauceReportingGradlePluginVersion=/ s/=.*/=${SNAPSHOT_VERSION}/\" gradle.properties)"
    sh "(cd plugin; cat gradle.properties)"
    sh "(cd plugin-it; sed -i -e \"/sauceReportingGradlePluginVersion=/ s/=.*/=${SNAPSHOT_VERSION}/\" gradle.properties)"
    sh "(cd plugin-it; cat gradle.properties)"
    sh "git add -A; git commit -m 'Post-release version bump'"
}

def install() {
    sh "./gradlew :saucelabs-reporting-gradle-plugin:clean :saucelabs-reporting-gradle-plugin:pTML -x :saucelabs-reporting-gradle-plugin:test"
}

def test() {
    sh "./gradlew :saucelabs-reporting-gradle-plugin:clean :saucelabs-reporting-gradle-plugin:test -DSL_USER=${SL_USER} -DSL_KEY=${SL_KEY}"
}

def runE2ETest() {
//    sh "./gradlew :saucelabs-reporting-gradle-plugin-it:clean :saucelabs-reporting-gradle-plugin-it:test :saucelabs-reporting-gradle-plugin-it:reportToSauceLabs -DSL_USER=${SL_USER} -DSL_KEY=${SL_KEY}"
}

def tagRelease() {
    sh "git tag ${RELEASE_VERSION}"
}

def release() {
    sh "./gradlew :saucelabs-reporting-gradle-plugin:clean :saucelabs-reporting-gradle-plugin:uploadArchives -i"
}

def push() {
    sshagent(["${GIT_CREDENTIALS_ID}"]) {
        sh "git push --set-upstream origin master; git push --tags"
    }
}

def cleanupWorkspace() {
//    step([$class: 'WsCleanup'])
}

def isNotTestOnly() {
    "${TEST_ONLY}" == "false"
}

def isNotDryRunOnly() {
    "${TEST_ONLY}" == "false" && "${DRY_RUN}" == "false"
}

pipeline {
    agent any
    tools {
        gradle 'GRADLE_2.14'
        jdk 'jdk8'
    }
    options {
        timestamps()
    }
    stages {
        stage('Purge') {
            steps {
                purge()
            }
        }
        stage('Test') {
            steps {
                test()
            }
        }
        stage('Install') {
            steps {
                install()
            }
        }
        stage('E2E test') {
            steps {
                runE2ETest()
            }
        }
        stage('Set release version number') {
            when {
                expression {
                    isNotTestOnly()
                }
            }
            steps {
                setReleaseVersion()
            }
        }
        stage('Tag release') {
            when {
                expression {
                    isNotTestOnly()
                }
            }
            steps {
                tagRelease()
            }
        }
        stage('Release artefacts') {
            when {
                expression {
                    isNotDryRunOnly()
                }
            }
            steps {
                release()
            }
        }
        stage('Purge - snapshot') {
            when {
                expression {
                    isNotTestOnly()
                }
            }
            steps {
                purge()
            }
        }
        stage('Test - snapshot') {
            when {
                expression {
                    isNotTestOnly()
                }
            }
            steps {
                test()
            }
        }
        stage('Install - snapshot') {
            when {
                expression {
                    isNotTestOnly()
                }
            }
            steps {
                install()
            }
        }
        stage('E2E test - snapshot') {
            when {
                expression {
                    isNotTestOnly()
                }
            }
            steps {
                runE2ETest()
            }
        }
        stage('Set snapshot version number') {
            when {
                expression {
                    isNotTestOnly()
                }
            }
            steps {
                setSnapshotVersion()
            }
        }
        stage('Push release to origin') {
            when {
                expression {
                    isNotDryRunOnly()
                }
            }
            steps {
                push()
            }
        }
        stage('Cleanup') {
            steps {
                cleanupWorkspace()
            }
        }
    }
}