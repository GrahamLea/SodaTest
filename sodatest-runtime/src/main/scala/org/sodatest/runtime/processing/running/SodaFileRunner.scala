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
import java.io.{FileInputStream, BufferedInputStream, File}
import org.sodatest.api.SodaTestLog
import data.results.{NoteBlockResult, SodaTestResult}
import data.blocks.{Line, BlockSource, NoteBlock}

object SodaFileRunner {

  def run(inputFile: File)(implicit properties: SodaTestProperties, log: SodaTestLog): SodaTestResult = {
    log.info("Running " + inputFile)
    val test = new SodaTest(SodaFileUtils.getTestName(inputFile), inputFile.toString, BlockFactory.create(
      BlockSourceSplitter.parseBlocks(
        CsvCellSplitter.split(
          new BufferedInputStream(new FileInputStream(inputFile))
        )
      ))
    )
    try {
      SodaTestExecutor.execute(test, new SodaTestContext(properties))
    }
    catch {
      case t: Throwable => log.error("Error executing " + inputFile.getName + " :" + t)
      new SodaTestResult(test, List(new NoteBlockResult(new NoteBlock(BlockSource(List(
        Line(0, List("", "An uncaught error occurred while running this test")),
        Line(0, List("", t.toString))
      ))))), false)
    }
  }

  def main(args: Array[String]) {
    //TODO: Main for one file?
  }
}