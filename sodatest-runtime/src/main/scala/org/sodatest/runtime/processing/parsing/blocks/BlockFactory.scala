// Copyright (c) 2010 Belmont Technology Pty Ltd. All rights reserved.

package org.sodatest.runtime.processing.parsing.blocks

import org.sodatest.api.SodaTestLog
import org.sodatest.runtime.data.blocks.{Line, EventExecution, ReportExecution, ReportBlock, EventBlock, FixtureBlock, Block, BlockSource}

class BlockFactory(implicit val log: SodaTestLog) {

  private val factories = Map(
    "Fixture" -> FixtureFactory,
    "Event" -> EventFactory,
    "Report" -> ReportFactory
  )

  def create(blocks: List[BlockSource]): List[Block] = {
    log.info("Creating blocks...")
    blocks.map(createBlock)
  }

  private def createBlock(source: BlockSource): Block = {
    factories.get(source.lines(0).cells(0)) match {
      case Some(factory) => factory.createBlock(source)
      case None => throw new IllegalStateException("Unknown block type: '" + source.lines(0).cells(0) + "'") // TODO: Commute to output
    }
  }

  private trait Factory { def createBlock(source: BlockSource): Block }

  private object FixtureFactory extends Factory {
    def createBlock(source: BlockSource): FixtureBlock = {
      // TODO: deal with fail cases: multiple lines, no td #1, more than #1 td
      new FixtureBlock(source, source.lines(0).cells(1))
    }
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
      val initialList: List[(Line, List[Line]) ] = Nil

      lines.foldLeft(initialList)((executionAccList, line) => {
        line.cells match {
          case "!!" :: parameterList => (line, Nil) :: executionAccList
          case "" :: reportResultLine => executionAccList match {
            case head :: tail => (head._1, head._2 :+ line) :: tail
            case _ => throw new RuntimeException("TODO: Empty line before parameter values list")
          }
          case _ => throw new RuntimeException("TODO: Handle non-!! or empty line under report")
        }
      }).reverse.map(p => {new ReportExecution(Some(p._1), p._2)})
    }
  }
}