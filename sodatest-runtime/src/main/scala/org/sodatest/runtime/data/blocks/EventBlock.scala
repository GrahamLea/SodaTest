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
package data.blocks

import data.results.EventBlockResult
import processing.execution.{EventBlockExecutor, SodaTestExecutionContext}

class EventExecution(parameterValues: Option[Line])
  extends ParameterValuesContainer(parameterValues) {

  override def toString = {
    "EventExecution: " + parameterValues
  }
}

class EventBlock (
    source: BlockSource,
    eventName: String,
    inline: Boolean,
    parameterNames: List[String],
    val executions: List[EventExecution]
) extends ParamterisedBlock(source, eventName, inline, parameterNames) {

  def execute(context: SodaTestExecutionContext): EventBlockResult = EventBlockExecutor.execute(this, context)

}