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

package org.sodatest.runtime.processing.running

import java.io.File
import xml.factory.XMLLoader
import xml.Elem
import org.junit.{After, Test}
import org.sodatest.runtime.processing.SodaTestContext

class SodaFolderRunnerTest extends XMLLoader[Elem] {

  val systemTempDir = new File(System.getProperty("java.io.tmpdir"))
  val tempFile = File.createTempFile("SodaFolderRunnerTest.", ".tmp", systemTempDir)
  tempFile.deleteOnExit()

  implicit val context = new SodaTestContext

  @Test(expected = classOf[InvalidDirectoryException])
  def nonExistentInputDirectory() {
    new SodaFolderRunner(null, null).runTestsAndWriteResults(new File(systemTempDir, "DOESNOTEXIST.sodatest"), systemTempDir, (b) => {})
  }

  @Test(expected = classOf[InvalidDirectoryException])
  def nonDirectoryInputDirectory() {
    new SodaFolderRunner(null, null).runTestsAndWriteResults(tempFile, systemTempDir, (b) => {})
  }

  @Test(expected = classOf[InvalidDirectoryException])
  def nonDirectoryOutputFolder() {
    new SodaFolderRunner(null, null).runTestsAndWriteResults(systemTempDir, tempFile, (b) => {})
  }

  private def changeTempFileToDirectory(): Unit = {
    tempFile.delete
    tempFile.mkdir
  }

  @After
  def removeTempFile(): Unit = {
    tempFile.setWritable(true);
    tempFile.delete
  }
}

