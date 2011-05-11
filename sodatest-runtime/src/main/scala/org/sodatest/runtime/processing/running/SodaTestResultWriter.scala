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

trait SodaTestResultWriter {
  def createOutputDirectories(inputRoot: File, files: scala.List[File], outputRoot: File): Unit
  def writeResultsFiles(filesAndResults: List[(File, SodaTestResult)], inputRoot: File, outputRoot: File)(implicit log: SodaTestLog): Unit
}

object XhtmlSodaTestResultWriter extends SodaTestResultWriter {

  @Override
  def createOutputDirectories(inputRoot: File, files: scala.List[File], outputRoot: File) {
    val inputDirectories = files.map(_.getParentFile).toSet
    val inputRootSize = asList(inputRoot).size
    for (outputDirectory <- inputDirectories.map(getOutputPath(_, inputRootSize, outputRoot))) {
      if (!outputDirectory.exists && !outputDirectory.mkdirs)
        error("Failed to create output directory " + outputDirectory.getAbsolutePath)
    }
  }

  @Override
  def writeResultsFiles(filesAndResults: List[(File, SodaTestResult)], inputRoot: File, outputRoot: File)(implicit log: SodaTestLog): Unit = {
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

}