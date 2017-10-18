#!groovy

multibranchPipelineJob('saucelabs-reporting-gradle-plugin') {
    branchSources {
        git {
            remote('git@github.com:deliverymind/saucelabs-reporting-gradle-plugin.git')
            credentialsId('github-creds')
        }
    }
}
