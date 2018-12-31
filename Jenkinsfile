#!groovy

pipeline {
    agent {
        label 'linux'
    }
    parameters {
        string(name: 'RELEASE_VERSION', defaultValue: '9.0.0', description: '')
        string(name: 'SNAPSHOT_VERSION', defaultValue: '9.0.1-SNAPSHOT', description: '')
        string(name: 'TEST_ONLY', defaultValue: 'true', description: '')
        string(name: 'DRY_RUN', defaultValue: 'true', description: '')
    }
    options {
        timestamps()
        skipDefaultCheckout()
        disableConcurrentBuilds()
        buildDiscarder(logRotator(numToKeepStr: '10'))
    }
    stages {
        stage('Cleanup') {
            steps {
                cleanWs()
            }
        }
        stage('Clone') {
            steps {
                sshagent(['github-creds']) {
                    git credentialsId: 'github-creds', url: 'git@github.com:automatictester/saucelabs-reporting-gradle-plugin.git'
                }
            }
        }
        stage('Set release version number') {
            when {
                expression {
                    "${params.TEST_ONLY}" == "false"
                }
            }
            steps {
                sh "sed -i -e \"/sauceReportingGradlePluginVersion=/ s/=.*/=${params.RELEASE_VERSION}/\" gradle.properties"
                sh "cat gradle.properties"
                sh "git add -A; git commit -m 'Release version bump'"
            }
        }
        stage('Test') {
            steps {
                sh "./gradlew clean test"
            }
            post {
                always {
                    junit 'build/test-results/*.xml'
                }
            }
        }
        stage('Integration test') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'sauce-labs-job-creds', usernameVariable: 'SL_USER', passwordVariable: 'SL_KEY')]) {
                    sh "./gradlew clean check -x test -DSL_USER=${SL_USER} -DSL_KEY=${SL_KEY}"
                }
            }
            post {
                always {
                    junit 'build/test-results/*.xml'
                }
            }
        }
        stage('Tag release') {
            when {
                expression {
                    "${params.TEST_ONLY}" == "false"
                }
            }
            steps {
                sh "git tag ${params.RELEASE_VERSION}"
            }
        }
        stage('Release artefacts') {
            when {
                expression {
                    "${params.TEST_ONLY}" == "false" && "${params.DRY_RUN}" == "false" && "${env.BRANCH_NAME}" == "master"
                }
            }
            steps {
                // Maven Central
                sh "./gradlew clean uploadArchives -i"
                // Gradle Plugin Portal
                sh "./gradlew clean publishPlugins -i"
            }
        }
        stage('Set snapshot version number') {
            when {
                expression {
                    "${params.TEST_ONLY}" == "false"
                }
            }
            steps {
                sh "sed -i -e \"/sauceReportingGradlePluginVersion=/ s/=.*/=${params.SNAPSHOT_VERSION}/\" gradle.properties"
                sh "cat gradle.properties"
                sh "git add -A; git commit -m 'Post-release version bump'"
            }
        }
        stage('Push release to origin') {
            when {
                expression {
                    "${params.TEST_ONLY}" == "false" && "${params.DRY_RUN}" == "false" && "${env.BRANCH_NAME}" == "master"
                }
            }
            steps {
                sshagent(['github-creds']) {
                    sh "git push --set-upstream origin master; git push --tags"
                }
            }
        }
    }
}