// Copyright (c) 2010 Belmont Technology Pty Ltd. All rights reserved.

package org.sodatest.runtime.processing.execution

import org.sodatest.api.SodaFixture
import org.sodatest.runtime.processing.SodaTestContext

class SodaTestExecutionContext(val testContext: SodaTestContext) {
  var currentFixture: Option[SodaFixture] = None
}