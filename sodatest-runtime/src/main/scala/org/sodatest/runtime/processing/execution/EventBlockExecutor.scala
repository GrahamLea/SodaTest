/*
 * Copyright (c) 2010-2011 Belmont Technology Pty Ltd. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.sodatest
package runtime.processing.execution

import runtime.data.blocks.EventBlock
import runtime.data.results.{EventExecutionResult, ExecutionError, EventBlockResult}

object EventBlockExecutor {
  def execute(implicit block: EventBlock, context: SodaTestExecutionContext): EventBlockResult = {
    // TODO: Deal with event in test before fixture declared
    // TODO: Should be a new event for each execution
    val fixture = context.currentFixture.get
    fixture.createEvent(block.eventName) match {
      case None => new EventBlockResult(Nil, Some(new ExecutionError("Fixture '" + fixture.getClass.getSimpleName + "' doesn't know how to create an event for '" + block.eventName + "'")))
      case Some(event) => new EventBlockResult(
        for (val execution <- block.executions) yield {
          try {
            event(block.parameterMap(execution))
            new EventExecutionResult(execution)
          } catch {
            case e => {
              context.testContext.log.error("Exception while executing Event (" + block + "): " + e)
              new EventExecutionResult(execution, Some(new ExecutionError("An exception occurred while executing the event", e)))
            }
          }
        }
      )
    }
  }
}