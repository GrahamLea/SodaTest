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

package org.sodatest.runtime
package processing
package running

import execution.SodaTestExecutor
import processing.SodaTestContext
import data.SodaTest
import parsing.blocks.{BlockSourceSplitter, BlockFactory}
import parsing.csv.CsvCellSplitter
import data.results.{NoteBlockResult, SodaTestResult}
import data.blocks.{Line, BlockSource, NoteBlock}
import formatting.console.ConsoleResultSummaryWriter
import formatting.xhtml.{XhtmlIndexFileSummaryWriter, XhtmlSodaTestResultWriter}
import java.io._

object SodaFileRunner {

  def runTest(inputFile: File)(implicit context: SodaTestContext): SodaTestResult = {
    context.log.info("Running " + inputFile)
    val test = new SodaTest(SodaFileUtils.getTestName(inputFile), inputFile.toString, BlockFactory.create(
      BlockSourceSplitter.parseBlocks(
        CsvCellSplitter.split(
          new BufferedInputStream(new FileInputStream(inputFile))
        )
      ))
    )
    try {
      SodaTestExecutor.execute(test, context)
    }
    catch {
      case t: Throwable => context.log.error("Error executing " + inputFile.getName + " :" + t)
      new SodaTestResult(test, List(new NoteBlockResult(new NoteBlock(BlockSource(List(
        Line(0, List("", "An uncaught error occurred while running this test")),
        Line(0, List("", t.toString))
      ))))), false)
    }
  }

  def runTestAndWriteResult(inputFile: File, outputFile: File, successCallback: (Boolean) => Unit)(implicit context: SodaTestContext): Unit = {
    if (!inputFile.exists)
      throw new FileNotFoundException(inputFile.getAbsolutePath)
    if (!outputFile.getAbsoluteFile.getParentFile.exists)
      throw new InvalidDirectoryException("Output directory '" + outputFile.getAbsoluteFile.getParent + "' does not exist.")

    val testResult = runTest(inputFile)

    XhtmlSodaTestResultWriter.writeResultFile(testResult, outputFile)

    val summary = SodaTestResultSummary.summarise(testResult)
    ConsoleResultSummaryWriter.writeSummaries(List(summary), inputFile, outputFile)

    val succeeded = summary.mismatchCount == 0 && summary.errorCount == 0
    successCallback(succeeded)
  }

  def main(args: Array[String]): Unit = {
    main(args, succeeded => { exit(if (succeeded) 0 else 1) } );
  }

  def main(args: Array[String], successCallback: (Boolean) => Unit): Unit = {
    if (args.length != 3) {
      usage()
      successCallback(false)
    } else {
      val fixtureRoot = args(0)
      val inputFile = new File(args(1))
      val outputFile = new File(args(2))
      implicit val context = new SodaTestContext(fixtureRoot)

      try {
        runTestAndWriteResult(inputFile, outputFile, successCallback)
      }
      catch {
        case e: InvalidDirectoryException => usage(Some("Error: " + e.getMessage)); successCallback(false)
        case e: IOException => usage(Some("Error: " + e.getMessage)); successCallback(false)
      }
    }
  }

  private def usage(): Unit = usage(None)

  private def usage(message: Option[String]): Unit = {
    message map {System.err.println(_)}
    System.err.println("usage: SodaDirectoryRunner <fixture_root_package> <input_file> <output_file>")
  }
}