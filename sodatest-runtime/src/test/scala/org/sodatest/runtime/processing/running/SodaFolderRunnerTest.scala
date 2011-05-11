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
import org.sodatest.runtime.processing.SodaTestProperties
import org.sodatest.runtime.ConsoleLog
import org.junit.{After, Test}
import org.hamcrest.CoreMatchers._
import org.junit.Assert._

class SodaFolderRunnerTest extends XMLLoader[Elem] {

  val systemTempDir = new File(System.getProperty("java.io.tmpdir"))
  val tempFile = File.createTempFile("SodaFolderRunnerTest.", ".tmp", systemTempDir)
  tempFile.deleteOnExit()

  implicit val properties = new SodaTestProperties()
  implicit val log = new ConsoleLog()

  @Test
  def nonExistentInputDirectory() {
    var success: Option[Boolean] = None
    SodaFolderRunner.run(new File(systemTempDir, "DOESNOTEXIST.sodatest"), systemTempDir, (b) => {success = Some(b)})
    assertThat(success.get, is(false))
  }

  @Test
  def nonDirectoryInputDirectory() {
    var success: Option[Boolean] = None
    SodaFolderRunner.run(tempFile, systemTempDir, (b) => {success = Some(b)})
    assertThat(success.get, is(false))
  }

    // TODO: Does this work on Unix?
//  @Test
//  def unreadableInputDirectory() {
//    changeTempFileToDirectory()
//    tempFile.setReadable(false)
//    assertFalse(tempFile.canRead)
//    var success: Option[Boolean] = None
//    SodaFolderRunner.run(tempFile, systemTempDir, (b) => {success = Some(b)})
//    assertThat(success.get, is(false))
//  }

  @Test
  def nonDirectoryOutputFolder() {
    var success: Option[Boolean] = None
    SodaFolderRunner.run(systemTempDir, tempFile, (b) => {success = Some(b)})
    assertThat(success.get, is(false))
  }

    // TODO: Does this work on Unix?
//  @Test
//  def nonCreatableOutputFolder() {
//    changeTempFileToDirectory()
//    assertTrue(tempFile.setWritable(false))
//    assertFalse(tempFile.canWrite)
//    var success: Option[Boolean] = None
//    SodaFolderRunner.run(systemTempDir, new File(tempFile, "subdir"), (b) => {success = Some(b)})
//    assertThat(success.get, is(false))
//  }

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

