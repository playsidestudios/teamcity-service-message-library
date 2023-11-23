package com.playside.servicemessage

/**
 * You can report messages to a build log as follows:
 * ```##teamcity[message text='<message text>' errorDetails='<error details>' status='<status value>']```
 *
 * where:
 *
 * - 'status' can take the following values: NORMAL (default), WARNING, FAILURE, ERROR.
 * - 'errorDetails' is used only if status is ERROR, in other cases it is ignored. This message fails the build in case its status is ERROR and the "Fail build if an error message is logged by build runner" box is checked on the Build Failure Conditions page of the build configuration.
 *
 * ```##teamcity[message text='Exception text' errorDetails='stack trace' status='ERROR']```
 */
class GenericMessage(
    text: String,
    status: Status = Status.NORMAL,
    errorDetails: String? = null,
    flowId: String? = null,
) :
    TeamCityMessage(
        Message.GenericMessage,
        listOf(
            "text" to text,
            "status" to status.name,
            "errorDetails" to errorDetails,
            "flowId" to flowId,
        ),
    )