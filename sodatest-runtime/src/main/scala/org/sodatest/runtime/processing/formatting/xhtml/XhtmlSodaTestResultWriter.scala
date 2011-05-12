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

import org.sodatest.runtime.data.results.SodaTestResult
import org.sodatest.api.SodaTestLog
import java.io.{File, FileWriter, PrintWriter}
import org.sodatest.runtime.processing.running.{SodaTestResultWriter, PathUtils}

object XhtmlSodaTestResultWriter extends SodaTestResultWriter {

  import PathUtils._

  @Override
  def createOutputDirectories(inputRoot: File, files: scala.List[File], outputRoot: File) {
    val inputDirectories = files.map(_.getParentFile).toSet
    for (outputDirectory <- inputDirectories.map(getOutputPath(_, inputRoot, outputRoot))) {
      if (!outputDirectory.exists && !outputDirectory.mkdirs)
        error("Failed to create output directory " + outputDirectory.getAbsolutePath)
    }
  }

  @Override
  def writeResultsFiles(filesAndResults: List[(File, SodaTestResult)], inputRoot: File, outputRoot: File)(implicit log: SodaTestLog): Unit = {
    for ((file, result) <- filesAndResults) {
      val writer = new PrintWriter(new FileWriter(getOutputPath(file, inputRoot, outputRoot, ".html")))
      try {
        writer.println(new XhtmlFormatter().format(result))
      } finally {
        writer.close
      }
    }
  }


}