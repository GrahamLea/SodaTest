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

package org.sodatest.runtime.processing.parsing.blocks

import org.sodatest.runtime.data.blocks.{ParseErrorBlock, NoteBlock, Block, BlockSource}

private[blocks] object NoteBlockFactory extends BlockFactory {

  import BlockFactory._

  def createBlock(source: BlockSource): Block = {
    source match {
      case ValidNoteBlock() => new NoteBlock(source)
      case ParseError(errorBlock) => errorBlock
      case _ => new ParseErrorBlock(source, "Uncategorised Parse Error. Please report this as a bug.", (0, 0))
    }
  }

  private object ValidNoteBlock {
    def unapply(source: BlockSource): Boolean =
      source.lines.tail.filter(_.cells(0).trim != "").isEmpty &&
        !source.lines.flatMap(_.cells.tail).filterNot(_.trim.isEmpty).isEmpty
  }


  private object ParseError {
    def unapply(implicit source: BlockSource): Option[ParseErrorBlock] = {
      source match {
        case TextInFirstColumn(firstLine) => parseError("The first column of a Note block should always be blank after the first line", (firstLine, 0))
        case NoText() => parseError("No Note text specified", (0, 0))
        case _ => None
      }
    }

    private object NoText {
      def unapply(source: BlockSource): Boolean = {
        // If the list of all non-empty cells is empty, that's an error (=> true)
        source.lines.map(_.cells.tail).flatMap(_.filter(!_.isEmpty)).isEmpty
      }
    }
  }
}

