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

import formatting.xhtml.XhtmlFormatter
import execution.SodaTestExecutor
import processing.SodaTestContext
import data.SodaTest
import parsing.blocks.{BlockSourceSplitter, BlockFactory}
import parsing.csv.CsvCellSplitter
import java.io.{FileWriter, PrintWriter, FileInputStream, BufferedInputStream, File}
import org.sodatest.api.SodaTestLog

object SodaFileRunner {

  def execute(inputFile: File, outputFile: File, properties: SodaTestProperties)(implicit log : SodaTestLog) {
    log.info("Running " + inputFile)
    val output = run(inputFile, outputFile, properties)
    log.info("Writing " + outputFile)
    val out = new PrintWriter(new FileWriter(outputFile))
    try {
      out.println(output)
    } finally {
      out.close
    }
  }

  private def run(inputFile: File, outputFile: File, properties: SodaTestProperties)(implicit log : SodaTestLog) = {
    new XhtmlFormatter().format(
      new SodaTestExecutor().execute(
        new SodaTest(SodaFileUtils.getTestName(inputFile), inputFile.toString, new BlockFactory().create(
          new BlockSourceSplitter().parseBlocks(
            new CsvCellSplitter().split(
              new BufferedInputStream(new FileInputStream(inputFile))
            )
          ))
        ),
        new SodaTestContext(properties = properties)
      )
    )
  }

  def main(args: Array[String]) {
    //TODO: Main for one file?
  }
}