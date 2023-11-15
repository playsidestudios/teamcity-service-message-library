package com.playside.servicemessage

class Test(val name: String) {
  var failures: Int = 0
    private set

  fun failed(message: String) {
    "testFailed".printServiceMessage("name" to name, "message" to message)
    failures++
  }

  fun failedComparison(
      expected: Any,
      actual: Any,
      message: String = "Unexpected value",
      details: String = ""
  ) {
    "testFailed"
        .printServiceMessage(
            "name" to name,
            "message" to message,
            "details" to details,
            "expected" to expected,
            "actual" to actual)
    failures++
  }

  fun ignored(message: String) {
    "testIgnored".printServiceMessage("name" to name, "message" to message)
  }
}