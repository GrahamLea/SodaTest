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
package runtime.data.results

import runtime.data.blocks.{ParameterValuesContainer, ReportExecution, Line, ReportBlock}

class ExecutionResult[P <: ParameterValuesContainer](val execution: P, val error: Option[ExecutionError])

trait ExecutionResultContainer[P <: ParameterValuesContainer, R <: ExecutionResult[P]] {
  val executionResults: List[R]
}

class ReportExecutionResult(execution: ReportExecution, val matchResult: ReportMatchResult, error: Option[ExecutionError] = None)
extends ExecutionResult[ReportExecution](execution, error) {
  def this(execution: ReportExecution, error: ExecutionError) = this(execution, new ReportMatchResult(Nil), Some(error))

  override def toString = String.format("ReportExecutionResult(execution: %s, matchResult: %s, error: %s)", execution, matchResult, error)
}

class ReportMatchResult(val lineResults: List[ReportMatchLineResult]) {
  val passed: Boolean = lineResults.foldLeft(true)((allMatch, line) => {allMatch && line.isInstanceOf[ReportLineMatch]})

  override def toString = if (passed) "Passed" else lineResults.toString
}

object ReportMatchResult {
  def allGood(lines: List[Line]): ReportMatchResult = {
    new ReportMatchResult(lines.map(line => new ReportLineMatch(line, line.cells)))
  }
}

abstract sealed class ReportMatchLineResult
case class ReportLineMatch(sourceLine: Line, cells: List[String]) extends ReportMatchLineResult
case class ReportLineMismatch(sourceLine: Line, cellResults: List[ReportMatchCellResult]) extends ReportMatchLineResult
case class ReportLineMissing(sourceLine: Line) extends ReportMatchLineResult
case class ReportLineExtra(cells: Seq[String]) extends ReportMatchLineResult

abstract sealed class ReportMatchCellResult
case class ReportCellMatch(value: String) extends ReportMatchCellResult
case class ReportCellMismatch(expectedValue: String, actualValue: String) extends ReportMatchCellResult
case class ReportCellMissing(expectedValue: String) extends ReportMatchCellResult
case class ReportCellExtra(actualValue: String) extends ReportMatchCellResult

class ReportBlockResult(val executionResults: List[ReportExecutionResult], error: Option[ExecutionError] = None)(implicit block: ReportBlock)
extends BlockResult[ReportBlock](
  block,
  succeeded = (error == None && !(executionResults.map(r => r.matchResult.passed && r.error == None).contains(false))),
  executionErrorOccurred = error != None || !executionResults.filter(_.error != None).isEmpty,
  blockError = error)
with ExecutionResultContainer[ReportExecution, ReportExecutionResult] {

  def this(error: ExecutionError)(implicit block: ReportBlock) = this(Nil, Some(error))
}
