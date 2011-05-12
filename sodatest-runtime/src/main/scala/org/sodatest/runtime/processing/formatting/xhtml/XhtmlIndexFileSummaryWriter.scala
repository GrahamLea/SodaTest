package org.sodatest.runtime.processing.formatting.xhtml

/*
 * Copyright (c) 2011 Belmont Technology Pty Ltd. All rights reserved.
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

import java.io.{PrintWriter, FileWriter, File}
import java.text.SimpleDateFormat
import java.util.Date
import xml.Text
import org.sodatest.runtime.processing.running.{SodaTestResultSummary, PathUtils, SodaTestResultSummaryWriter}

object XhtmlIndexFileSummaryWriter extends SodaTestResultSummaryWriter {

  import PathUtils._

  val NEWLINE = Text("\n")

  private val preamble =
"""<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
"""

  val stylesheet = DefaultStylesheet.load()

  def writeSummaries(results: Seq[SodaTestResultSummary], inputRoot: File, outputRoot: File): Unit = {
    val out = new PrintWriter(new FileWriter(new File(outputRoot, "index.html")))

    val totalFailedTests = results.filter(_.failed).size
    val failed = totalFailedTests != 0

    val resultsByDirectorySorted = results.foldLeft(Map[File, List[SodaTestResultSummary]]())((map, nextResult) => {
      val parentFile: File = (new File(nextResult.testPath)).getParentFile
      map.get(parentFile) match {
        case Some(list) => map + (parentFile -> (nextResult :: list))
        case None => map + (parentFile -> List(nextResult))
      }
    }).toList
      .map(kv => (kv._1, kv._2.sortBy(_.testName)))
      .sortBy(_._1.toString)

    try {
      out.println(preamble)
      out.println(
        <head>
          <title>SodaTest: {if (failed) "FAILED" else "Passed"} ({formatPath(inputRoot.toString + File.separator)})</title>
          <style type="text/css">
            {stylesheet}
          </style>
        </head>
      )
      out.println(
        <body class={"summary " + (if (failed) "failed" else "passed")}>
          <div class="header">
            <div class="title">
              <h1>SodaTest Results Summary</h1>
              <h2>({formatPath(inputRoot.toString + File.separator)})</h2>
              <h2 class="timestamp">{new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a").format(new Date())}</h2>
            </div>
            <div class="testResult">
              <p>{if (failed) "FAILED" else "Passed"}</p>
            </div>
          </div>
          <div class="results">
            { for ((directory, results) <- resultsByDirectorySorted) yield {
              <h2>{formatPath(relativeToInputPath(directory, inputRoot) + File.separator)}</h2> ++ NEWLINE ++
              <div class={"directoryResult" + (if (results.map(_.failed).contains(true)) " failure" else "")}>
                <table>
                    <thead>
                    <tr>
                        <th>Result</th>
                        <th>Test</th>
                        <th>Mismatches</th>
                        <th>Errors</th>
                    </tr>
                    </thead>
                    <tbody>
                    {for (result <- results) yield {
                      <tr class={"testResult " + (if (result.failed) "failed" else "passed")}>
                          <td class="testResult">{if (result.failed) "Failed" else "Passed"}</td>
                          <td class="testName"><a href={relativeToInputPath(new File(result.testPath), inputRoot).replace('\\', '/') + ".html"}>{result.testName}</a></td>
                          <td class="mismatches count">{result.mismatchCount}</td>
                          <td class="errors count">{result.errorCount}</td>
                       </tr> ++ NEWLINE
                    }}
                    </tbody>
                </table>
              </div> ++ NEWLINE
          }}
          </div>
        </body>
      )
      
      out.println("</html>")
    } finally {
      out.close()
    }

  }

  private def formatPath(path: String) = {
    if (java.lang.Boolean.getBoolean("sodatest.outputUnixPathsOnWindows"))
      path.replace('\\', '/')
    else
      path
  }

}