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

import java.io.File
import org.sodatest.api.SodaTestLog
import data.results.{EventBlockResult, ReportBlockResult, SodaTestResult}

object SodaFolderRunner {

  class SodaTestResultSummary(val testName: String, val testPath: String, val mismatchCount: Int, val errorCount: Int) {
    val failed = mismatchCount != 0 || errorCount != 0
  }

  class InvalidDirectoryException(message: String) extends IllegalArgumentException(message)

  def run(inputDirectory: File, outputDirectory: File)(implicit properties: SodaTestProperties, log: SodaTestLog): List[SodaTestResultSummary] = {
    checkDirectories(inputDirectory, outputDirectory)

    def runRecursive(inputDirectory: File, outputDirectory: File)(implicit properties: SodaTestProperties, log: SodaTestLog): List[SodaTestResultSummary] = {
      if (!outputDirectory.exists && !outputDirectory.mkdirs)
        error("Failed to create output directory " + outputDirectory.getAbsolutePath)

      val resultsInDirectory =
        for (val testFile <- inputDirectory.listFiles.filter(_.getName.toLowerCase.endsWith(".csv"))) yield {
          SodaFileRunner.runAndWrite(testFile, new File(outputDirectory, testFile.getName + ".html"), properties)
        }

      // Recurse into sub-directories
      val resultsInSubdirectories =
        for (val inputSubdirectory <- inputDirectory.listFiles.filter(_.isDirectory)) yield {
          runRecursive(inputSubdirectory, new File(outputDirectory, inputSubdirectory.getName))
        }

      summariseList(resultsInDirectory.toList) ++ resultsInSubdirectories.toList.flatten
    }

    runRecursive(inputDirectory, outputDirectory)
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
    exit(if (mainWithoutExit(args)) 0 else 1)
  }

  def mainWithoutExit(args: Array[String]): Boolean = {
    if (args.length != 3)
      usage

    val fixtureRoot = args(0)
    val inputDirectory = new File(args(1))
    val outputDirectory = new File(args(2))
    implicit val log = new ConsoleLog()
    implicit val properties = new SodaTestProperties(fixtureRoot)

    try {
      val results = run(inputDirectory, outputDirectory)
      printSummary(results)
      !results.map(r => r.mismatchCount == 0 && r.errorCount == 0).contains(false)
    } catch {
      case e: InvalidDirectoryException => usage(Some("Error: " + e.getMessage))
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

  private def usage: Nothing = usage(None)

  private def usage(message: Option[String]): Nothing = {
    message map {System.err.println(_)}

    System.err.println("usage: SodaDirectoryRunner <fixture_root_package> <input_directory> <output_directory>")
    exit(1)
  }
}