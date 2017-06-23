# saucelabs-reporting-gradle-plugin
Sauce Labs Reporting Gradle Plugin

[![Central status](https://maven-badges.herokuapp.com/maven-central/uk.co.deliverymind/saucelabs-reporting-gradle-plugin/badge.svg)](https://maven-badges.herokuapp.com/maven-central/uk.co.deliverymind/saucelabs-reporting-gradle-plugin)

Gradle plugin which reports test results back to [Sauce Labs](https://saucelabs.com).

How does this plugin work:
- traverses recursively given location in you file system to retrieve all JUnit reports in XML format produced by your unit test framework
- parses them looking for **SauceOnDemandSessionID**
- reports test results back to Sauce Labs
- for every result reported back, it checks if Sauce Labs session was correctly updated
- if not, it gives a warning or an error
- it is unit test framework-agnostic, as long as produced reports are in JUnit XML format
- it expects your test classes to be in 1-to-1 relationship with Sauce Labs sessions

## Quick start guide

Add plugin to your **build.gradle**:

```
[...]

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        // check maven central badge above for most recent released version number
        classpath "uk.co.deliverymind:saucelabs-reporting-gradle-plugin:1.0.0"
    }
}

apply plugin: 'uk.co.deliverymind.saucelabs-reporting'

saucelabsReportingSettings {

    // Filename pattern of JUnit reports. In most cases, stick to the default.
    // filenamePattern = /TEST-(.)*\.xml/

    // Where to find JUnit reports. This will depend on your project setup and Gradle version being used.
    testResultsDir = 'build/test-results'

    // Sauce Labs credentials.
    user = <your-username>
    key = <your-access-key>

    // What to do if session status after updated is different from expected. Either ERROR or WARNING (default).
    // actionOnFailure = 'WARNING'
}

[...]
```

After you run your tests, report the results to Sauce Labs:

```gradle reportToSauceLabs```
