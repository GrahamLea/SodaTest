// Copyright (c) 2010 Belmont Technology Pty Ltd. All rights reserved.

package org.sodatest.runtime

import org.sodatest.api.SodaTestLog

object NoOpLog extends SodaTestLog {
  def debug(message: String) = {}

  def info(message: String) = {}

  def error(message: String) = {}
}