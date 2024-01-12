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
  fun slackNotification() {
    val connectionID = "CONNECTION_ID_1"
    val channelID = "C052UHDRZU7"
    val message =
        """
      test multiline message
      `markdown allowed`
    """
            .trimIndent()
    val stdOut = tapSystemOutNormalized { sendSlackMessage(message, channelID, connectionID) }
    assertEquals(
        "##teamcity[notification notifier='${NotifierTypes.slack.name}' message='test multiline message|n`markdown allowed`' connectionID='$connectionID' sendTo='$channelID']",
        stdOut.trim())
  }

  @Test
  fun testBuildStatisticsInteger() {
    val key = "test"
    val value = 11111111
    val expected = "##teamcity[buildStatisticValue key='$key' value='$value']"
    val stdOut = tapSystemOutNormalized { buildStatisticValue(key, value) }
    assertEquals(expected, stdOut.trim())
  }

  @Test
  fun testBuildStatisticsFloat() {
    val key = "test"
    val value = 1.11111F
    val expected = "##teamcity[buildStatisticValue key='$key' value='$value']"
    val stdOut = tapSystemOutNormalized { buildStatisticValue(key, value) }
    println(stdOut)
    assertEquals(expected, stdOut.trim())
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
