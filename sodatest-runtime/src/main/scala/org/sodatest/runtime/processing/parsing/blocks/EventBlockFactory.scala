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

import org.sodatest.runtime.data.blocks._
import java.lang.Short

private[blocks] object EventBlockFactory extends BlockFactory {

  import BlockFactory._

  def createBlock(source: BlockSource): Block = {
    source match {
      case ValidEventBlock() => {
        val inline = source.lines.length == 1
        val (parameters, executions) = source.lines match {
          case eventLine :: parameterLine :: tail => (parameterLine.cells.tail, tail.map(line => new EventExecution(Some(line))))
          case _ => (List.empty, List(new EventExecution(None)))
        }
        new EventBlock(source, source.lines(0).cells(1), inline, parameters, executions)
      }
      case ParseError(errorBlock) => errorBlock
      case _ => new ParseErrorBlock(source, "Uncategorised Parse Error. Please report this as a bug.", (0, 0))
    }
  }

  private object ValidEventBlock {
    def unapply(implicit source: BlockSource): Boolean = {
      hasValidFirstLine(source) &&
        (source.lines.size match {
          case 1 => true
          case 2 => false
          case _ => hasValidParameterNames(source.lines(1)) &&
                      hasValidExecutions(source.lines.tail.tail, maxLength = source.lines(1).cells.size)
        })
    }

    private def hasValidFirstLine(source: BlockSource): Boolean = {
      source.lines(0).cells.size == 2 && source.lines(0).cells(1).trim != ""
    }

    private def hasValidParameterNames(parameterNamesLine: Line): Boolean = {
      parameterNamesLine.cells(0) == "" &&
        parameterNamesLine.cells.tail.filter(_.trim == "").isEmpty
    }

    private def hasValidExecutions(executionLines: List[Line], maxLength: Int): Boolean = {
      executionLines.filterNot(line => {line.cells(0) == "" && line.cells.size <= maxLength}).isEmpty
    }
  }

  private object ParseError {
    def unapply(implicit source: BlockSource): Option[ParseErrorBlock] = {
      source match {
        case NoName(errorCell) => parseError("No Event name specified", (0, errorCell))
        case ReportInvokerPresent() => parseError("Events do not use the '!!' invoker", (0, 2))
        case ExtraCellsAfterName() => parseError("Extra cells after Event name", (0, 2))
        case TextInFirstColumn(firstLine) => parseError("The first column of an Event block should always be blank after the first line", (firstLine, 0))
        case ParamtersNamesButNoValues() => parseError("Event has parameters names but no values", (1, 1))
        case BlankParameterName(firstCell) => parseError("Parameter Names cannot be blank space", (1, firstCell))
        case MoreParameterValuesThanNames(firstLine, firstCell) => parseError("Parameter Value specified without a Parameter Name", (firstLine, firstCell))
        case _ => None
      }
    }
  }

  private object ReportInvokerPresent {
    def unapply(source: BlockSource): Boolean = source.lines(0).cells.size > 2 && source.lines(0).cells(2).trim == "!!"
  }

  private object MoreParameterValuesThanNames {
    def unapply(source: BlockSource): Option[(Int, Int)] = {
      if (source.lines.size > 1) {
        val parameterNameLineWidth = source.lines(1).cells.size
        source.lines.tail.tail
          .zip(2 to Short.MAX_VALUE)
          .filter(_._1.cells.size > parameterNameLineWidth)
          .headOption
          .map(p => (p._2, parameterNameLineWidth))
      } else {
        None
      }
    }
  }

}

