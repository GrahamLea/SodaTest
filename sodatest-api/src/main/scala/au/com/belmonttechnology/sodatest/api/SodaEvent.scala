// Copyright (c) 2010 Belmont Technology Pty Ltd. All rights reserved.

package au.com.belmonttechnology.sodatest.api

trait SodaEvent {
  def apply(parameters: Map[String, String]): Unit
}