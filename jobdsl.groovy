#!groovy

multibranchPipelineJob('saucelabs-reporting-gradle-plugin') {
    branchSources {
        git {
            remote('git@github.com:automatictester/saucelabs-reporting-gradle-plugin.git')
            credentialsId('github-creds')
        }
    }
}
