// Copyright (c) 2010 Belmont Technology Pty Ltd. All rights reserved.

package org.sodatest.runtime.data
package results

import blocks.FixtureBlock

class FixtureBlockResult(error: Option[ExecutionError] = None)(implicit block: FixtureBlock)
        extends BlockResult[FixtureBlock](block, executionErrorOccurred = error != None, error = error) {
  
  override def toString =
    getClass.getSimpleName + ": " + block.fixtureName +
      (error match {case Some(e) => " [Error: " + e.message + "]"; case _ => ""})
}

