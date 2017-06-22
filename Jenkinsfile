#!groovy

pipeline {
    agent any
    tools {
        gradle 'GRADLE_2.14'
        jdk 'jdk8'
    }
    options {
        timestamps()
        skipDefaultCheckout()
    }
    stages {
        stage('Cleanup') {
            steps {
                step([$class: 'WsCleanup'])
            }
        }
        stage('Clone') {
            steps {
                sshagent(["${GIT_CREDENTIALS_ID}"]) {
                    sh "git clone ${REPO_URL} ."
                }
            }
        }
        stage('Purge') {
            steps {
                sh 'rm -rf ~/.m2/repository/uk/co/deliverymind/'
            }
        }
        stage('Set release version number') {
            when {
                expression {
                    "${TEST_ONLY}" == "false"
                }
            }
            steps {
                sh "(cd plugin; sed -i -e \"/sauceReportingGradlePluginVersion=/ s/=.*/=${RELEASE_VERSION}/\" gradle.properties)"
                sh "(cd plugin; cat gradle.properties)"
                sh "(cd plugin-it; sed -i -e \"/sauceReportingGradlePluginVersion=/ s/=.*/=${RELEASE_VERSION}/\" gradle.properties)"
                sh "(cd plugin-it; cat gradle.properties)"
                sh "git add -A; git commit -m 'Release version bump'"
            }
        }
        stage('Test') {
            steps {
                sh "(cd plugin; gradle clean check -DSL_USER=${SL_USER} -DSL_KEY=${SL_KEY})"
            }
            post {
                always {
                    junit 'plugin/build/test-results/*.xml'
                }
            }
        }
        stage('Install') {
            steps {
                sh "(cd plugin; gradle clean pTML -x test -x integrationTest)"
            }
        }
        stage('E2E test') {
            steps {
                sh "(cd plugin-it; gradle clean test reportToSauceLabs -DSL_USER=${SL_USER} -DSL_KEY=${SL_KEY})"
            }
        }
        stage('Tag release') {
            when {
                expression {
                    "${TEST_ONLY}" == "false"
                }
            }
            steps {
                sh "git tag ${RELEASE_VERSION}"
            }
        }
        stage('Release artefacts') {
            when {
                expression {
                    "${TEST_ONLY}" == "false" && "${DRY_RUN}" == "false"
                }
            }
            steps {
                sh "(cd plugin; gradle clean uploadArchives -i)"
            }
        }
        stage('Set snapshot version number') {
            when {
                expression {
                    "${TEST_ONLY}" == "false"
                }
            }
            steps {
                sh "(cd plugin; sed -i -e \"/sauceReportingGradlePluginVersion=/ s/=.*/=${SNAPSHOT_VERSION}/\" gradle.properties)"
                sh "(cd plugin; cat gradle.properties)"
                sh "(cd plugin-it; sed -i -e \"/sauceReportingGradlePluginVersion=/ s/=.*/=${SNAPSHOT_VERSION}/\" gradle.properties)"
                sh "(cd plugin-it; cat gradle.properties)"
                sh "git add -A; git commit -m 'Post-release version bump'"
            }
        }
        stage('Push release to origin') {
            when {
                expression {
                    "${TEST_ONLY}" == "false" && "${DRY_RUN}" == "false"
                }
            }
            steps {
                sshagent(["${GIT_CREDENTIALS_ID}"]) {
                    sh "git push --set-upstream origin master; git push --tags"
                }
            }
        }
    }
}