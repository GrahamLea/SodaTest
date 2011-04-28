// Copyright (c) 2010 Belmont Technology Pty Ltd. All rights reserved.

package org.sodatest.runtime.processing

import org.sodatest.runtime.data.results.SodaTestResult

trait ResultFormatter {
  def format(result: SodaTestResult): String
}