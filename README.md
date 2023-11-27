# TeamCity Service Messages

TeamCity Service Messages is a simple library designed to make it easier to utilise service messages in java programs.
Internally intended to be used with Kotlin Scripts running in TeamCity. The aim is to reduce the busy work involved in
declaring nested services messages that can have many values set.

The library also implements a basic dsl builder to make it easier to declare nested services messages like tests and
test suites.

```kotlin
val testSuiteResult = testSuite("tests") {
    test("test") {
        val expectedResult = 1
        val testResult = functionThatFails()
        if (testResult != expectedResult) {
            failedComparison(
                "test did not pass",
                "because of reasons",
                actual = testResult.toString(),
                expected = expectedResult.toString()
            )
        }
    }
}

val failedTests = testSuiteResult.totalFailures
```

Results in:

```text
##teamcity[testSuiteStarted name='tests']
##teamcity[testStarted name='test' captureStandardOutput='false']
##teamcity[testFailed name='test' message='test did not pass' details='because of reasons' type='comparisonFailure' actual='0' expected='1']
##teamcity[testFinished name='test' duration='0']
##teamcity[testSuiteFinished name='tests']
```

Single line services messages can be invoked with a function eg: `disableServiceMessages()` these functions print
directly to stdOut when they are executed. If you want greater control over execution you can instantiate the class
directly (for disable service messages that would be `DisableServiceMessages`)

For more examples of how the library can be used take a look at the tests.

## Currently missing

- proper support for flowId (this is supposed to be possible to use in any message)
- proper support for timestamp (this is also generic across all message types)

## Build Setup

For sonatype
login [create a token username/password pair](https://blog.solidsoft.pl/2015/09/08/deploy-to-maven-central-using-api-key-aka-auth-token/)
and set them as env variables.

- `ORG_GRADLE_PROJECT_sonatypeUsername`
- `ORG_GRADLE_PROJECT_sonatypePassword`

For pgp
key [use in-memory ascii-armored OpenPGP subkeys](https://docs.gradle.org/current/userguide/signing_plugin.html#using_in_memory_ascii_armored_openpgp_subkeys)

- `ORG_GRADLE_PROJECT_signingKey`
- `ORG_GRADLE_PROJECT_signingPassword`