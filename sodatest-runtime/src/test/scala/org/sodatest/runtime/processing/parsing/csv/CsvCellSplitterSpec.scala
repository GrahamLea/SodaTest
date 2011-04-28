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

package org.sodatest.runtime.processing.parsing.csv

import java.io.ByteArrayInputStream
import org.sodatest.runtime.NoOpLog
import org.specs.SpecificationWithJUnit

class CsvCellSplitterSpec extends SpecificationWithJUnit {

  implicit val log = NoOpLog
  val splitter = new CsvCellSplitter

  implicit def string2InputStream(s: String) = new ByteArrayInputStream(s.getBytes("UTF-8"))

  "CsvCellSplitter" should {
    "split normal text" in {
       splitter.split("abc,123\nxyz,098,765") must_== List(List("abc", "123"), List("xyz", "098", "765"))
    }

    "include quoted commas" in {
       splitter.split("Test,\"Test,Test\",Test") must_== List(List("Test", "Test,Test", "Test"))
    }

    "include escaped commas" in {
       splitter.split("Test,Test\\,Test,Test") must_== List(List("Test", "Test,Test", "Test"))
    }

    "include escaped quotes" in {
       splitter.split("Test,Test\\\"Test,Test") must_== List(List("Test", "Test\"Test", "Test"))
    }
  }

}