// Copyright (c) 2010 Belmont Technology Pty Ltd. All rights reserved.

package org.sodatest
package runtime.data.blocks

import runtime.processing.execution.{ReportBlockExecutor, SodaTestExecutionContext}
import runtime.data.results.ReportBlockResult

class ParameterValuesContainer(val parameterValues: Option[Line])

class ReportExecution(parameterValues: Option[Line], val expectedResult: List[Line])
extends ParameterValuesContainer(parameterValues) {
  override def toString = "ReportExecution(parameterValues: " + parameterValues + ", expectedResult: " + expectedResult + ")"
}

// TODO: Make reportName and inline not 'val' s ?
class ReportBlock(
        source: BlockSource,
        val reportName: String,
        inline: Boolean,
        parameterNames: List[String],
        val executions: List[ReportExecution]
) extends ParamterisedBlock(source, reportName, inline, parameterNames) {

  def parameterMap(execution: ReportExecution): Map[String, String] = {
    execution.parameterValues match {
      case Some(line) => (parameterNames zip line.cells.tail) toMap
      case None => Map()
    }
  }

  def execute(context: SodaTestExecutionContext): ReportBlockResult = ReportBlockExecutor.execute(this, context)

  override def toString() = "ReportBlock: " + reportName
}