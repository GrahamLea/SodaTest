/*
 * Copyright (c) 2010-2011 Belmont Technology Pty Ltd. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.sodatest

package runtime.processing.formatting.xhtml

import java.io.{StringWriter, PrintWriter}
import runtime.processing.ResultFormatter
import org.sodatest.api.SodaTestLog
import runtime.data.results._

class XhtmlFormatter(val stylesheet: String)(implicit val log: SodaTestLog) extends ResultFormatter {

  def this()(implicit log: SodaTestLog) = this(DefaultStylesheet.load())(log)

  private val preamble =
"""<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
"""

  private def header(testName: String, testPath: String) =
      <head>
        <title>{testName} ({testPath})</title>
        <style type="text/css">
          {stylesheet}
        </style>
      </head>

  private def bodyTitle(passed: Boolean, testName: String, testPath: String) =
      <div class="header">
        <div class="title">
          <h1>{testName}</h1>
          <h2>({testPath})</h2>
        </div>
        <div class="testResult">
          <p>{if (passed) "PASSED" else "FAILED"}</p>
        </div>
      </div>

  def format(result: SodaTestResult): String = try {
    log.info("Formatting...")
    val sb = new StringBuilder()
              .append(preamble)
              .append(header(result.test.testName, formatPath(result.test.testPath)))
              .append("<body class=\"").append(if (result.passed) "passed" else "failed").append("\">")
              .append(bodyTitle(result.passed, result.test.testName, formatPath(result.test.testPath)))
              .append('\n')

    result.results.map(_ match {
      case fbr: FixtureBlockResult => XhtmlFixtureFormatter.format(fbr)
      case ebr: EventBlockResult => XhtmlEventFormatter.format(ebr)
      case rbr: ReportBlockResult => XhtmlReportFormatter.format(rbr)
      case nbr: NoteBlockResult => XhtmlNoteFormatter.format(nbr)
      case anything => <p>Don't know how to format {anything.getClass.getName}!</p>
    }).addString(sb, "\n")
      .append('\n')
      .append("</body>")
      .append('\n')
      .append("</html>")
      .toString
    
  } catch {
    case e => e.printStackTrace; throw e
  }

  private def formatPath(path: String) = {
    if (java.lang.Boolean.getBoolean("sodatest.outputUnixPathsOnWindows"))
      path.replace('\\', '/')
    else
      path
  }

  private def exception2String(x: Throwable) = {
    val stringy = new StringWriter
    val out = new PrintWriter(stringy)
    x.printStackTrace(out)
    out.flush
    stringy.toString
  }

  private implicit def string2Option(s: String) : Option[String] = Some(s)
  private implicit def elem2Option(x: scala.xml.Elem) : Option[scala.xml.Elem] = Some(x)

  private object EmptyMap {
    def unapply(m: Map[_, _]) = m.isEmpty
  }


}