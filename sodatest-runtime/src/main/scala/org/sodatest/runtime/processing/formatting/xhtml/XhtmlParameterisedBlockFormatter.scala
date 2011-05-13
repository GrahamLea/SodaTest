/*
 * Copyright (c) 2011 Belmont Technology Pty Ltd. All rights reserved.
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

import runtime.data.blocks._
import xml.NodeSeq
import collection.immutable.List._
import runtime.data.results._
import api.ParameterBindingException

private[xhtml] class XhtmlParameterisedBlockFormatter[T <: ParamterisedBlock](result: BlockResult[T])
  extends XhtmlBlockFormatter[T](result) {

  import XhtmlBlockFormatter._

  def parameterNames: NodeSeq = {
    if (!block.inline) {
      lineToHtml(blockSource.lines(1), "parameterNames")
    } else {
      NO_XML
    }
  }

  def bindFailureDetails(bindException: ParameterBindingException, result: BlockResult[_ <: ParamterisedBlock]) = {
    for (val failure <- bindException.bindFailures) yield {
      val parameterIndex = result.block.parameterNames.indexOf(failure.parameterName) + 1
      val blockType = if (result.isInstanceOf[ReportBlockResult]) "Report" else "Event"
      errorDetails(errorMessage = letters(parameterIndex) + ": The " + blockType + " was unable to bind the value '" + failure.parameterValue + "' to parameter '" + failure.parameterName + "'",
                    errorCause = Some(failure.errorMessage),
                    causeToString = failure.exception.map(_.toString),
                    cause = failure.exception) ++ NEWLINE
    }
  }

  def parameterValuesRow[T <: ExecutionResult[_ <: ParameterValuesContainer]](executionResult: T): NodeSeq = {
    executionResult.execution.parameterValues match {
      case None => NO_XML
      case Some(line) => {
        val (rowClass, invokerClass, bindException) = executionResult.error match {
          case None => (if (executionResult.isInstanceOf[EventExecutionResult]) " success" else "", " success", None)
          case Some(error) => error.cause match {
            case Some(bindException: ParameterBindingException) => (" failure", "", Some(bindException))
            case _ => (" failureSource", " failure", None)
          }
        }
        val bindFailureParameterNames: List[String] = bindException.map(e => e.bindFailures).getOrElse(Nil).map(_.parameterName)
        <tr class={"parameterValues" + rowClass}>
          <th>{line.lineNumber}</th>
          {for ((parameterName, value) <- ("" :: block.parameterNames) zip line.cells) yield
            (if (value == "!!")
              <td class={"reportInvoker" + invokerClass}>{value}</td>
            else if (bindFailureParameterNames.contains(parameterName)) {
              <td class="failureSource">{value}</td>
            } else {
              <td>{value}</td>
            }) ++ NEWLINE} {emptyCellsFrom(line.cells.size)}
        </tr> ++ NEWLINE
      }
    }
  }

  @inline
  def lineToHtml(line: Line, rowClass: String, containsExecutionError: Boolean = false, extraCell: Option[NodeSeq] = None): NodeSeq =
    cellsToHtml(Some(line.lineNumber), line.cells, rowClass, containsExecutionError, extraCell)

  @inline
  def cellsToHtml(lineNumber: Option[Int], cells: List[String], rowClass: String, containsExecutionError: Boolean = false, extraCell: Option[NodeSeq] = None): NodeSeq = {
    <tr class={rowClass}>
      <th>{lineNumber match { case Some(i) => i; case None => "-"}}</th>
      {for (val cell <- cells) yield
        (if (cell == "!!")
        <td class={"reportInvoker " + (if (containsExecutionError) "failure" else "success")}>{cell}</td>
        else
        <td>{cell}</td>) ++ NEWLINE} {emptyCellsFrom(cells.size)} {if (extraCell != None) extraCell.get}
    </tr> ++ NEWLINE
  }
}