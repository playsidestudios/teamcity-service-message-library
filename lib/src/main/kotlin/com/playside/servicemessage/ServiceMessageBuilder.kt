package com.playside.servicemessage

import java.net.URL
import java.nio.file.Path
import kotlin.time.DurationUnit
import kotlin.time.TimeSource

class EnableServiceMessages : TeamCityMessage(Message.EnabledServiceMessages)

class DisableServiceMessages : TeamCityMessage(Message.DisabledServiceMessages)

private class BlockOpened(name: String, description: String? = null) :
    TeamCityMessage(Message.BlockOpened, listOf("name" to name, "description" to description))

private class BlockClosed(name: String) :
    TeamCityMessage(Message.BlockClosed, listOf("name" to name))

private class CompilerOpen(compiler: String) :
    TeamCityMessage(Message.CompilationStarted, listOf("compiler" to compiler))

private class CompilerClosed(compiler: String) :
    TeamCityMessage(Message.CompilationFinished, listOf("compiler" to compiler))

private class InspectionType(id: String, name: String, category: String, description: String) :
    TeamCityMessage(
        Message.InspectionType,
        listOf("id" to id, "name" to name, "category" to category, "description" to description))

private class InspectionMessage(
    id: String,
    file: Path,
    line: Int? = null,
    message: String? = null,
    severity: InspectionSeverity? = null
) :
    TeamCityMessage(
        Message.Inspection,
        listOf(
            "typeId" to id,
            "message" to message,
            "file" to file.toString(),
            "line" to line?.toString(),
            "SEVERITY" to severity?.text))

private class TestSuiteStarted(testSuite: String) :
    TeamCityMessage(Message.TestSuiteStarted, listOf("name" to testSuite))

private class TestSuiteFinished(testSuite: String) :
    TeamCityMessage(Message.TestSuiteFinished, listOf("name" to testSuite))

private class TestStarted(test: String, captureStandardOutput: Boolean) :
    TeamCityMessage(
        Message.TestStarted,
        listOf("name" to test, "captureStandardOutput" to captureStandardOutput.toString()),
    )

private class TestFinished(test: String, duration: Int) :
    TeamCityMessage(
        Message.TestFinished,
        listOf("name" to test, "duration" to duration.toString()),
    )

private class TestIgnored(test: String, message: String) :
    TeamCityMessage(Message.TestIgnored, listOf("name" to test, "message" to message))

private class TestStdOut(test: String, out: String) :
    TeamCityMessage(Message.TestStdOut, listOf("name" to test, "out" to out))

private class TestStdErr(test: String, out: String) :
    TeamCityMessage(Message.TestStdErr, listOf("name" to test, "out" to out))

private class TestFailed(test: String, message: String, details: String) :
    TeamCityMessage(
        Message.TestFailed,
        listOf(
            "name" to test,
            "message" to message,
            "details" to details,
        ))

private class TestMetadata(test: String, value: String, type: MetadataType, name: String? = null) :
    TeamCityMessage(
        Message.TestMetadata,
        listOf("name" to name, "testname" to test, "value" to value, "type" to type.text))

private class TestFailedComparison(
    test: String,
    message: String,
    details: String,
    actual: String,
    expected: String
) :
    TeamCityMessage(
        Message.TestFailed,
        listOf(
            "name" to test,
            "message" to message,
            "details" to details,
            "type" to "comparisonFailure",
            "actual" to actual,
            "expected" to expected))

class BLOCK(private var name: String, private var description: String?) : ServiceMessageBlock {
  override fun open() {
    BlockOpened(this.name, this.description).print()
  }

  override fun close() {
    BlockClosed(this.name).print()
  }
}

class COMPILER(private var compiler: String) : ServiceMessageBlock {
  override fun open() {
    CompilerOpen(this.compiler).print()
  }

  override fun close() {
    CompilerClosed(this.compiler).print()
  }
}

class TEST(private var test: String, private var captureStandardOutput: Boolean) :
    ServiceMessageBlock {
  private val timeSource = TimeSource.Monotonic
  private var startTime: TimeSource.Monotonic.ValueTimeMark = timeSource.markNow()
  private var endTime: TimeSource.Monotonic.ValueTimeMark = timeSource.markNow()
  var failures: Int = 0
  var ignored: Boolean = false

  override fun open() {
    startTime = timeSource.markNow()
    TestStarted(test, captureStandardOutput).print()
  }

  override fun close() {
    endTime = timeSource.markNow()
    TestFinished(test, (endTime - startTime).toInt(DurationUnit.MILLISECONDS)).print()
  }

  fun failed(message: String, details: String) {
    failures += 1
    TestFailed(test, message, details).print()
  }

  fun failedComparison(message: String, details: String, actual: String, expected: String) {
    failures += 1
    TestFailedComparison(test, message, details, actual, expected).print()
  }

  fun metadataNumber(name: String, value: Number) {
    TestMetadata(test, value.toString(), type = MetadataType.NUMBER, name).print()
  }

  fun metadataText(name: String, value: String) {
    TestMetadata(test, value, MetadataType.TEXT, name).print()
  }

  fun metadataLink(name: String, value: URL) {
    TestMetadata(test, value.toString(), MetadataType.LINK, name).print()
  }

  /**
   * ```
   * ##teamcity[testMetadata testName='test.name' type='artifact' value='path/to/catalina.out']
   * ```
   *
   * The path to the artifact should be relative to the build artifacts directory, and can reference
   * a file inside an archive:
   * ```
   * ##teamcity[testMetadata testName='test.name' type='artifact' value='logs.zip!/testTyping/full-log.txt']
   * ```
   *
   * When showing links to artifacts, TeamCity shows both the name attribute and the filename of the
   * referenced artifact. If the name was autogenerated, it is not shown.
   */
  fun metadataArtifact(name: String, value: String) {
    TestMetadata(test, value, MetadataType.ARTIFACT, name).print()
  }

  fun metadataImage(name: String, value: Path) {
    TestMetadata(test, value.toString(), MetadataType.IMAGE, name)
  }

  fun metadataVideo(name: String, value: Path) {
    TestMetadata(test, value.toString(), MetadataType.VIDEO, name)
  }

  fun ignore(message: String) {
    ignored = true
    TestIgnored(test, message).print()
  }

  fun stdOut(out: String) {
    TestStdOut(test, out).print()
  }

  fun stdErr(out: String) {
    TestStdErr(test, out).print()
  }
}

class TESTS(private val testSuite: String) : ServiceMessageBlock {
  private val children = arrayListOf<TEST>()
  val totalFailures: Int
    get() =
        children.fold(0) { sum, test ->
          if (!test.ignored) {
            sum + test.failures
          } else {
            0
          }
        }

  override fun open() {
    TestSuiteStarted(testSuite).print()
  }

  override fun close() {
    TestSuiteFinished(testSuite).print()
  }

  fun test(test: String, captureStandardOutput: Boolean = false, init: TEST.() -> Unit): TEST {
    val t = TEST(test, captureStandardOutput)
    children.add(t)
    t.open()
    t.init()
    t.close()
    return t
  }
}

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

fun enableServiceMessages() {
  EnableServiceMessages().print()
}

fun disableServiceMessages() {
  DisableServiceMessages().print()
}

fun inspectionType(id: String, name: String, category: String, description: String) {
  InspectionType(id, name, category, description).print()
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
