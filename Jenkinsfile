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
    sh "./gradlew :plugin:clean :plugin:pTML -x :plugin:test"
}

def test() {
    sh "./gradlew :plugin:clean :plugin:test -DSL_USER=${SL_USER} -DSL_KEY=${SL_KEY}"
}

def runITs() {
//    sh "./gradlew :plugin-it:clean :plugin-it:test :plugin-it:reportToSauceLabs -DSL_USER=${SL_USER} -DSL_KEY=${SL_KEY}"
}

def tagRelease() {
    sh "git tag ${RELEASE_VERSION}"
}

def release() {
    sh "./gradlew :plugin:clean :plugin:uploadArchives -i"
}

def push() {
    sshagent(["${GIT_CREDENTIALS_ID}"]) {
        sh "git push --set-upstream origin master; git push --tags"
    }
}

def cleanupWorkspace() {
    step([$class: 'WsCleanup'])
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
        stage('Test, install and run E2E ITs') {
            steps {
                test()
                install()
                runITs()
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
        stage('Test, install and run E2E ITs - snapshot') {
            when {
                expression {
                    isNotTestOnly()
                }
            }
            steps {
                test()
                install()
                runITs()
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
        stage('Push release to origin/master') {
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