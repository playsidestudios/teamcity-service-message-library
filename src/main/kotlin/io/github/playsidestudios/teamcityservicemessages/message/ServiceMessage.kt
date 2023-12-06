package io.github.playsidestudios.teamcityservicemessages.message

interface ServiceMessage {
  override fun toString(): String

  fun print()
}