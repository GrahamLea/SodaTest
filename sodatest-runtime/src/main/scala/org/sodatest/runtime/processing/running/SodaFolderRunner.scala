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
import java.io.{FileWriter, PrintWriter, File}
import formatting.xhtml.XhtmlFormatter
import annotation.tailrec
import collection.immutable.List

object SodaFolderRunner {

  class SodaTestResultSummary(val testName: String, val testPath: String, val mismatchCount: Int, val errorCount: Int) {
    val failed = mismatchCount != 0 || errorCount != 0
  }

  class InvalidDirectoryException(message: String) extends IllegalArgumentException(message)

  def run(inputRoot: File, outputRoot: File, successCallback: (Boolean) => Unit)(implicit properties: SodaTestProperties, log: SodaTestLog): Unit = {
    try {
      checkDirectories(inputRoot, outputRoot)

      val files = getFilesRecursive(inputRoot, _.getName.toLowerCase.endsWith(".csv"))

      createOutputDirectories(inputRoot, files, outputRoot)

      val filesAndResults = files.map(f => (f, SodaFileRunner.run(f)))

      writeResultsFiles(filesAndResults, inputRoot, outputRoot)

      val resultsSummaries = summariseList(filesAndResults.map(_._2))

      printSummary(resultsSummaries)

      val succeeded = !resultsSummaries.map(r => r.mismatchCount == 0 && r.errorCount == 0).contains(false)
      successCallback(succeeded)
    }
    catch {
      case e: InvalidDirectoryException => usage(Some("Error: " + e.getMessage)); successCallback(false)
    }
  }

  private def createOutputDirectories(inputRoot: File, files: scala.List[File], outputRoot: File) {
    val inputDirectories = files.map(_.getParentFile).toSet
    val inputRootSize = asList(inputRoot).size
    for (outputDirectory <- inputDirectories.map(getOutputPath(_, inputRootSize, outputRoot))) {
      if (!outputDirectory.exists && !outputDirectory.mkdirs)
        error("Failed to create output directory " + outputDirectory.getAbsolutePath)
    }
  }

  private def writeResultsFiles(filesAndResults: List[(File, SodaTestResult)], inputRoot: File, outputRoot: File)(implicit log: SodaTestLog): Unit = {
    val inputRootSize = asList(inputRoot).size
    for ((file, result) <- filesAndResults) {
      val writer = new PrintWriter(new FileWriter(getOutputPath(file, inputRootSize, outputRoot, ".html")))
      try {
        writer.println(new XhtmlFormatter().format(result))
      } finally {
        writer.close
      }
    }
  }

  private def getOutputPath(inputPath: File, inputRootSize: Int, outputRoot: File, newSuffix: String = ""): File = {
    val relativePathList = asList(inputPath).drop(inputRootSize)
    new File(outputRoot, relativePathList.mkString(File.separator) + newSuffix)
  }

  @tailrec
  private def asList(file: File, list: List[String] = Nil): List[String] = {
    file.getParentFile match {
      case null => list
      case p => asList(p, file.getName :: list)
    }
  }

  private def getFilesRecursive(inputDirectory: File, fileFilter: File => Boolean): List[File] = {
    inputDirectory.listFiles.filter(fileFilter).toList ++
      inputDirectory.listFiles.filter(_.isDirectory).map(getFilesRecursive(_, fileFilter)).toList.flatten
  }

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

  def main(args: Array[String]): Unit = {
    main(args, succeeded => { exit(if (succeeded) 0 else 1) } );
  }

  def main(args: Array[String], successCallback: (Boolean) => Unit): Unit = {
    if (args.length != 3) {
      usage
      successCallback(false)
    } else {
      val fixtureRoot = args(0)
      val inputDirectory = new File(args(1))
      val outputDirectory = new File(args(2))
      implicit val log = new ConsoleLog()
      implicit val properties = new SodaTestProperties(fixtureRoot)

      run(inputDirectory, outputDirectory, successCallback)
    }
  }

  private def printSummary(results: Seq[SodaTestResultSummary]): Unit = {
    val totalFailedTests = results.filter(_.failed).size
    val totalErrors = results.map(_.errorCount).sum
    val totalMismatches = results.map(_.mismatchCount).sum
    println("----------------------------------------")
    if (totalErrors == 0 && totalMismatches == 0) {
      printf("%s Test%s ran\n", results.size, if (results.size == 1) "" else "s")
      println("No errors or mismatches")
      println("SodaTest Result: ALL TESTS PASSED")
    } else {
      println("Failing Tests:")
      for (failedTest <- results.filter(r => r.errorCount != 0 || r.mismatchCount != 0)) {
        printf("\t%s (%s)\n", failedTest.testName, failedTest.testPath)
      }
      printf("%s Test%s ran\n", results.size, if (results.size == 1) "" else "s")
      printf("%s Test%s failed\n", totalFailedTests, if (totalFailedTests == 1) "" else "s")
      if (totalMismatches != 0)
        println("\t" + totalMismatches + " Report(s) had mismatches")
      if (totalErrors != 0)
        println("\t" + totalErrors + " Block(s) caused errors")
      println("SodaTest Result: THERE WERE FAILURES")
    }
    println("----------------------------------------")
  }

  private def usage: Unit = usage(None)

  private def usage(message: Option[String]): Unit = {
    message map {System.err.println(_)}
    System.err.println("usage: SodaDirectoryRunner <fixture_root_package> <input_directory> <output_directory>")
  }
}