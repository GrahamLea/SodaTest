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
package runtime
package processing.formatting.xhtml

import api.SodaTestLog
import data.blocks.Line
import data.results.ParseErrorBlockResult
import xml.{NodeSeq, Elem}
import java.lang.Short

object XhtmlParseErrorFormatter {

  import XhtmlBlockFormatter._

  def format(result: ParseErrorBlockResult)(implicit log: SodaTestLog): Elem = {
    log.verbose("      " + result)
    val lines: List[Line] = result.block.source.lines
    val formatter = new XhtmlBlockFormatter(result)
    val errorSource: (Int, Int) = result.block.errorSource
    val firstLineNumber = result.block.source.lines(0).lineNumber

    <div class="blockResult parseError failure">
      <p class="errorMessage"><span class="parseErrorLabel">Parse Error:</span> {result.block.error}</p>
      <table>
        <colgroup><col class="lineNumbers"/></colgroup>
        <thead>
          {formatter.columnHeaders}
        </thead>
        <tbody>
          <tr>
            <th>{result.block.source.lines(0).lineNumber}</th>
            <td class={"instruction" + (if (errorSource == (0, 0)) " failureSource" else "")}>{result.block.source.lines(0).cells(0)}</td>
            {tds(result.block.source.lines(0).cells.tail, (errorSource._1, errorSource._2 - 1), formatter.sourceBlockWidth, 0)}
            {formatter.emptyCellsFrom(2) }
          </tr>
          { for (line <- result.block.source.lines.tail) yield {
              <tr>
                <th>{line.lineNumber}</th>
                {tds(line.cells, errorSource, formatter.sourceBlockWidth, line.lineNumber - firstLineNumber)}
                {formatter.emptyCellsFrom(line.cells.size) }
              </tr> ++ NEWLINE
            }
          }
        </tbody>
      </table>
    </div>
  }

  private def tds(cells: List[String], errorSource: (Int, Int), sourceBlockWidth: Int, lineOffset: Int): NodeSeq = {
    (for ((cell, cellIndex) <- (cells zip (0 to Short.MAX_VALUE))) yield {
      if ((lineOffset, cellIndex) == errorSource) {
        <td class="failureSource">{cell}</td>
      } else {
        <td>{cell}</td>
      }
    }).foldLeft(NO_XML.asInstanceOf[NodeSeq])(_ ++ NEWLINE ++ _) 

  }
}