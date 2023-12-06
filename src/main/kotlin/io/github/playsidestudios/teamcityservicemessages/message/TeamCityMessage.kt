package io.github.playsidestudios.teamcityservicemessages.message

import io.github.playsidestudios.teamcityservicemessages.Message
import io.github.playsidestudios.teamcityservicemessages.ServiceMessage

/**
 * [Escaped Values](https://www.jetbrains.com/help/teamcity/service-messages.html#Escaped+Values)
 */
fun String.escapeForTeamcity(): String {
  return this.replace("|", "||")
      .replace("'", "|'")
      .replace("\n", "|n")
      .replace("\r", "|r")
      .replace("[", "|[")
      .replace("]", "|]")
      .replace(Regex("\\\\u([\\da-zA-Z]{4})")) { "|0x${it.groups[1]}" }
}

/**
 * [Service Messages Formats](https://www.jetbrains.com/help/teamcity/service-messages.html#Service+Messages+Formats)
 */
sealed class TeamCityMessage() : ServiceMessage {
    private val open = "##teamcity["
    private val close = "]"
  override fun toString(): String =
      when (this) {
        is SingleAttributeMessage -> "$open${this.name.text} '${this.value.escapeForTeamcity()}'$close"
        is NoAttributeMessage -> "$open${this.name.text}$close"
        is MultiAttributeMessage -> {
          val keyValues =
              this.arguments
                  .filter { it.second != null }
                  .joinToString(separator = " ") {
                    "${it.first}='${it.second?.escapeForTeamcity()}'"
                  }
                  .let {
                    if (it.isNotBlank()) {
                      it.padStart(it.length + 1)
                    } else {
                      it
                    }
                  }

          "$open${this.name.text}$keyValues$close"
        }
      }

  override fun print() {
    println(toString())
  }
}

open class MultiAttributeMessage(
    val name: Message,
    val arguments: List<Pair<String, String?>> = listOf(),
) : TeamCityMessage()

open class SingleAttributeMessage(val name: Message, val value: String) : TeamCityMessage()

open class NoAttributeMessage(val name: Message) : TeamCityMessage()
