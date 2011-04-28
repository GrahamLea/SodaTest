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
package runtime.processing.formatting.xhtml

import api.SodaTestLog
import runtime.data.results._
import xml.{NodeSeq, Elem}

object XhtmlReportFormatter {

  import XhtmlBlockFormatter._

  def format(result: ReportBlockResult)(implicit log: SodaTestLog): Elem = {
    log.debug("   Formatting: " + result)
    implicit val formatter = new XhtmlParameterisedBlockFormatter(result)
    <div class={"blockResult report " + (if (result.block.inline) "inline " else "") + (if (result.succeeded) "success" else "failure")}>
      <table>
        <colgroup><col class="lineNumbers"/></colgroup>
        <thead>
          {formatter.columnHeaders}
        </thead>
        <tbody>
          {formatter.blockHeader}
          {formatter.resultError}
          {formatter.parameterNames}
          {
            for (val executionResult <- result.executionResults) yield {
              formatter.parameterValuesRow(executionResult) ++
              (executionResult.error match {
                case None =>
                  executionResultRows(executionResult)

                case ContainsParameterBindingException(bindException) => {
                  formatter.failureDetails(() => {
                    formatter.bindFailureDetails(bindException, result)
                  }) ++ NEWLINE
                }

                case Some(error) => {
                  formatter.failureDetails(() => {
                    formatter.errorDetails(error.message, error.causeString, None, error.causeTrace)
                  }) ++ NEWLINE
                }
              })
            }
          }
        </tbody>
      </table>
    </div>
  }

  @inline
  private def executionResultRows(executionResult: ReportExecutionResult)(implicit formatter: XhtmlParameterisedBlockFormatter[_]): Seq[NodeSeq] = {
    if (executionResult.matchResult.passed)
      for (val line <- executionResult.execution.expectedResult) yield formatter.lineToHtml(line, "result match")
    else
      toHtml(executionResult.matchResult)
  }

  @inline
  private def toHtml(result: ReportMatchResult)(implicit formatter: XhtmlParameterisedBlockFormatter[_]): Seq[NodeSeq] = {
    for (val lineResult <- result.lineResults) yield {
      lineResult match {
        case l: ReportLineMatch => formatter.lineToHtml(l.sourceLine, "result match")
        case l: ReportLineMismatch => reportLineMismatchToHtml(l, "result mismatch")
        case l: ReportLineMissing => formatter.lineToHtml(l.sourceLine, "result missing", extraCell = Some(<td class="lineNote">(Missing line)</td>))
        case l: ReportLineExtra => formatter.cellsToHtml(None, "" :: l.cells, "result extra", extraCell = Some(<td class="lineNote">(Extra line)</td>))
      }
    }
  }

  @inline
  private def reportLineMismatchToHtml(lineMistmatch: ReportLineMismatch, rowClass: String)(implicit formatter: XhtmlParameterisedBlockFormatter[_]): NodeSeq = {
    <tr class={rowClass}>
      <th>{lineMistmatch.sourceLine.lineNumber}</th>
      <td></td>
      {for (val cell <- lineMistmatch.cellResults) yield {
        val cellClass = cell match {
          case c: ReportCellMatch => "match"
          case c: ReportCellMissing => "missing"
          case c: ReportCellExtra => "extra"
          case c: ReportCellMismatch => "mismatch"
        }

        (cell match {
          case c: ReportCellMatch =>
            <td class={cellClass}>{c.value}</td>
          case c: ReportCellMissing =>
            <td class={cellClass}><span class="valueLabel">Missing:</span> {c.expectedValue}</td>
          case c: ReportCellExtra =>
            <td class={cellClass}><span class="valueLabel">Extra:</span> {c.actualValue}</td>
          case c: ReportCellMismatch =>
            <td class={cellClass}>
              <span class="valueLabel">Expected:</span> {c.expectedValue}
              <hr/>
              <span class="valueLabel">Actual:</span> {c.actualValue}
            </td>
        }) ++ NEWLINE
      }} {formatter.emptyCellsFrom(lineMistmatch.cellResults.size + 1)}
    </tr> ++ NEWLINE
  }

}