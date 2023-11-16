package com.playside.servicemessage

import kotlin.test.Test
import kotlin.test.assertEquals

internal class MessageTests {
  @Test
  fun testEnableServiceMessages() {
    val enableExpected = "##teamcity[enableServiceMessages]"
    assertEquals(enableExpected, EnableServiceMessages().toString())
  }

  @Test
  fun testDisableServiceMessages() {
    val disableExpected = "##teamcity[disableServiceMessages]"
    assertEquals(disableExpected, DisableServiceMessages().toString())
  }
}
