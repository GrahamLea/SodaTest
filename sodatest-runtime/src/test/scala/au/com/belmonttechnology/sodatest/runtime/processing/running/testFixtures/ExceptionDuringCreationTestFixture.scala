// Copyright (c) 2010 Belmont Technology Pty Ltd. All rights reserved.

package au.com.belmonttechnology.sodatest.runtime.processing.running.testFixtures

import au.com.belmonttechnology.sodatest.api.SodaFixture

class ExceptionDuringCreationTestFixture extends SodaFixture {
  if (true) throw new RuntimeException("This fixture throws an error when created")

  def createReport(name: String) = None
  def createEvent(name: String) = None
}