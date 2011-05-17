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

import org.sodatest.runtime.data.blocks.{ParseErrorBlock, FixtureBlock, Block, BlockSource}

private[blocks] object FixtureBlockFactory extends BlockFactory {

  import BlockFactory._

  def createBlock(source: BlockSource): Block = {
    source match {
      case ValidFixtureBlock() => new FixtureBlock(source, source.lines(0).cells(1))
      case ParseError(errorBlock) => errorBlock
      case _ => new ParseErrorBlock(source, "Uncategorised Parse Error. Please report this as a bug.", (0, 0))
    }
  }

  private object ValidFixtureBlock {
    def unapply(source: BlockSource): Boolean =
      source.lines.size == 1 &&
        source.lines(0).cells.size == 2 &&
        source.lines(0).cells(1).trim != ""
  }

  private object ParseError {
    def unapply(implicit source: BlockSource): Option[ParseErrorBlock] = {
      source match {
        case NoName(errorCell) => parseError("No Fixture name specified", (0, errorCell))
        case MoreThanOneLine() => parseError("Fixture blocks only have a single line", (1, 0))
        case ExtraCellsAfterName() => parseError( "Extra cells after Fixture name", (0, 2))
        case _ => None
      }
    }

    private object MoreThanOneLine {
      def unapply(source: BlockSource):Boolean = source.lines.size > 1
    }
  }
}

