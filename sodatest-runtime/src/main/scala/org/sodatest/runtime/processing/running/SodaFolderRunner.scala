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

import org.sodatest.api.SodaTestLog
import data.results.{EventBlockResult, ReportBlockResult, SodaTestResult}
import java.io.File
import collection.immutable.List

class InvalidDirectoryException(message: String) extends IllegalArgumentException(message)

class SodaTestResultSummary(val testName: String, val testPath: String, val mismatchCount: Int, val errorCount: Int) {
  val failed = mismatchCount != 0 || errorCount != 0
}

class SodaFolderRunner(val resultWriter: SodaTestResultWriter, val resultSummaryWriter: SodaTestResultSummaryWriter) {

  @throws(classOf[InvalidDirectoryException])
  def run(inputRoot: File, outputRoot: File, successCallback: (Boolean) => Unit)(implicit properties: SodaTestProperties, log: SodaTestLog): Unit = {
    checkDirectories(inputRoot, outputRoot)

    val files = getFilesRecursive(inputRoot, _.getName.toLowerCase.endsWith(".csv"))

    resultWriter.createOutputDirectories(inputRoot, files, outputRoot)

    val filesAndResults = files.map(f => (f, SodaFileRunner.run(f)))

    resultWriter.writeResultsFiles(filesAndResults, inputRoot, outputRoot)

    val resultsSummaries = summariseList(filesAndResults.map(_._2))

    resultSummaryWriter.writeSummaries(resultsSummaries)

    val succeeded = !resultsSummaries.map(r => r.mismatchCount == 0 && r.errorCount == 0).contains(false)
    successCallback(succeeded)
  }

  private def getFilesRecursive(inputDirectory: File, fileFilter: File => Boolean): List[File] = {
    inputDirectory.listFiles.filter(fileFilter).toList ++
      inputDirectory.listFiles.filter(_.isDirectory).map(getFilesRecursive(_, fileFilter)).toList.flatten
  }

  @throws(classOf[InvalidDirectoryException])
  private def checkDirectories(inputDirectory: File, outputDirectory: File): Unit = {
    if (!inputDirectory.exists)
      throw new InvalidDirectoryException("Input directory " + inputDirectory.getAbsolutePath + " does not exist")

    if (!inputDirectory.isDirectory)
      throw new InvalidDirectoryException("Input directory " + inputDirectory.getAbsolutePath + " is not a directory")

    if (!inputDirectory.canRead)
      throw new InvalidDirectoryException("Insufficient permissions to read input directory " + inputDirectory.getAbsolutePath)

    if (!outputDirectory.exists && !outputDirectory.mkdirs)
      throw new InvalidDirectoryException("Failed to create output directory " + outputDirectory.getAbsolutePath)

    if (!outputDirectory.isDirectory)
      throw new InvalidDirectoryException("Output directory " + inputDirectory.getAbsolutePath + " is not a directory")
  }

  private def summariseList(rs: List[SodaTestResult]): List[SodaTestResultSummary] =
    rs.map(r => summarise(r))

  private def summarise(r: SodaTestResult): SodaTestResultSummary = {
    val mismatchedBlocks: Int = r.results.flatMap {_ match {
        case rbr: ReportBlockResult => rbr.executionResults.map {er => if (er.matchResult.passed) 0 else 1}
        case _ => Nil
    }}.sum

    val blockErrors = r.results.map(br => {(if (br.error == None) 0 else 1)}).sum

    val executionErrors = r.results.flatMap(br => {br match {
        case rbr: ReportBlockResult => rbr.executionResults.map{er => if (er.error == None) 0 else 1}
        case ebr: EventBlockResult => ebr.executionResults.map{er => if (er.error == None) 0 else 1}
        case _ => Nil
      }}).sum

    new SodaTestResultSummary(r.test.testName, r.test.testPath, mismatchedBlocks, blockErrors + executionErrors)
  }
}

object SodaFolderRunner {

  def main(args: Array[String]): Unit = {
    main(args, succeeded => { exit(if (succeeded) 0 else 1) } );
  }

  def main(args: Array[String], successCallback: (Boolean) => Unit): Unit = {
    if (args.length != 3) {
      usage()
      successCallback(false)
    } else {
      val fixtureRoot = args(0)
      val inputDirectory = new File(args(1))
      val outputDirectory = new File(args(2))
      implicit val log = new ConsoleLog()
      implicit val properties = new SodaTestProperties(fixtureRoot)

      try {
        new SodaFolderRunner(XhtmlSodaTestResultWriter, ConsoleResultSummaryWriter).run(inputDirectory, outputDirectory, successCallback)
      }
      catch {
        case e: InvalidDirectoryException => usage(Some("Error: " + e.getMessage)); successCallback(false)
      }
    }
  }

  private def usage(): Unit = usage(None)

  private def usage(message: Option[String]): Unit = {
    message map {System.err.println(_)}
    System.err.println("usage: SodaDirectoryRunner <fixture_root_package> <input_directory> <output_directory>")
  }
}