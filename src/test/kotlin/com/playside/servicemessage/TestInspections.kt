package com.playside.servicemessage

import com.github.stefanbirkner.systemlambda.SystemLambda
import kotlin.io.path.Path
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.html.body
import kotlinx.html.html
import kotlinx.html.p
import kotlinx.html.stream.createHTML

internal class TestInspections {
  @Test
  fun inspectionOutput() {
    val stdOut =
        SystemLambda.tapSystemOutNormalized {
          inspection(
              "test",
              Path("file"),
              1,
              message = "test message",
              severity = InspectionSeverity.ERROR)
        }

    assertEquals(
        "##teamcity[inspection typeId='test' message='test message' file='file' line='1' SEVERITY='ERROR']",
        stdOut.trim())
  }

  @Test
  fun inspectionTypeOutput() {
    val testId = "test"
    val descriptionHtml = createHTML().html { body { p { +"Description in html" } } }
    val stdOut =
        SystemLambda.tapSystemOutNormalized {
          inspectionType(testId, "name", "category", descriptionHtml)
        }
    assertEquals(
        "##teamcity[inspectionType id='$testId' name='name' category='category' description='<html>|n  <body>|n    <p>Description in html</p>|n  </body>|n</html>|n']",
        stdOut.trim())
  }
}
