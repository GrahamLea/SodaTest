// Copyright (c) 2010 Belmont Technology Pty Ltd. All rights reserved.

package au.com.belmonttechnology.sodatest.runtime.processing.parsing.csv

import java.io.ByteArrayInputStream
import au.com.belmonttechnology.sodatest.runtime.NoOpLog
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