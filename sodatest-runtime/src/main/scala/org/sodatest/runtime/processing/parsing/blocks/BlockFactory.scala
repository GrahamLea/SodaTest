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
    def createBlock(source: BlockSource): FixtureBlock = {
      // TODO: deal with fail cases: multiple lines, no td #1, more than #1 td
      new FixtureBlock(source, source.lines(0).cells(1))
    }
  }

  private object NoteFactory extends Factory {
    // TODO: Parsing errors: text in cell(0); empty note
    def createBlock(source: BlockSource): NoteBlock = new NoteBlock(source)
  }

  private object EventFactory extends Factory {
    def createBlock(source: BlockSource): EventBlock = {
      // TODO: deal with non-simple cases, and error cases, e.g. un-named parameter, parameter name gap, !! in wrong places
      val inline = source.lines.length == 1
      val (parameters, executions) = source.lines match {
        case eventLine :: parameterLine :: tail => (parameterLine.cells.tail, tail.map(line => new EventExecution(Some(line))))
        case _ => (List.empty, List(new EventExecution(None)))
      }
      // TODO: Check for parameters on inline execution
      // TODO: Check for no parameters on non-inline execution
      // TODO: Check for parameters without arguments
      // TODO: Check for arguments extending past parameter list
      // TODO: Check for noise in column A on parameter and argument lines
      // TODO: Check for empty parameter names in middle of list
      new EventBlock(source, source.lines(0).cells(1), inline, parameters, executions)
    }
  }

  private object ReportFactory extends Factory {
    def createBlock(source: BlockSource): ReportBlock = {
      // TODO: deal with non-simple cases, and error cases, e.g. un-named parameter, parameter name gap, !! in wrong places
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

    private def executionsFrom(lines: List[Line]): List[ReportExecution] = {
      type ExecutionAndResults = (Line, List[Line])

      lines.foldLeft(List[ExecutionAndResults]())((listOfExecutionsAndResults, nextLine) => {
        nextLine.cells match {
          case "!!" :: parameterList => (nextLine, Nil) :: listOfExecutionsAndResults
          case "" :: reportResultLine => listOfExecutionsAndResults match {
            case (lastExecutionLine, resultsLines) :: earlierExecutions => (lastExecutionLine, resultsLines :+ nextLine) :: earlierExecutions
            case _ => throw new RuntimeException("TODO: Empty line before parameter values list")
          }
          case _ => throw new RuntimeException("TODO: Handle non-!! or empty line under report")
        }
      }).reverse.map(p => {new ReportExecution(Some(p._1), p._2)})
    }
  }
}