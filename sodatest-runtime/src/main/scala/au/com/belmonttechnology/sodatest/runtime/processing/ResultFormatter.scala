// Copyright (c) 2010 Belmont Technology Pty Ltd. All rights reserved.

package au.com.belmonttechnology.sodatest.runtime.processing

import au.com.belmonttechnology.sodatest.runtime.data.results.SodaTestResult

trait ResultFormatter {
  def format(result: SodaTestResult): String
}