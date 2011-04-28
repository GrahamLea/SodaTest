// Copyright (c) 2010 Belmont Technology Pty Ltd. All rights reserved.

package org.sodatest.runtime.processing.running.testFixtures

import org.sodatest.api.SodaFixture

class NonEmptyConstructorTestFixture(val s: String) extends SodaFixture {
  def createReport(name: String) = None
  def createEvent(name: String) = None
}