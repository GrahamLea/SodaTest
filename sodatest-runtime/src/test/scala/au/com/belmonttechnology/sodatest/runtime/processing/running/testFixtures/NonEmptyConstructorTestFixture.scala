// Copyright (c) 2010 Belmont Technology Pty Ltd. All rights reserved.

package au.com.belmonttechnology.sodatest.runtime.processing.running.testFixtures

import au.com.belmonttechnology.sodatest.api.SodaFixture

class NonEmptyConstructorTestFixture(val s: String) extends SodaFixture {
  def createReport(name: String) = None
  def createEvent(name: String) = None
}