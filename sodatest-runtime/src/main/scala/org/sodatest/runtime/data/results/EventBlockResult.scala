// Copyright (c) 2010 Belmont Technology Pty Ltd. All rights reserved.

package org.sodatest.runtime
package data.results

import data.blocks.{EventExecution, EventBlock}

class EventExecutionResult(execution: EventExecution, error: Option[ExecutionError] = None) extends ExecutionResult(execution, error)

class EventBlockResult(
    val executionResults: List[EventExecutionResult],
    error: Option[ExecutionError] = None
)(implicit block: EventBlock)
extends BlockResult[EventBlock](
  block,
  executionErrorOccurred = error != None || !executionResults.filter(_.error != None).isEmpty,
  error = error) {

  override def toString =
    getClass.getSimpleName + ": " + block.eventName +
      (error match {case Some(e) => " [Error: " + e.message + "]"; case _ => ""})
}
