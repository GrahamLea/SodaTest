// Copyright (c) 2010 Belmont Technology Pty Ltd. All rights reserved.

package org.sodatest.api

trait SodaTestLog {
  def error(message: String): Unit
  def info(message: String): Unit
  def debug(message: String): Unit
}