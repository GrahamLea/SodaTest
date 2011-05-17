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

class BlockFactory(implicit val log: SodaTestLog) {

  private val factories = Map(
    "Event" -> EventFactory,
    "Report" -> ReportFactory,
    "Note" -> NoteFactory,
    "Fixture" -> FixtureFactory
  )

  def create(blocks: List[BlockSource]): List[Block] = {
    log.info("Creating blocks...")
    blocks.map(createBlock)
  }

  private def createBlock(source: BlockSource): Block = {
    source.lines(0).cells(0).trim match {
      case "" => new ParseErrorBlock(source, "No Block type specified", (0, 0))
      case blockType =>
        factories.get(blockType) match {
          case Some(factory) => factory.createBlock(source)
          case None => new ParseErrorBlock(source, "Unknown Block type: '" + blockType + "'", (0, 0))
        }
    }
  }

  private trait Factory { def createBlock(source: BlockSource): Block }

  private object FixtureFactory extends Factory {
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

  private object NoteFactory extends Factory {
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

  private object EventFactory extends Factory {
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
        source.lines(0).cells.size == 2 && !source.lines(0).cells(1).trim.isEmpty
      }

      private def hasValidParameterNames(parameterNamesLine: Line): Boolean = {
        parameterNamesLine.cells(0) == "" &&
          parameterNamesLine.cells.tail.filter(_.trim.isEmpty).isEmpty
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

  private object ReportFactory extends Factory {
    def createBlock(source: BlockSource): Block = {
      source match {
        case ParseError(errorBlock) => errorBlock
      // TODO: deal with non-simple cases, and error cases, e.g. un-named parameter, parameter name gap, !! in wrong places
        case _ => {
          val inline = source.lines(0).cells match {
            case List(_, _, "!!") => true
            case List(_, _) => false
            case _ => throw new RuntimeException("TODO: This should be a parse error. Something other than !! after Report name")
          }
          val (parameterList, executions) =
            if (inline)
              (List(), List(new ReportExecution(None, source.lines.tail)))
            else
              source.lines match {
                // TODO: Handle parameter list that has no cells or starts with something other than blank
                case reportLine :: parameterListCells :: tail => (parameterListCells.cells.tail, executionsFrom(tail))
                case _ => (Nil, Nil)
              }
          new ReportBlock(source, source.lines(0).cells(1), inline, parameterList, executions)
        }
      }
    }


    private object ValidReportBlock {
      def unapply(implicit source: BlockSource): Boolean = {
        (hasValidInlineFirstLine(source) && hasOnlyReportOutputInBody(source.lines.tail)) ||
          (hasValidNonInlineFirstLine(source) &&
            (source.lines.size match {
              case 1 => false
              case 2 => false
              case _ => hasValidParameterNames(source.lines(1)) &&
                          source.lines(2).cells(0) == "!!" &&
                          hasValidExecutions(source.lines.tail.tail, maxLength = source.lines(1).cells.size)
            })
          )
      }

      private def hasValidInlineFirstLine(source: BlockSource): Boolean = {
        source.lines(0).cells.size == 3 &&
          !source.lines(0).cells(1).trim.isEmpty &&
          source.lines(0).cells(2) == ""
      }

      private def hasValidNonInlineFirstLine(source: BlockSource): Boolean = {
        source.lines(0).cells.size == 2 && !source.lines(0).cells(1).trim.isEmpty
      }

      private def hasValidParameterNames(parameterNamesLine: Line): Boolean = {
        parameterNamesLine.cells(0) == "" &&
          parameterNamesLine.cells.tail.filter(_.trim.isEmpty).isEmpty
      }

      private def hasOnlyReportOutputInBody(executionLines: List[Line]): Boolean = {
        executionLines.filterNot(_.cells(0) == "").isEmpty
      }

      private def hasValidExecutions(executionLines: List[Line], maxLength: Int): Boolean = {
        executionLines.filter(line => {line.cells(0) == "!!" && line.cells.size > maxLength}).isEmpty
      }
    }

    private object ParseError {
      def unapply(implicit source: BlockSource): Option[ParseErrorBlock] = {
        source match {
          case NoName(errorCell) => parseError("No Report name specified", (0, errorCell))
//          case ReportInvokerPresent() => parseError("Events do not use the '!!' invoker", (0, 2))
          case ExtraCellsAfterReportName() => parseError( "Extra cells after Report name", (0, 2))
          case TextInFirstColumnOfReport(firstLine) => parseError("The first column of a Report block should either contain the Report Invoker (!!) or be empty", (firstLine, 0))
          case ParamtersNamesButNoValues() => parseError("Report has parameters names but no values", (1, 1))
          case BlankParameterName(firstCell) => parseError("Parameter Names cannot be blank space", (1, firstCell))
          case MoreParameterValuesThanNames(firstLine, firstCell) => parseError("Parameter Value specified without a Parameter Name", (firstLine, firstCell))
          case ReportInvokerOnParameterNamesLine() => parseError("The second line of a Report must be a Parameter name list, not an execution", (1, 0))
          case OutputLineBeforeExecution() => parseError("Report Parameter names must be followed by an execution (!!)", (2, 0))
          case InlineAndInBlockExecutions() => parseError("Reports cannot have an inline execution and block executions", (2, 0))
          case _ => None
        }
      }
    }

    private def executionsFrom(lines: List[Line]): List[ReportExecution] = {
      type ExecutionAndResults = (Line, List[Line])

      lines.foldLeft(List[ExecutionAndResults]())((listOfExecutionsAndResults, nextLine) => {
        nextLine.cells match {
          case "!!" :: parameterList => (nextLine, Nil) :: listOfExecutionsAndResults
          case "" :: reportResultLine => listOfExecutionsAndResults match {
            case (lastExecutionLine, resultsLines) :: earlierExecutions => (lastExecutionLine, resultsLines :+ nextLine) :: earlierExecutions
            case _ => throw new RuntimeException("Should get a ParseError before reaching here")
          }
          case _ => throw new RuntimeException("Should get a ParseError before reaching here")
        }
      }).reverse.map(p => {new ReportExecution(Some(p._1), p._2)})
    }

    private object ExtraCellsAfterReportName {
      def unapply(source: BlockSource): Boolean =
        source.lines(0).cells.size > 2 && source.lines(0).cells(2) != "!!"
    }

    private object ExtraCellsAfterInlineReportInvoker {
      def unapply(source: BlockSource): Boolean =
        source.lines(0).cells.size > 3 && source.lines(0).cells(2) == "!!"
    }

    private object TextInFirstColumnOfReport {
      private val stringsAllowedInFirstColumn = Set("", "!!")

      def unapply(source: BlockSource): Option[Int] = {
        source.lines.tail
          .zip(1 to Short.MAX_VALUE)
          .filter(p => !stringsAllowedInFirstColumn.contains(p._1.cells(0)))
          .headOption
          .map(_._2)
      }
    }

    private object MoreParameterValuesThanNames {
      def unapply(source: BlockSource): Option[(Int, Int)] = {
        if (source.lines.size > 1) {
          val parameterNameLineWidth = source.lines(1).cells.size
          source.lines.tail.tail
            .zip(2 to Short.MAX_VALUE)
            .filter(_._1.cells(0) == "!!")
            .filter(_._1.cells.size > parameterNameLineWidth)
            .headOption
            .map(p => (p._2, parameterNameLineWidth))
        } else {
          None
        }
      }
    }

    private object OutputLineBeforeExecution {
      def unapply(source: BlockSource): Boolean =
        !inlineReportInvokerPresent(source) &&
          (source.lines.map(_.cells(0)) match {
            case instruction :: parameterGap :: "" :: otherFirstCells => true
            case _ => false
          })
    }

    private object InlineAndInBlockExecutions {
      def unapply(source: BlockSource): Boolean =
        inlineReportInvokerPresent(source) && source.lines.size > 2 && source.lines(2).cells(0) == "!!"
    }

    private object ReportInvokerOnParameterNamesLine {
      def unapply(source: BlockSource): Boolean = source.lines.size > 1 && source.lines(1).cells(0) == "!!"
    }

    private def inlineReportInvokerPresent(source: BlockSource): Boolean =
      source.lines(0).cells.size > 2 && source.lines(0).cells(2) == "!!"
  }

  private object ParamtersNamesButNoValues {
    def unapply(source: BlockSource): Boolean =
      source.lines.size == 2 && (source.lines(0).cells.size < 3 || source.lines(0).cells(2) != "!!")
  }

  private object NoName {
    def unapply(source: BlockSource): Option[Int] = {
      source.lines(0).cells match {
        case blockType :: Nil => Some(0)
        case blockType :: "" :: tail => Some(1)
        case _ => None
      }
    }
  }

  private object BlankParameterName {
    def unapply(source: BlockSource): Option[Int] = {
      if (source.lines.size > 1)
        source.lines(1).cells.tail.zip(1 to Short.MAX_VALUE).filter(_._1.trim == "").headOption.map(_._2)
      else
        None
    }
  }

  private object TextInFirstColumn {
    def unapply(source: BlockSource): Option[Int] = {
      source.lines.tail
        .zip(1 to Short.MAX_VALUE)
        .filter(_._1.cells(0).trim != "")
        .headOption
        .map(_._2)
    }
  }

  private object ExtraCellsAfterName {
    def unapply(source: BlockSource): Boolean = source.lines(0).cells.size > 2
  }

  private def parseError(message: String, location: (Int, Int))(implicit source: BlockSource): Some[ParseErrorBlock] =
    Some(new ParseErrorBlock(source, message, location))
}