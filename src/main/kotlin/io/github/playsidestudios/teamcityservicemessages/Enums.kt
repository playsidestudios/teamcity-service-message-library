package io.github.playsidestudios.teamcityservicemessages

enum class Message(val text: String) {
  EnabledServiceMessages("enableServiceMessages"),
  DisabledServiceMessages("disableServiceMessages"),
  BlockOpened("blockOpened"),
  BlockClosed("blockClosed"),
  CompilationStarted("compilationStarted"),
  CompilationFinished("compilationFinished"),
  GenericMessage("message"),
  TestSuiteStarted("testSuiteStarted"),
  TestSuiteFinished("testSuiteFinished"),
  TestStarted("testStarted"),
  TestFinished("testFinished"),
  TestFailed("testFailed"),
  TestIgnored("testIgnored"),
  TestStdErr("testStdErr"),
  TestStdOut("testStdOut"),
  TestMetadata("testMetadata"),
  InspectionType("inspectionType"),
  Inspection("inspection"),
  PublishArtifacts("publishArtifacts"),
  BuildStatisticValue("buildStatisticValue"),
  Notification("notification"),
  BuildProblem("buildProblem"),
  AddBuildTag("addBuildTag"),
  RemoveBuildTag("removeBuildTag"),
}

enum class NotifierTypes {
  slack,
  email,
}

enum class Status {
  NORMAL,
  WARNING,
  FAILURE,
  ERROR,
}

enum class InspectionSeverity(val text: String) {
  INFO("INFO"),
  ERROR("ERROR"),
  WARNING("WARNING"),
  WEAK_WARNING("WEAK WARNING"),
}

enum class MetadataType(val text: String?) {
  TEXT(null),
  NUMBER("number"),
  LINK("link"),
  ARTIFACT("artifact"),
  IMAGE("image"),
  VIDEO("video"),
}