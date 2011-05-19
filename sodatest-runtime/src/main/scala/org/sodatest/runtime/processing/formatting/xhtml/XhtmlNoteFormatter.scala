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
import data.results.NoteBlockResult
import data.blocks.Line
import xml.{NodeSeq, Text, Elem}

object XhtmlNoteFormatter {
  import XhtmlBlockFormatter._

  def format(result: NoteBlockResult): Elem = {
    val lines: List[Line] = result.block.source.lines
    val requiresTable = lines.map({_.cells.size > 2}).contains(true)
    val formatter = new XhtmlBlockFormatter(result)
    <div class="blockResult note">
      <p class="lineNumber">Note [{lines(0).lineNumber}]</p>
      { if (requiresTable) {
      <table>
          <tbody>
            { for (line <- lines) yield {
              <tr>
                { line.cells.tail.map(cell => <td>{cell}</td> ++ NEWLINE) }
                { formatter.emptyCellsFrom(line.cells.size) }
              </tr> ++ NEWLINE
            }}
          </tbody>
      </table>
        } else {
          <p>
            { lines.map(line => Text(line.cells(1))).reduceLeft[NodeSeq](_ ++ <br/> ++ NEWLINE ++ _) }
          </p>
        }
      }
    </div>
  }
}