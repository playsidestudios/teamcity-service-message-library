package io.github.playsidestudios.teamcityservicemessages

import java.nio.file.Path

fun testSuite(testSuite: String, init: TESTS.() -> Unit): TESTS {
  val tests = TESTS(testSuite)
  tests.open()
  tests.init()
  tests.close()
  return tests
}

/**
 * Blocks are used to group several messages in the build log.
 *
 * The blockOpened system message has the name attribute, and you can also add its description
 */
fun block(name: String, description: String? = null, init: BLOCK.() -> Unit): BLOCK {
  val block = BLOCK(name, description)
  block.open()
  block.init()
  block.close()
  return block
}

/**
 * ```
 * ##teamcity[compilationStarted compiler='<compiler_name>']
 * ...
 * ##teamcity[message text='compiler output']
 * ##teamcity[message text='compiler output']
 * ##teamcity[message text='compiler error' status='ERROR']
 * ...
 * ##teamcity[compilationFinished compiler='<compiler name>']
 * ```
 * - 'compiler_name' is an arbitrary name of the compiler performing compilation, for example, javac
 *   or groovyc. Currently, it is used as a block name in the build log.
 * - Any message with status ERROR reported between compilationStarted and compilationFinished will
 *   be treated as a compilation error.
 */
fun compiler(compiler: String, init: COMPILER.() -> Unit): COMPILER {
  val compilerBlock = COMPILER(compiler)
  compilerBlock.open()
  compilerBlock.init()
  compilerBlock.close()
  return compilerBlock
}

/**
 * If you need for some reason to disable searching for service messages in the output, you can
 * disable the service messages search with the messages:
 * ```
 * ##teamcity[enableServiceMessages]
 * ##teamcity[disableServiceMessages]
 * ```
 *
 * Any messages that appear between these two are not parsed as service messages and are effectively
 * ignored. For server-side processing of service messages, enable/disable service messages also
 * supports the flowId attribute and will ignore only the messages with the same flowId.
 */
fun enableServiceMessages(flowId: String? = null) {
  EnableServiceMessages(flowId).print()
}

fun disableServiceMessages(flowId: String? = null) {
  DisableServiceMessages(flowId).print()
}

fun inspectionType(id: String, name: String, category: String, description: String) {
  InspectionType(id, name, category, description).print()
}

fun publishArtifact(path: String) {
  PublishArtifactMessage(path).print()
}

fun inspection(
    id: String,
    file: Path,
    line: Int?,
    message: String?,
    severity: InspectionSeverity?
) {
  InspectionMessage(id, file, line, message, severity).print()
}

fun <T : Number> buildStatisticValue(key: String, value: T) {
  BuildStatistic(key = key, value = value.toString()).print()
}

fun sendSlackMessage(message: String, sendTo: String, connectionID: String) {
  NotifySlack(message, sendTo, connectionID).print()
}

fun buildProblem(description: String, identity: String? = null) {
  BuildProblem(description, identity).print()
}

fun addBuildTag(tag: String) {
  AddBuildTag(tag).print()
}

fun removeBuildTag(tag: String) {
  RemoveBuildTag(tag).print()
}
