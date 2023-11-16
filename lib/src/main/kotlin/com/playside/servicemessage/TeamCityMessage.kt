package com.playside.servicemessage

abstract class TeamCityMessage(
    private val name: Message,
    private val arguments: List<Pair<String, String?>> = listOf(),
) : ServiceMessage {

  private fun String.escapeValues(): String {
    return this.replace("|", "||")
        .replace("'", "|'")
        .replace("\n", "|n")
        .replace("\r", "|r")
        .replace("[", "|[")
        .replace("]", "|]")
        .replace(Regex("\\\\u([\\da-zA-Z]{4})")) { "|0x${it.groups[1]}" }
  }

  override fun toString(): String {
    val content =
        this.arguments
            .filter { it.second != null }
            .joinToString(separator = " ") { "${it.first}='${it.second?.escapeValues()}'" }
    return "##teamcity[${this.name.text} $content]"
  }

  override fun print() {
    println(toString())
  }
}