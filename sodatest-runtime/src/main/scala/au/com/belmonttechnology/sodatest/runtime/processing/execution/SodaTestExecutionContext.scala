// Copyright (c) 2010 Belmont Technology Pty Ltd. All rights reserved.

package au.com.belmonttechnology.sodatest.runtime.processing.execution

import au.com.belmonttechnology.sodatest.api.SodaFixture
import au.com.belmonttechnology.sodatest.runtime.processing.SodaTestContext

class SodaTestExecutionContext(val testContext: SodaTestContext) {
  var currentFixture: Option[SodaFixture] = None
}