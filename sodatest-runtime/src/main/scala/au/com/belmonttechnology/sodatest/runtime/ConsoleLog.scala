// Copyright (c) 2010 Belmont Technology Pty Ltd. All rights reserved.

package au.com.belmonttechnology.sodatest.runtime

import au.com.belmonttechnology.sodatest.api.SodaTestLog

class ConsoleLog extends SodaTestLog {
  def error(message: String) = println("Error: " + message)
  def info(message: String) = println("Info:  " + message)
  def debug(message: String) = println("Debug: " + message)
}