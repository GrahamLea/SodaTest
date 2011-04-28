// Copyright (c) 2010 Belmont Technology Pty Ltd. All rights reserved.

package org.sodatest.api

trait SodaReport {
  def apply(parameters: Map[String, String]): List[List[String]]
}