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

import org.sodatest.api.SodaTestLog
import org.sodatest.runtime.data.blocks._
import java.lang.Short

private[blocks] trait BlockFactory {
  def createBlock(source: BlockSource): Block
}

object BlockFactory {

  def create(blocks: List[BlockSource])(implicit log: SodaTestLog): List[Block] = {
    log.info("Creating blocks...")
    blocks.map(createBlock)
  }

  private def createBlock(source: BlockSource): Block = {
    source.lines(0).cells(0).trim match {
      case "Event" => EventBlockFactory.createBlock(source)
      case "Report" => ReportBlockFactory.createBlock(source)
      case "Note" => NoteBlockFactory.createBlock(source)
      case "Fixture" => FixtureBlockFactory.createBlock(source)
      case "" => new ParseErrorBlock(source, "No Block type specified", (0, 0))
      case blockType => new ParseErrorBlock(source, "Unknown Block type: '" + blockType + "'", (0, 0))
    }
  }

  private[blocks] object ParamtersNamesButNoValues {
    def unapply(source: BlockSource): Boolean =
      source.lines.size == 2 && (source.lines(0).cells.size < 3 || source.lines(0).cells(2) != "!!")
  }

  private[blocks] object NoName {
    def unapply(source: BlockSource): Option[Int] = {
      source.lines(0).cells match {
        case blockType :: Nil => Some(0)
        case blockType :: "" :: tail => Some(1)
        case _ => None
      }
    }
  }

  private[blocks] object BlankParameterName {
    def unapply(source: BlockSource): Option[Int] = {
      if (source.lines.size > 1)
        source.lines(1).cells.tail.zip(1 to Short.MAX_VALUE).filter(_._1.trim == "").headOption.map(_._2)
      else
        None
    }
  }

  private[blocks] object TextInFirstColumn {
    def unapply(source: BlockSource): Option[Int] = {
      source.lines.tail
        .zip(1 to Short.MAX_VALUE)
        .filter(_._1.cells(0).trim != "")
        .headOption
        .map(_._2)
    }
  }

  private[blocks] object ExtraCellsAfterName {
    def unapply(source: BlockSource): Boolean = source.lines(0).cells.size > 2
  }

  private[blocks] def parseError(message: String, location: (Int, Int))(implicit source: BlockSource): Some[ParseErrorBlock] =
    Some(new ParseErrorBlock(source, message, location))
}