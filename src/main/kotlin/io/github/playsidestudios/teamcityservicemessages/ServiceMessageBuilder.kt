package io.github.playsidestudios.teamcityservicemessages

import io.github.playsidestudios.teamcityservicemessages.Message.*
import io.github.playsidestudios.teamcityservicemessages.Message.InspectionType
import io.github.playsidestudios.teamcityservicemessages.message.MultiAttributeMessage
import io.github.playsidestudios.teamcityservicemessages.message.NoAttributeMessage
import io.github.playsidestudios.teamcityservicemessages.message.SingleAttributeMessage
import java.net.URL
import java.nio.file.Path
import kotlin.time.DurationUnit
import kotlin.time.TimeSource

internal class EnableServiceMessages(flowId: String? = null) :
    NoAttributeMessage(EnabledServiceMessages, flowId)

internal class DisableServiceMessages(flowId: String? = null) :
    NoAttributeMessage(DisabledServiceMessages, flowId)

/**
 * You can publish the build artifacts while the build is still running, immediately after the
 * artifacts are built.
 *
 * To do this, you need to output the following line:
 * ```
 * ##teamcity[publishArtifacts '<path>']
 * ```
 *
 * The <path> has to adhere to the same rules as the Build Artifact specification of the Build
 * Configuration Settings. The files matching the <path> will be uploaded and visible as the
 * artifacts of the running build.
 *
 * The message should be printed after all the files are ready and no file is locked for reading.
 * ---
 * Tip
 * > To publish multiple artifact files in one archive, you need to configure the Artifact paths in
 * > General Settings of a build configuration. If you use service messages, only artifacts for the
 * > last rule will be published to the archive. --- Artifacts are uploaded in the background, which
 * > can take time. Make sure the matching files are not deleted till the end of the build (for
 * > example, you can put them in a directory that is cleaned on the next build start, in a temp
 * > directory, or use Swabra to clean them after the build). --- note
 *
 * > The process of publishing artifacts can affect the build, because it consumes network traffic,
 * > and some disk/CPU resources (should be pretty negligible for not large files/directories). ---
 * > Artifacts that are specified in the build configuration setting will be published as usual.
 */
internal class PublishArtifactMessage(path: String) :
    SingleAttributeMessage(PublishArtifacts, path)

internal class NotifySlack(
    /**
     * the message to show. Supports Markdown syntax (apart from "\n" for line breaks, use "|n" or
     * "|r" instead).
     */
    message: String,
    /**
     * specifies who should receive the message. Accepts a single Slack channel name, channel ID
     * (starts with "C", for instance, "C052UHDRZU7"), or user ID (starts with "U", for instance,
     * "U02K2UVKJP7") as value. If you need to send the same message to multiple recipients, create
     * multiple service messages with different sendTo values.
     */
    sendTo: String,
    /**
     * the optional parameter that allows you to choose a specific Slack connection that TeamCity
     * should use to send this message. Accepts connection IDs as values. If this parameter is not
     * specified, TeamCity will retrieve all Slack connections available for the current project and
     * choose the one whose Notifications limit is not zero.
     */
    connectionID: String
) :
    MultiAttributeMessage(
        Notification,
        listOf(
            "notifier" to NotifierTypes.slack.name,
            "message" to message,
            "connectionID" to connectionID,
            "sendTo" to sendTo))

internal class BuildStatistic(key: String, value: String) :
    MultiAttributeMessage(BuildStatisticValue, listOf("key" to key, "value" to value))

private class BlockOpened(name: String, description: String? = null) :
    MultiAttributeMessage(BlockOpened, listOf("name" to name, "description" to description))

private class BlockClosed(name: String) :
    MultiAttributeMessage(BlockClosed, listOf("name" to name))

private class CompilerOpen(compiler: String) :
    MultiAttributeMessage(CompilationStarted, listOf("compiler" to compiler))

private class CompilerClosed(compiler: String) :
    MultiAttributeMessage(CompilationFinished, listOf("compiler" to compiler))

internal class InspectionType(id: String, name: String, category: String, description: String) :
    MultiAttributeMessage(
        InspectionType,
        listOf("id" to id, "name" to name, "category" to category, "description" to description))

internal class InspectionMessage(
    id: String,
    file: Path,
    line: Int? = null,
    message: String? = null,
    severity: InspectionSeverity? = null,
) :
    MultiAttributeMessage(
        Inspection,
        listOf(
            "typeId" to id,
            "message" to message,
            "file" to file.toString(),
            "line" to line?.toString(),
            "SEVERITY" to severity?.text))

private class TestSuiteStarted(testSuite: String) :
    MultiAttributeMessage(TestSuiteStarted, listOf("name" to testSuite))

private class TestSuiteFinished(testSuite: String) :
    MultiAttributeMessage(TestSuiteFinished, listOf("name" to testSuite))

private class TestStarted(test: String, captureStandardOutput: Boolean) :
    MultiAttributeMessage(
        TestStarted,
        listOf("name" to test, "captureStandardOutput" to captureStandardOutput.toString()),
    )

private class TestFinished(test: String, duration: Int) :
    MultiAttributeMessage(
        TestFinished,
        listOf("name" to test, "duration" to duration.toString()),
    )

private class TestIgnored(test: String, message: String) :
    MultiAttributeMessage(TestIgnored, listOf("name" to test, "message" to message))

private class TestStdOut(test: String, out: String) :
    MultiAttributeMessage(TestStdOut, listOf("name" to test, "out" to out))

private class TestStdErr(test: String, out: String) :
    MultiAttributeMessage(TestStdErr, listOf("name" to test, "out" to out))

private class TestFailed(test: String, message: String, details: String) :
    MultiAttributeMessage(
        TestFailed,
        listOf(
            "name" to test,
            "message" to message,
            "details" to details,
        ))

private class TestMetadata(test: String, value: String, type: MetadataType, name: String? = null) :
    MultiAttributeMessage(
        TestMetadata,
        listOf("name" to name, "test-name" to test, "value" to value, "type" to type.text))

private class TestFailedComparison(
    test: String,
    message: String,
    details: String,
    actual: String,
    expected: String
) :
    MultiAttributeMessage(
        TestFailed,
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
  val duration: Int
    get() = (endTime - startTime).toInt(DurationUnit.MILLISECONDS)

  override fun open() {
    startTime = timeSource.markNow()
    TestStarted(test, captureStandardOutput).print()
  }

  override fun close() {
    endTime = timeSource.markNow()
    TestFinished(test, duration).print()
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
  val children = arrayListOf<TEST>()
  val totalFailures: Int
    get() =
        children.fold(0) { sum, test ->
          if (!test.ignored) {
            sum + test.failures
          } else {
            sum
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
