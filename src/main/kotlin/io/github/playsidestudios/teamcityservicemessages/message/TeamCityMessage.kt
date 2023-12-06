package io.github.playsidestudios.teamcityservicemessages.message

import io.github.playsidestudios.teamcityservicemessages.Message

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
 * [Service-Messages-Formats](https://www.jetbrains.com/help/teamcity/service-messages.html#Service+Messages+Formats)
 */
sealed class TeamCityMessage(
    private val name: Message,
    private val flowId: String? = null,
) : ServiceMessage {
  private val open = "##teamcity["
  private val close = "]"

  private fun keyValue(key: String, value: String?): String {
    if (value.isNullOrBlank()) {
      return ""
    }
    return "${key}='${value.escapeForTeamcity()}'"
  }

  override fun toString(): String {
    val flowIdKey =
        keyValue("flowId", flowId).let { if (it.isNotBlank()) it.padStart(it.length + 1) else it }

    when (this) {
      is SingleAttributeMessage ->
          return "$open${this.name.text} '${this.value.escapeForTeamcity()}'$flowIdKey$close"
      is NoAttributeMessage -> return "$open${this.name.text}$flowIdKey$close"
      is MultiAttributeMessage -> {
        val temp = this.arguments.toMutableList()
        temp.add(Pair("flowId", this.flowId))
        val keyValues =
            temp
                .filterNot { it.second.isNullOrBlank() }
                .joinToString(separator = " ") { keyValue(it.first, it.second) }
                .let {
                  if (it.isNotBlank()) {
                    it.padStart(it.length + 1)
                  } else {
                    it
                  }
                }

        return "$open${this.name.text}$keyValues$close"
      }
    }
  }

  override fun print() {
    println(toString())
  }
}

open class MultiAttributeMessage(
    name: Message,
    val arguments: List<Pair<String, String?>>,
    flowId: String? = null,
) : TeamCityMessage(name, flowId)

internal open class SingleAttributeMessage(
    name: Message,
    val value: String,
    flowId: String? = null,
) : TeamCityMessage(name, flowId)

internal open class NoAttributeMessage(
    name: Message,
    flowId: String? = null,
) : TeamCityMessage(name, flowId)
