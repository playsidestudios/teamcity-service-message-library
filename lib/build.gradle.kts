/*
 * This file was generated by the Gradle 'init' task.
 *
 * This generated file contains a sample Kotlin library project to get you started.
 * For more details on building Java & JVM projects, please refer to https://docs.gradle.org/8.4/userguide/building_java_projects.html in the Gradle documentation.
 * This project uses @Incubating APIs which are subject to change.
 */

plugins {
  // Apply the org.jetbrains.kotlin.jvm Plugin to add support for Kotlin.
  id("org.jetbrains.kotlin.jvm") version "1.9.20"

  // Apply the java-library plugin for API and implementation separation.
  `java-library`
}

repositories {
  // Use Maven Central for resolving dependencies.
  mavenCentral()
}

dependencies {
  // This dependency is exported to consumers, that is to say found on their compile classpath.
  api("org.apache.commons:commons-math3:3.6.1")

  // This dependency is used internally, and not exposed to consumers on their own compile
  // classpath.
  implementation("com.google.guava:guava:32.1.1-jre")

  // This dependency lets us capture system.out and assert on its content
  testImplementation("com.github.stefanbirkner:system-lambda:1.2.1")
  testImplementation(kotlin("test"))
}

kotlin { sourceSets.all { languageSettings { languageVersion = "2.0" } } }

// Apply a specific Java toolchain to ease working on different environments.
java { toolchain { languageVersion.set(JavaLanguageVersion.of(20)) } }

tasks.test { useJUnitPlatform() }
