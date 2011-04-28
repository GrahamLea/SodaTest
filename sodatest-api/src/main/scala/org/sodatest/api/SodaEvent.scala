// Copyright (c) 2010 Belmont Technology Pty Ltd. All rights reserved.

package org.sodatest.api

trait SodaEvent {
  def apply(parameters: Map[String, String]): Unit
}