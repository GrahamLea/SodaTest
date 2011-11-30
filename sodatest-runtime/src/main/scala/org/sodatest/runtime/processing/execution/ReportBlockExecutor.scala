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

import runtime.data.results._
import runtime.data.blocks.{Line, ReportBlock}
import api.SodaFixture
import api.reflection.{NameMatchesMoreThanOneMethodException, ReflectionTargetReturnsTheWrongTypeException}
import java.lang.reflect.InvocationTargetException

object ReportBlockExecutor {

  def execute(implicit block: ReportBlock, context: SodaTestExecutionContext): ReportBlockResult = {
    context.currentFixture match {
      case None => new ReportBlockResult(Nil, Some(new ExecutionError("No Fixture has been declared before this Report")))
      case Some(fixture) => {
        (try { Right(fixture.createReport(block.name)) }
         catch {
           case e: ReflectionTargetReturnsTheWrongTypeException => Left(blockError(fixture, "The function that matches this name does not return a Report", Some(e)))
           case e: NameMatchesMoreThanOneMethodException => Left(blockError(fixture, "The Report name is ambiguous in the current Fixture", Some(e)))
           case e: InvocationTargetException => Left(blockError(fixture, "An error occurred while creating the Report", Some(e.getCause)))
           case e: Throwable => Left(blockError(fixture, "An error occurred while creating the Report", Some(e)))
        }) match {
          case Left(blockError) => blockError
          case Right(Some(_)) => runReport(fixture, block, context)
          case _ => blockError(fixture, "Fixture '" + fixture.getClass.getSimpleName + "' doesn't know how to create a Report for '" + block.name + "'")
        }
      }
    }
  }

  private def blockError(fixture: SodaFixture, message: String, cause: Option[Throwable] = None)(implicit block: ReportBlock) =
    new ReportBlockResult(Nil, Some(new ExecutionError(message, cause)))

  private def runReport(implicit fixture: SodaFixture, block: ReportBlock, context: SodaTestExecutionContext): ReportBlockResult = {
    new ReportBlockResult(
      for (val execution <- block.executions) yield {
        fixture.createReport(block.name) match {
          case None => throw new RuntimeException("Fixture '" + fixture.getClass.getSimpleName + "' created one Report for '" + block.name + "' but subsequently refused to create more.")
          case Some(report) => {
            try {
              val reportOutput = report(block.parameterMap(execution))
                                  .map(_.reverse.dropWhile(_.trim.isEmpty).reverse) // Drop trailing cells containing only whitespace
              val expectedResult = execution.expectedResult
              if (reportOutput == expectedResult.map(_.cells.tail))
                new ReportExecutionResult(execution, ReportMatchResult.allGood(expectedResult))
              else
                new ReportExecutionResult(execution, new ReportMatchResult(diff(expectedResult, reportOutput)))
            } catch {
              case e => {
                context.testContext.log.error("Exception while executing Report (" + block + "): " + e)
                new ReportExecutionResult(execution, new ExecutionError("An exception occurred while executing the report", e))
              }
            }
          }
        }
      }
    )
  }

  @inline
  private def diff(expected: List[Line], actual: Seq[Seq[String]]): List[ReportMatchLineResult] =
    for (val line <- expected.map{Some(_)}.zipAll(actual.map{Some(_)}, None, None)) yield compareRow(line)

  @inline
  private def compareRow(zippedRow: (Option[Line], Option[Seq[String]])) : ReportMatchLineResult = {
    zippedRow match {
      case (Some(expectedLine), Some(outputCells)) => compareCells(expectedLine, expectedLine.cells.tail.map({Some(_)}).zipAll(outputCells.map({Some(_)}), None, None))
      case (Some(expectedLine), None)              => new ReportLineMissing(expectedLine)
      case (None, Some(outputCells))               => new ReportLineExtra(outputCells)
      case (None, None)                            => throw new IllegalStateException("Shouldn't happen unless List.zipAll has a bug")
    }
  }

  @inline
  private def compareCells(line: Line, zippedCells: List[(Option[String], Option[String])]) : ReportMatchLineResult = {
    if (zippedCells.filterNot(c => {c._1 == c._2}).isEmpty)
      new ReportLineMatch(line, zippedCells.flatMap(_._1))
    else
      new ReportLineMismatch(line, zippedCells.map(compareCell(_)))
  }

  @inline
  private def compareCell(cellPair: (Option[String], Option[String])) : ReportMatchCellResult = {
    cellPair match {
      case (Some(expectedValue), Some(output)) =>
        if (output == expectedValue) new ReportCellMatch(expectedValue) else new ReportCellMismatch(expectedValue, output)

      case (Some(expectedValue), None) => new ReportCellMissing(expectedValue)
      case (None, Some(output))        => new ReportCellExtra(output)
      case (None, None)                => throw new IllegalStateException("Shouldn't happen unless List.zipAll has a bug")
    }
  }
}