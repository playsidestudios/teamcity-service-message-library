package com.playside.servicemessage

import com.github.stefanbirkner.systemlambda.SystemLambda.tapSystemOutNormalized
import kotlinx.html.body
import kotlinx.html.html
import kotlinx.html.p
import kotlinx.html.stream.createHTML
import kotlin.io.path.Path
import kotlin.test.Test
import kotlin.test.assertEquals

internal class OutputTests {
  val testSuiteName = "tests"
  val testName = "test"

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
  fun inspectionOutput() {
    val stdOut = tapSystemOutNormalized {
      inspection(
          "test", Path("nofile"), 1, message = "test message", severity = InspectionSeverity.ERROR)
    }

    assertEquals(
        "##teamcity[inspection typeId='test' message='test message' file='nofile' line='1' SEVERITY='ERROR']",
        stdOut.trim())
  }

  @Test
  fun inspectionTypeOutput() {
    val testId = "test"
    val descriptionHtml = createHTML().html { body { p { +"Description in html" } } }
    val stdOut = tapSystemOutNormalized {
      inspectionType(testId, "name", "category", descriptionHtml)
    }
    assertEquals(
        "##teamcity[inspectionType id='$testId' name='name' category='category' description='<html>|n  <body>|n    <p>Description in html</p>|n  </body>|n</html>|n']",
        stdOut.trim())
  }
}
