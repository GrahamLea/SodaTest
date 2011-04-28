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

  def run(inputFolder: File, outputFolder: File)(implicit properties: SodaTestProperties, log: SodaTestLog) {
    // TODO: something nicer than assertions
    assert(inputFolder.exists, "Input folder doesn't exist: " + inputFolder)
    assert(outputFolder.exists || outputFolder.mkdirs, "Failed to create " + outputFolder)

    val csvFiles = inputFolder.listFiles.toList.filter(_.getName.toLowerCase.endsWith(".csv"))
    for (val testFile <- csvFiles) {
      SodaFileRunner.execute(testFile, new File(outputFolder, testFile.getName + ".html"), properties)
    }

    for (val inputSubdirectory <- inputFolder.listFiles.filter(_.isDirectory)) {
      run(inputSubdirectory, new File(outputFolder, inputSubdirectory.getName))
    }

    //TODO: return codes
  }

  def main(args: Array[String]) {
    // TODO: Argument checking
    val fixtureRoot = args(0)
    val inputFolder = new File(args(1))
    val outputFolder = new File(args(2))
    implicit val log = new ConsoleLog()

    implicit val properties = new SodaTestProperties(fixtureRoot)
    run(inputFolder, outputFolder)
  }

}