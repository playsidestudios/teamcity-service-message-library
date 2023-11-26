package io.github.playsidestudios.teamcityservicemessages

interface ServiceMessage {
  override fun toString(): String

  fun print()
}