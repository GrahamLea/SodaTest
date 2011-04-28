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

import collection.immutable.SortedSet
import runtime.data.blocks._
import collection.immutable.List._
import runtime.data.results._
import xml.{Text, Node, NodeSeq}

private[xhtml] object XhtmlBlockFormatter {
  val NEWLINE = Text("\n")
  val NO_XML = Text("")

  val letters = ('A' to 'Z' toList).map(_.toString)
}

private[xhtml] class XhtmlBlockFormatter[T <: Block](val result: BlockResult[T]) {

  val NEWLINE = Text("\n")
  val NO_XML = Text("")

  val letters = ('A' to 'Z' toList).map(_.toString)

  @inline
  def block = result.block
  @inline
  def blockSource = result.block.source

  val sourceBlockWidth: Int = SortedSet(blockSource.lines.map(_.cells.length): _*).last

  def columnHeaders: Node = {
    <tr>
      <td/>
      {for (letter <- letters.take(sourceBlockWidth)) yield
      <th>{letter}</th> ++ NEWLINE}
    </tr>
  }

  def blockHeader: Node = {
    val isInlineReport = result.block.inline && result.block.getClass == classOf[ReportBlock]
    <tr>
      <th>{result.block.source.lines(0).lineNumber}</th>
      <td class="instruction">{result.block.source.lines(0).cells(0)}</td>
      {td("name", result.block.name, "failureSource", extraClassCondition = result.error != None || (result.block.inline && result.executionErrorOccurred))}
      { if (isInlineReport)
            <td class={"reportInvoker " + (if (result.executionErrorOccurred) "failure" else "success")}>!!</td>
        else ""
      }
      { if (sourceBlockWidth > (result.block.source.lines(0).cells.size))
          emptyCellsFrom(result.block.source.lines(0).cells.size)
      }
      { if (result.executionErrorOccurred)
          <td class="failureAccomodator"/>
        else
          ""
      }
    </tr>
  }

  def resultError: Node = {
    result.error match {
      case None => NO_XML
      case Some(error) =>
        failureDetails(() => {
          errorDetails("B: " + error.message, error.causeString, None, stackTrace = error.causeTrace)
        })
    }
  }

  def failureDetails(errorsFunction: () => Seq[NodeSeq]): Node = {
    <tr class="failureDetails">
        <td/>
        <td colspan={String.valueOf(sourceBlockWidth + 1)}>
            <ul>
              {errorsFunction()}
            </ul>
        </td>
    </tr>
  }

  def errorDetails(errorMessage: String, errorCause: Option[String], causeToString: Option[String] = None,
                   stackTrace: Option[Array[StackTraceElement]]): Node = {
      <li>
        <p class="errorMessage">{errorMessage}</p>
        { errorCause map (cs => <p class="errorCause">{cs}</p>) getOrElse "" }
        { stackTrace map (st =>
            <p class="stackTrace">{
causeToString.getOrElse(errorCause.getOrElse("")) + "\n  at " + st.map(_.toString).mkString("\n  at ") + "\n"
           }</p>) getOrElse NO_XML
        }
      </li>
  }

  @inline
  def td[T <: Block](htmlClass: String, value: String, extraClass: String, extraClassCondition: Boolean) = {
    <td class={htmlClass + (if (extraClassCondition) " " + extraClass else "")}>{value}</td>
  }

  @inline
  def emptyCellsFrom(lastNonEmptyCell: Int): Seq[NodeSeq] = {
    (for (val i <- lastNonEmptyCell + 1 to sourceBlockWidth) yield List(<td></td>, NEWLINE)).flatten
  }

}