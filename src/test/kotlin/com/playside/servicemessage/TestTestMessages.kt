package com.playside.servicemessage

import com.github.stefanbirkner.systemlambda.SystemLambda.tapSystemOutNormalized
import kotlin.test.Test
import kotlin.test.assertEquals

internal class TestTestMessages {
  private val testSuiteName = "tests"
  private val testName = "test"

  @Test
  fun testSuccess() {
    var testSuiteResult: TESTS? = null
    val output = tapSystemOutNormalized {
      testSuiteResult = testSuite(testSuiteName) { test(testName) {} }
    }

    assertEquals(
        """
##teamcity[testSuiteStarted name='$testSuiteName']
##teamcity[testStarted name='$testName' captureStandardOutput='false']
##teamcity[testFinished name='$testName' duration='${testSuiteResult!!.children[0].duration}']
##teamcity[testSuiteFinished name='$testSuiteName']
"""
            .trimIndent(),
        output.trim())
    assertEquals(0, testSuiteResult!!.totalFailures)
  }

  @Test
  fun testIgnored() {
    var testSuiteResult: TESTS? = null
    val output = tapSystemOutNormalized {
      testSuiteResult =
          testSuite(testSuiteName) {
            test(testName) {
              ignore("ignore this test")
              failed("test_failed", "this should be ignored")
            }
          }
    }
    assertEquals(
        """
##teamcity[testSuiteStarted name='$testSuiteName']
##teamcity[testStarted name='$testName' captureStandardOutput='false']
##teamcity[testIgnored name='$testName' message='ignore this test']
##teamcity[testFailed name='$testName' message='test_failed' details='this should be ignored']
##teamcity[testFinished name='$testName' duration='${testSuiteResult!!.children[0].duration}']
##teamcity[testSuiteFinished name='$testSuiteName']
"""
            .trimIndent(),
        output.trim())
    assertEquals(0, testSuiteResult!!.totalFailures)
  }

  @Test
  fun testMultipleIgnored() {
    var testSuiteResult: TESTS? = null
    val output = tapSystemOutNormalized {
      testSuiteResult =
          testSuite(testSuiteName) {
            test("test 2") { failed("test_failed", "also failed") }
            test("test 1") {
              ignore("ignore this test")
              failed("test_failed", "failed")
            }
          }
    }
    assertEquals(
        """
##teamcity[testSuiteStarted name='$testSuiteName']
##teamcity[testStarted name='test 2' captureStandardOutput='false']
##teamcity[testFailed name='test 2' message='test_failed' details='also failed']
##teamcity[testFinished name='test 2' duration='${testSuiteResult!!.children[0].duration}']
##teamcity[testStarted name='test 1' captureStandardOutput='false']
##teamcity[testIgnored name='test 1' message='ignore this test']
##teamcity[testFailed name='test 1' message='test_failed' details='failed']
##teamcity[testFinished name='test 1' duration='${testSuiteResult!!.children[1].duration}']
##teamcity[testSuiteFinished name='$testSuiteName']
"""
            .trimIndent(),
        output.trim())
    assertEquals(1, testSuiteResult!!.totalFailures)
    assertEquals(2, testSuiteResult!!.children.size)
  }

  @Test
  fun testFailure() {
    var testSuiteResult: TESTS? = null

    val stdOut = tapSystemOutNormalized {
      testSuiteResult =
          testSuite(testSuiteName) {
            test(testName) {
              val expectedResult = 1
              val testResult = functionThatFails()
              if (testResult != expectedResult) {
                failedComparison(
                    "test did not pass",
                    "because of reasons",
                    actual = testResult.toString(),
                    expected = expectedResult.toString())
              }
            }
          }
    }

    assertEquals(
        """
##teamcity[testSuiteStarted name='$testSuiteName']
##teamcity[testStarted name='$testName' captureStandardOutput='false']
##teamcity[testFailed name='$testName' message='test did not pass' details='because of reasons' type='comparisonFailure' actual='0' expected='1']
##teamcity[testFinished name='$testName' duration='${testSuiteResult!!.children[0].duration}']
##teamcity[testSuiteFinished name='$testSuiteName']
"""
            .trimIndent(),
        stdOut.trim())
    assertEquals(1, testSuiteResult!!.totalFailures)
  }

  @Suppress("SameReturnValue")
  private fun functionThatFails(): Int {
    return 0
  }
}
