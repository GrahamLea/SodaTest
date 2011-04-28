// Copyright (c) 2010 Belmont Technology Pty Ltd. All rights reserved.

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