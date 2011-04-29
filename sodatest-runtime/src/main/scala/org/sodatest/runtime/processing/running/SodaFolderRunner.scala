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
object SodaFolderRunner {

  class InvalidDirectoryException(message: String) extends IllegalArgumentException(message)

  def run(inputDirectory: File, outputDirectory: File)(implicit properties: SodaTestProperties, log: SodaTestLog): Boolean = {
    checkDirectories(inputDirectory, outputDirectory)

    def runRecursive(inputDirectory: File, outputDirectory: File)(implicit properties: SodaTestProperties, log: SodaTestLog): Boolean = {
      if (!outputDirectory.exists && !outputDirectory.mkdirs)
        error("Failed to create output directory " + outputDirectory.getAbsolutePath)

      val resultsInDirectory =
        for (val testFile <- inputDirectory.listFiles.filter(_.getName.toLowerCase.endsWith(".csv"))) yield {
          SodaFileRunner.execute(testFile, new File(outputDirectory, testFile.getName + ".html"), properties)
        }

      // Recurse into sub-directories
      val resultsInSubdirectories =
        for (val inputSubdirectory <- inputDirectory.listFiles.filter(_.isDirectory)) yield {
          runRecursive(inputSubdirectory, new File(outputDirectory, inputSubdirectory.getName))
        }

      !(resultsInDirectory ++ resultsInSubdirectories).contains(false)
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
      run(inputDirectory, outputDirectory)
    } catch {
      case e: InvalidDirectoryException => usage(Some("Error: " + e.getMessage))
    }
  }

  private def usage: Nothing = usage(None)

  private def usage(message: Option[String]): Nothing = {
    message map {System.err.println(_)}

    System.err.println("usage: SodaDirectoryRunner <fixture_root_package> <input_directory> <output_directory>")
    exit(1)
  }
}