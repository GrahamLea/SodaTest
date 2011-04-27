// Copyright (c) 2010 Belmont Technology Pty Ltd. All rights reserved.

package au.com.belmonttechnology.sodatest.runtime
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
    val eventName: String,
    inline: Boolean,
    parameterNames: List[String],
    val executions: List[EventExecution]
) extends ParamterisedBlock(source, eventName, inline, parameterNames) {

  def execute(context: SodaTestExecutionContext): EventBlockResult = EventBlockExecutor.execute(this, context)

  def parameterMap(execution: EventExecution): Map[String, String] = {
    execution.parameterValues match {
      case Some(line) => (parameterNames zip line.cells.tail) toMap
      case None => Map()
    }
  }

  override def toString = "EventBlock: " + eventName
}