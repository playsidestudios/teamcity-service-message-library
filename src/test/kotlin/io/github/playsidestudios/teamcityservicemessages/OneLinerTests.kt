package io.github.playsidestudios.teamcityservicemessages

import com.github.stefanbirkner.systemlambda.SystemLambda.tapSystemOutNormalized
import kotlin.test.Test
import kotlin.test.assertEquals

internal class OneLinerTests {
  @Test
  fun testEnableServiceMessages() {
    val enableExpected = "##teamcity[enableServiceMessages]"
    assertEquals(enableExpected, EnableServiceMessages().toString())
    val stdOut = tapSystemOutNormalized { enableServiceMessages() }
    assertEquals(enableExpected, stdOut.trim())
  }

  @Test
  fun testDisableServiceMessages() {
    val disableExpected = "##teamcity[disableServiceMessages]"
    assertEquals(disableExpected, DisableServiceMessages().toString())
    val stdOut = tapSystemOutNormalized { disableServiceMessages() }
    assertEquals(disableExpected, stdOut.trim())
  }
}
