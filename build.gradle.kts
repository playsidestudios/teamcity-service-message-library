plugins {
  id("org.jetbrains.kotlin.jvm") version "1.9.20"
  `java-library`
  `maven-publish`
  signing
  id("io.github.gradle-nexus.publish-plugin") version "1.3.0"
}

repositories { mavenCentral() }

dependencies {
  testImplementation("org.jetbrains.kotlinx:kotlinx-html-jvm:0.9.1")
  // This dependency lets us capture system.out and assert on its content
  testImplementation("com.github.stefanbirkner:system-lambda:1.2.1")
  testImplementation(kotlin("test"))
}


group = "com.playside.teamcity.servicemessages"

kotlin { sourceSets.all { languageSettings { languageVersion = "2.0" } } }

// Apply a specific Java toolchain to ease working on different environments.
java { toolchain { languageVersion.set(JavaLanguageVersion.of(11)) } }

tasks.test { useJUnitPlatform() }

nexusPublishing {
  repositories {
    sonatype { // only for users registered in Sonatype after 24 Feb 2021
      nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
      snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
    }
  }
}

publishing {
  publications {
    create<MavenPublication>("mavenJava") {
      from(components["java"])

      pom {
        name.set("TeamCity Service Messages")
        description.set("Helper functions for printing TeamCity service messages to stdout")
        url.set("https://github.com/playsidestudios/teamcity-service-message-library")
        licenses {
          license {
            name.set("The MIT License (MIT)")
            url.set("https://mit-license.org/")
          }
        }
        developers {
          developer {
            id.set("dhamilton")
            name.set("Dyson Hamilton")
            email.set("dyson.hamilton@playsidestudios.com")
          }
        }
        scm {
          connection.set("https://github.com/playsidestudios/teamcity-service-message-library.git")
          developerConnection.set(
              "https://github.com/playsidestudios/teamcity-service-message-library.git")
          url.set("https://github.com/playsidestudios/teamcity-service-message-library")
        }
      }
    }
  }
}

signing {
  //val signingKey: String? by project
  //val signingPassword: String? by project
  //val keyId: String? by project
  //useInMemoryPgpKeys(keyId, signingKey, signingPassword)
  sign(publishing.publications["mavenJava"])
}
