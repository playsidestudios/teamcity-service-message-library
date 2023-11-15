package com.playside.servicemessage

import java.nio.file.Path

class Inspection(private var id: String, name: String, category: String, description: String) {
  init {
    "inspectionType"
        .printServiceMessage(
            "id" to id, "name" to name, "description" to description, "category" to category)
  }

  enum class Severity(var text: String) {
    INFO("INFO"),
    ERROR("ERROR"),
    WARNING("WARNING"),
    WEAK_WARNING("WEAK WARNING")
  }

  fun printInspection(
      severity: Severity,
      file: Path,
      message: String? = null,
      line: String? = null
  ) {
    "inspection"
        .printServiceMessage(
            "typeId" to id,
            "file" to file,
            "message" to message,
            "line" to line,
            "SEVERITY" to severity.text)
  }
}