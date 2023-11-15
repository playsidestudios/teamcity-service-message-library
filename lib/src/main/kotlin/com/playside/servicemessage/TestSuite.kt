package com.playside.servicemessage

class TestSuite(val name: String) {
  var failures: Int = 0
    private set

  fun test(name: String, f: Test.() -> Unit) {
    "testSuiteStarted".printServiceMessage("name" to name)
    val it = Test(name)
    it.f()
    failures += it.failures
    "testFinished".printServiceMessage("name" to name)
  }
}
