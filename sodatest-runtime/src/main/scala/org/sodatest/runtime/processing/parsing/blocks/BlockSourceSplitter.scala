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

package org.sodatest.runtime.processing.parsing.blocks

import org.sodatest.runtime.data.blocks.{Line, BlockSource}
import org.sodatest.api.SodaTestLog

class BlockSourceSplitter(implicit val log: SodaTestLog) {

  // These aren't used here, but I'll probably need them for the formatter
  private val letters = ('A' to 'Z' toList).map(_.toString)
  private val columnNames = for (val letter1 <- "" :: letters; val letter2 <- letters) yield letter1 + letter2

  def parseBlocks(table: List[List[String]]): List[BlockSource] = {
    log.info("Separating block sources...")
    (table ::: List(Nil)) // Append a Nil line so that foldLeft will create the last BlockSource
      .map(_.map(_.trim)) // Trim the content of every td
      .map(trimTrailingEmptyCells) // Trim empty cells from the ends of lines (also converts empty/whitespace lines to Nil)
      .zip(1 to Integer.MAX_VALUE) // Pair each line with a line number
      .map(p => Line(p._2, p._1)) // Map (List[String], Int) => Line
      .foldLeft ((List[Line](), List[BlockSource]())) ((linesAndBlocks, line) => {
        line.cells match {
          case Nil => linesAndBlocks._1 match {
            case Nil => (Nil, linesAndBlocks._2)
            case _ =>   (Nil, BlockSource(linesAndBlocks._1.reverse) :: linesAndBlocks._2)
          }
          case _ => (line :: linesAndBlocks._1, linesAndBlocks._2)
        }
      })
      ._2.reverse
  }

  @inline
  private def trimTrailingEmptyCells(cells: List[String]) : List[String] = {
    cells.reverse.foldLeft(List[String]()) ((trimmedLine, cell) => {
      if (trimmedLine != Nil || cell != "") {
        cell :: trimmedLine
      } else {
        trimmedLine
      }
    })

  }
}