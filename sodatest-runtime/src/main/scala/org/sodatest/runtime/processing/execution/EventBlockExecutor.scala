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

import api.SodaFixture
import coercion.{NameMatchesMoreThanOneMethodException, ReflectionTargetReturnsTheWrongTypeException}
import java.lang.reflect.InvocationTargetException
import runtime.data.blocks.{ReportBlock, EventBlock}
import runtime.data.results.{ReportBlockResult, EventExecutionResult, ExecutionError, EventBlockResult}

object EventBlockExecutor {
  def execute(implicit block: EventBlock, context: SodaTestExecutionContext): EventBlockResult = {
    context.currentFixture match {
      case None => new EventBlockResult(Nil, Some(new ExecutionError("No Fixture has been declared before this Event")))
      case Some(fixture) =>
        (try { Right(fixture.createEvent(block.name)) }
         catch {
           case e: ReflectionTargetReturnsTheWrongTypeException => Left(blockError(fixture, "The function that matches this name does not return an Event", Some(e)))
           case e: NameMatchesMoreThanOneMethodException => Left(blockError(fixture, "The Event name is ambiguous in the current Fixture", Some(e)))
           case e: InvocationTargetException => Left(blockError(fixture, "An error occurred while creating the Event", Some(e.getCause)))
           case e: Throwable => Left(blockError(fixture, "An error occurred while creating the Event", Some(e)))
        }) match {
          case Left(blockError) => blockError
          case Right(Some(_)) => runEvent(fixture, block, context)
          case _ => blockError(fixture, "Fixture '" + fixture.getClass.getSimpleName + "' doesn't know how to create an Event for '" + block.name + "'")
        }
    }
  }

  private def blockError(fixture: SodaFixture, message: String, cause: Option[Throwable] = None)(implicit block: EventBlock) =
    new EventBlockResult(Nil, Some(new ExecutionError(message, cause)))

  private def runEvent(implicit fixture: SodaFixture, block: EventBlock, context: SodaTestExecutionContext): EventBlockResult = {
    new EventBlockResult(
      for (val execution <- block.executions) yield {
        try {
          fixture.createEvent(block.name) match {
            case None => throw new RuntimeException("Fixture '" + fixture.getClass.getSimpleName + "' created one Event for '" + block.name + "' but subsequently refused to create more.")
            case Some(event) => {
              event(block.parameterMap(execution))
              new EventExecutionResult(execution)
            }
          }
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