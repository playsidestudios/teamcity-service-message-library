package com.playside.servicemessage

import com.github.stefanbirkner.systemlambda.SystemLambda.tapSystemOutNormalized
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
##teamcity[testSuiteStarted name='tests']
##teamcity[testStarted name='test' captureStandardOutput='false']
##teamcity[testFinished name='test' duration='${testSuiteResult!!.children[0].duration}']
##teamcity[testSuiteFinished name='tests']
"""
            .trimIndent(),
        output.trim())
  }
}
