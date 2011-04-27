// Copyright (c) 2010 Belmont Technology Pty Ltd. All rights reserved.

package au.com.belmonttechnology.sodatest
package runtime.processing.execution

import runtime.data.blocks.EventBlock
import runtime.data.results.{EventExecutionResult, ExecutionError, EventBlockResult}

object EventBlockExecutor {
  def execute(implicit block: EventBlock, context: SodaTestExecutionContext): EventBlockResult = {
    // TODO: Deal with action in test before fixture declared
    // TODO: Should be a new action for each execution
    val fixture = context.currentFixture.get
    fixture.createEvent(block.eventName) match {
      case None => new EventBlockResult(Nil, Some(new ExecutionError("Fixture '" + fixture.getClass.getSimpleName + "' doesn't know how to create an event for '" + block.eventName + "'")))
      case Some(event) => new EventBlockResult(
        for (val execution <- block.executions) yield {
          try {
            event(block.parameterMap(execution))
            new EventExecutionResult(execution)
          } catch {
            case e => new EventExecutionResult(execution, Some(new ExecutionError("An exception occurred while executing the event", e)))
          }
        }
      )
    }
  }
}