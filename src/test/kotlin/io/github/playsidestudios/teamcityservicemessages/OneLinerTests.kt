package io.github.playsidestudios.teamcityservicemessages

import com.github.stefanbirkner.systemlambda.SystemLambda.tapSystemOutNormalized
import kotlin.test.Test
import kotlin.test.assertEquals

internal class OneLinerTests {
  @Test
  fun testEnableServiceMessages() {
    val enableExpected = "##teamcity[enableServiceMessages flowId='test']"
    val stdOut = tapSystemOutNormalized { enableServiceMessages("test") }
    assertEquals(enableExpected, stdOut.trim())
  }

  @Test
  fun testDisableServiceMessages() {
    val disableExpected = "##teamcity[disableServiceMessages]"
    val stdOut = tapSystemOutNormalized { disableServiceMessages() }
    assertEquals(disableExpected, stdOut.trim())
  }

  @Test
  fun testArtifactPublishMessage() {
    val path = "+:test/**/* => test.zip"
    val expected = "##teamcity[publishArtifacts '${path}']"
    val stdOut = tapSystemOutNormalized { publishArtifact(path) }
    assertEquals(expected, stdOut.trim())
  }

  @Test
  fun testGenericMessage() {
    val expected = "##teamcity[message text='text' status='NORMAL']"
    val message = GenericMessage("text")
    assertEquals(expected, message.toString())
    val stdOut = tapSystemOutNormalized { message.print() }
    assertEquals(expected, stdOut.trim())
  }

  @Test
  fun testErrorGenericMessage() {
    val expected =
        "##teamcity[message text='failure' status='ERROR' errorDetails='failed for some reason' flowId='errorFlow']"
    val message = GenericMessage("failure", Status.ERROR, "failed for some reason", "errorFlow")
    assertEquals(expected, message.toString())
    val stdOut = tapSystemOutNormalized { message.print() }
    assertEquals(expected, stdOut.trim())
  }
}
