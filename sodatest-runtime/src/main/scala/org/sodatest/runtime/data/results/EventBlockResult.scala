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

package org.sodatest.runtime
package data.results

import data.blocks.{EventExecution, EventBlock}

class EventExecutionResult(execution: EventExecution, error: Option[ExecutionError] = None) extends ExecutionResult(execution, error)

class EventBlockResult(
  val executionResults: List[EventExecutionResult],
  error: Option[ExecutionError] = None)
  (implicit block: EventBlock)
extends BlockResult[EventBlock](
  block,
  executionErrorOccurred = error != None || !executionResults.filter(_.error != None).isEmpty,
  error = error) {

  override def toString =
    getClass.getSimpleName + ": " + block.eventName +
      (error match {case Some(e) => " [Error: " + e.message + "]"; case _ => ""})
}
