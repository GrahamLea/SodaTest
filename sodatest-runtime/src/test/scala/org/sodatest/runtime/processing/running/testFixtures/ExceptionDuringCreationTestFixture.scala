// Copyright (c) 2010 Belmont Technology Pty Ltd. All rights reserved.

package org.sodatest.runtime.processing.running.testFixtures

import org.sodatest.api.SodaFixture

class ExceptionDuringCreationTestFixture extends SodaFixture {
  if (true) throw new RuntimeException("This fixture throws an error when created")

  def createReport(name: String) = None
  def createEvent(name: String) = None
}