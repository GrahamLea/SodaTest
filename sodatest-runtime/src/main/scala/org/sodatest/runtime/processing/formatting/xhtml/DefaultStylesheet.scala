// Copyright (c) 2010 Belmont Technology Pty Ltd. All rights reserved.

package org.sodatest.runtime.processing.formatting.xhtml

import io.Source

object DefaultStylesheet {
  def load(): String = Source.fromInputStream(getClass.getResourceAsStream("/defaultStylesheet.css")).getLines.reduceLeft(_ + " " + _)
}