package org.sodatest.runtime.processing.running.testFixtures

// Copyright (c) 2010 Belmont Technology Pty Ltd. All rights reserved.

import org.sodatest.api.SodaFixture

class GoodTestFixture extends SodaFixture {
  def createReport(name: String) = None
  def createEvent(name: String) = None
}