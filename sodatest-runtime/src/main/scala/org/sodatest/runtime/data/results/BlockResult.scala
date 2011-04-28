// Copyright (c) 2010 Belmont Technology Pty Ltd. All rights reserved.

package org.sodatest.runtime.data
package results

import blocks.Block

abstract class BlockResult[T <: Block](
  val block: T,
  val executionErrorOccurred: Boolean,
  val succeeded: Boolean,
  val error: Option[ExecutionError]) {

  def this(block: T, executionErrorOccurred: Boolean, error: Option[ExecutionError]) =
    this(block, executionErrorOccurred, !executionErrorOccurred, error)
}