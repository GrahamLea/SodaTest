// Copyright (c) 2010 Belmont Technology Pty Ltd. All rights reserved.

package org.sodatest.runtime.processing.running

import org.specs.SpecificationWithJUnit
import java.io.File

class SodaFileUtilsSpec extends SpecificationWithJUnit {

  "SodaFileUtils" should {
    "format a test name" in {
      SodaFileUtils.getTestName(new File("test/directory/SomeTestFile.csv")) must_== "Some Test File"
    }
    "remove underscores, hyphens, periods and extra spaces" in {
      SodaFileUtils.getTestName(new File("test/directory/ This.is.  Some_Test-File  .csv")) must_== "This is Some Test File"
    }
    "upper case the first character" in {
      SodaFileUtils.getTestName(new File("test/directory/_someTestFile.csv")) must_== "Some Test File"
    }
  }
}