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

package org.sodatest.runtime
package processing.parsing.blocks

import org.specs.SpecificationWithJUnit
import data.blocks._

class BlockFactoryErrorsSpec extends SpecificationWithJUnit {

  implicit val log = NoOpLog
  val blockFactory = BlockFactory

  "BlockFactory" should {

    "create parse error blocks" in {
      val unknownBlockTypeBlockSource = BlockSource(List(
        Line(1, List("Junk", "Is not recognised")),
        Line(2, List("", "Should collect")),
        Line(3, List("", "all the lines"))
      ))

      val emptyBlockTypeBlockSource = BlockSource(List(
        Line(5, List("", "No type at all")),
        Line(6, List("", "A bit silly"))
      ))

      val noEventNameBlockSource = BlockSource(List(
        Line(8, List("Event", "")),
        Line(9, List("", "Some Parameter")),
        Line(10, List("", "Some Value"))
      ))

      val noReportNameBlockSource = BlockSource(List(
        Line(11, List("Report", "")),
        Line(12, List("", "Some Parameter")),
        Line(13, List("", "Some Value"))
      ))

      val eventWithExtraCellsOnFirstLineBlockSource = BlockSource(List(
        Line(15, List("Event", "Event Name", "Extra Cell")),
        Line(16, List("", "Some Parameter")),
        Line(17, List("", "Some Value"))
      ))

      val eventWithInlineReportInvokerBlockSource = BlockSource(List(
        Line(19, List("Event", "Event Name", "!!"))
      ))

      val eventWithParametersButNoValuesBlockSource = BlockSource(List(
        Line(21, List("Event", "Some Event")),
        Line(22, List("", "Some Parameter", "Some Other Parameter"))
      ))

      val eventWithEmptyParameterNameBlockSource = BlockSource(List(
        Line(24, List("Event", "Some Event")),
        Line(25, List("", "Some Parameter", " ", "Some Other Parameter")),
        Line(26, List("", "One", "Two", "Three"))
      ))

      val eventWithExtraParameterValuesBlockSource = BlockSource(List(
        Line(28, List("Event", "Some Event")),
        Line(29, List("", "Some Parameter", "Some Other Parameter")),
        Line(30, List("", "Value One", "Value Two", "Value Three"))
      ))

      val eventWithTextInFirstColumn = BlockSource(List(
        Line(32, List("Event", "Some Event")),
        Line(33, List("", "Some Parameter", "Some Other Parameter")),
        Line(34, List("Report", "Value One", "Value Two"))
      ))

      val reportWithExtraCellsOnFirstLineBlockSource = BlockSource(List(
        Line(36, List("Report", "Report Name", "Extra Cell")),
        Line(37, List("!!", "Some Parameter")),
        Line(38, List("", "Some Value"))
      ))

      val reportWithParametersButNoValuesBlockSource = BlockSource(List(
        Line(40, List("Report", "Some Report")),
        Line(41, List("", "Some Parameter", "Some Other Parameter"))
      ))

      val reportWithEmptyParameterNameBlockSource = BlockSource(List(
        Line(42, List("Report", "Some Report")),
        Line(43, List("", "Some Parameter", " ", "Some Other Parameter")),
        Line(44, List("!!", "One", "Two", "Three"))
      ))

      val reportWithExtraParameterValuesBlockSource = BlockSource(List(
        Line(46, List("Report", "Some Report")),
        Line(47, List("", "Some Parameter", "Some Other Parameter")),
        Line(48, List("!!", "Value One", "Value Two", "Value Three"))
      ))

      val reportWithTextInFirstColumnBlockSource = BlockSource(List(
        Line(50, List("Report", "Some Report")),
        Line(51, List("", "Some Parameter", "Some Other Parameter")),
        Line(52, List("Report", "Value One", "Value Two"))
      ))

      val fixtureWithMoreThanOneLineBlockSource = BlockSource(List(
        Line(54, List("Fixture", "Fixture Name")),
        Line(55, List("", "Some Parameter", "Some Other Parameter"))
      ))

      val fixtureWithTextAfterNameBlockSource = BlockSource(List(
        Line(57, List("Fixture", "Fixture Name", "More Text", "And More"))
      ))

      val fixtureWithBlankNameBlockSource = BlockSource(List(
        Line(59, List("Fixture", ""))
      ))

      val noFixtureNameCellBlockSource = BlockSource(List(
        Line(61, List("Fixture"))
      ))

      val noEventNameCellBlockSource = BlockSource(List(
        Line(63, List("Event"))
      ))

      val noReportNameCellBlockSource = BlockSource(List(
        Line(65, List("Report"))
      ))

      val noteWithTextInFirstColumnBlockSource = BlockSource(List(
        Line(67, List("Note", "First Line")),
        Line(68, List("", "Second Line", "is okay")),
        Line(69, List("Third line", "has text", "in column A"))
      ))

      val noteWithNoTextBlockSource = BlockSource(List(
        Line(71, List("Note", ""))
      ))

      val noteWithNoTextCellsBlockSource = BlockSource(List(
        Line(73, List("Note"))
      ))

      val reportWithOutputBeforeExecutionBlockSource = BlockSource(List(
        Line(75, List("Report", "Some Report")),
        Line(76, List("", "Parameter One", "Parameter Two", "Parameter Three")),
        Line(77, List("", "Exected Output", "not an execution")),
        Line(78, List("", "More text")),
        Line(79, List("!!", "Execution here", "in column A")),
        Line(80, List("", "And some more output"))
      ))

      val reportWithInlineAndInBlockExecutionBlockSource = BlockSource(List(
        Line(82, List("Report", "Some Report", "!!")),
        Line(83, List("", "Parameter One", "Parameter Two")),
        Line(84, List("!!", "Execution here", "in column A")),
        Line(85, List("", "And some output to match"))
      ))

      val reportWithExecutionOnParameterNamesLineBlockSource = BlockSource(List(
        Line(87, List("Report", "Some Report")),
        Line(88, List("!!", "Parameter One", "Parameter Two")),
        Line(89, List("", "Execution here", "in column A")),
        Line(90, List("", "And some output to match"))
      ))

      // TODO: Extra cells after report invoker in inline report

      val blocks = blockFactory.create(List(
        unknownBlockTypeBlockSource,
        emptyBlockTypeBlockSource,
        noEventNameBlockSource,
        noReportNameBlockSource,
        eventWithExtraCellsOnFirstLineBlockSource,
        eventWithInlineReportInvokerBlockSource,
        eventWithParametersButNoValuesBlockSource,
        eventWithEmptyParameterNameBlockSource,
        eventWithExtraParameterValuesBlockSource,
        eventWithTextInFirstColumn,
        reportWithExtraCellsOnFirstLineBlockSource,
        reportWithParametersButNoValuesBlockSource,
        reportWithEmptyParameterNameBlockSource,
        reportWithExtraParameterValuesBlockSource,
        reportWithTextInFirstColumnBlockSource,
        fixtureWithMoreThanOneLineBlockSource,
        fixtureWithTextAfterNameBlockSource,
        fixtureWithBlankNameBlockSource,
        noFixtureNameCellBlockSource,
        noEventNameCellBlockSource,
        noReportNameCellBlockSource,
        noteWithTextInFirstColumnBlockSource,
        noteWithNoTextBlockSource,
        noteWithNoTextCellsBlockSource,
        reportWithOutputBeforeExecutionBlockSource,
        reportWithInlineAndInBlockExecutionBlockSource,
        reportWithExecutionOnParameterNamesLineBlockSource
      ))

      var blockIndex = 0;
      {
        val parseErrorBlock = blocks(blockIndex).asInstanceOf[ParseErrorBlock]
        parseErrorBlock.source must_== unknownBlockTypeBlockSource
        parseErrorBlock.name must_== "Is not recognised"
        parseErrorBlock.error must_== "Unknown Block type: 'Junk'"
        parseErrorBlock.errorSource must_== (0, 0)
      }
      blockIndex += 1

      {
        val parseErrorBlock = blocks(blockIndex).asInstanceOf[ParseErrorBlock]
        parseErrorBlock.source must_== emptyBlockTypeBlockSource
        parseErrorBlock.name must_== "No type at all"
        parseErrorBlock.error must_== "No Block type specified"
        parseErrorBlock.errorSource must_== (0, 0)
      }
      blockIndex += 1

      {
        val parseErrorBlock = blocks(blockIndex).asInstanceOf[ParseErrorBlock]
        parseErrorBlock.source must_== noEventNameBlockSource
        parseErrorBlock.name must_== ""
        parseErrorBlock.error must_== "No Event name specified"
        parseErrorBlock.errorSource must_== (0, 1)
      }
      blockIndex += 1

      {
        val parseErrorBlock = blocks(blockIndex).asInstanceOf[ParseErrorBlock]
        parseErrorBlock.source must_== noReportNameBlockSource
        parseErrorBlock.name must_== ""
        parseErrorBlock.error must_== "No Report name specified"
        parseErrorBlock.errorSource must_== (0, 1)
      }
      blockIndex += 1

      {
        val parseErrorBlock = blocks(blockIndex).asInstanceOf[ParseErrorBlock]
        parseErrorBlock.source must_== eventWithExtraCellsOnFirstLineBlockSource
        parseErrorBlock.name must_== "Event Name"
        parseErrorBlock.error must_== "Extra cells after Event name"
        parseErrorBlock.errorSource must_== (0, 2)
      }
      blockIndex += 1

      {
        val parseErrorBlock = blocks(blockIndex).asInstanceOf[ParseErrorBlock]
        parseErrorBlock.source must_== eventWithInlineReportInvokerBlockSource
        parseErrorBlock.name must_== "Event Name"
        parseErrorBlock.error must_== "Events do not use the '!!' invoker"
        parseErrorBlock.errorSource must_== (0, 2)
      }
       blockIndex += 1

      {
        val parseErrorBlock = blocks(blockIndex).asInstanceOf[ParseErrorBlock]
        parseErrorBlock.source must_== eventWithParametersButNoValuesBlockSource
        parseErrorBlock.name must_== "Some Event"
        parseErrorBlock.error must_== "Event has parameters names but no values"
        parseErrorBlock.errorSource must_== (1, 1)
      }
      blockIndex += 1

      {
        val parseErrorBlock = blocks(blockIndex).asInstanceOf[ParseErrorBlock]
        parseErrorBlock.source must_== eventWithEmptyParameterNameBlockSource
        parseErrorBlock.name must_== "Some Event"
        parseErrorBlock.error must_== "Parameter Names cannot be blank space"
        parseErrorBlock.errorSource must_== (1, 2)
      }
      blockIndex += 1

      {
        val parseErrorBlock = blocks(blockIndex).asInstanceOf[ParseErrorBlock]
        parseErrorBlock.source must_== eventWithExtraParameterValuesBlockSource
        parseErrorBlock.name must_== "Some Event"
        parseErrorBlock.error must_== "Parameter Value specified without a Parameter Name"
        parseErrorBlock.errorSource must_== (2, 3)
      }
      blockIndex += 1

      {
        val parseErrorBlock = blocks(blockIndex).asInstanceOf[ParseErrorBlock]
        parseErrorBlock.source must_== eventWithTextInFirstColumn
        parseErrorBlock.name must_== "Some Event"
        parseErrorBlock.error must_== "The first column of an Event block should always be blank after the first line"
        parseErrorBlock.errorSource must_== (2, 0)
      }
      blockIndex += 1

      {
        val parseErrorBlock = blocks(blockIndex).asInstanceOf[ParseErrorBlock]
        parseErrorBlock.source must_== reportWithExtraCellsOnFirstLineBlockSource
        parseErrorBlock.name must_== "Report Name"
        parseErrorBlock.error must_== "Extra cells after Report name"
        parseErrorBlock.errorSource must_== (0, 2)
      }
       blockIndex += 1

      {
        val parseErrorBlock = blocks(blockIndex).asInstanceOf[ParseErrorBlock]
        parseErrorBlock.source must_== reportWithParametersButNoValuesBlockSource
        parseErrorBlock.name must_== "Some Report"
        parseErrorBlock.error must_== "Report has parameters names but no values"
        parseErrorBlock.errorSource must_== (1, 1)
      }
      blockIndex += 1

      {
        val parseErrorBlock = blocks(blockIndex).asInstanceOf[ParseErrorBlock]
        parseErrorBlock.source must_== reportWithEmptyParameterNameBlockSource
        parseErrorBlock.name must_== "Some Report"
        parseErrorBlock.error must_== "Parameter Names cannot be blank space"
        parseErrorBlock.errorSource must_== (1, 2)
      }
      blockIndex += 1

      {
        val parseErrorBlock = blocks(blockIndex).asInstanceOf[ParseErrorBlock]
        parseErrorBlock.source must_== reportWithExtraParameterValuesBlockSource
        parseErrorBlock.name must_== "Some Report"
        parseErrorBlock.error must_== "Parameter Value specified without a Parameter Name"
        parseErrorBlock.errorSource must_== (2, 3)
      }
      blockIndex += 1

      {
        val parseErrorBlock = blocks(blockIndex).asInstanceOf[ParseErrorBlock]
        parseErrorBlock.source must_== reportWithTextInFirstColumnBlockSource
        parseErrorBlock.name must_== "Some Report"
        parseErrorBlock.error must_== "The first column of a Report block should either contain the Report Invoker (!!) or be empty"
        parseErrorBlock.errorSource must_== (2, 0)
      }
      blockIndex += 1

      {
        val parseErrorBlock = blocks(blockIndex).asInstanceOf[ParseErrorBlock]
        parseErrorBlock.source must_== fixtureWithMoreThanOneLineBlockSource
        parseErrorBlock.name must_== "Fixture Name"
        parseErrorBlock.error must_== "Fixture blocks only have a single line"
        parseErrorBlock.errorSource must_== (1, 0)
      }
      blockIndex += 1

      {
        val parseErrorBlock = blocks(blockIndex).asInstanceOf[ParseErrorBlock]
        parseErrorBlock.source must_== fixtureWithTextAfterNameBlockSource
        parseErrorBlock.name must_== "Fixture Name"
        parseErrorBlock.error must_== "Extra cells after Fixture name"
        parseErrorBlock.errorSource must_== (0, 2)
      }
      blockIndex += 1

      {
        val parseErrorBlock = blocks(blockIndex).asInstanceOf[ParseErrorBlock]
        parseErrorBlock.source must_== fixtureWithBlankNameBlockSource
        parseErrorBlock.name must_== ""
        parseErrorBlock.error must_== "No Fixture name specified"
        parseErrorBlock.errorSource must_== (0, 1)
      }
      blockIndex += 1

      {
        val parseErrorBlock = blocks(blockIndex).asInstanceOf[ParseErrorBlock]
        parseErrorBlock.source must_== noFixtureNameCellBlockSource
        parseErrorBlock.name must_== ""
        parseErrorBlock.error must_== "No Fixture name specified"
        parseErrorBlock.errorSource must_== (0, 0)
      }
      blockIndex += 1

      {
        val parseErrorBlock = blocks(blockIndex).asInstanceOf[ParseErrorBlock]
        parseErrorBlock.source must_== noEventNameCellBlockSource
        parseErrorBlock.name must_== ""
        parseErrorBlock.error must_== "No Event name specified"
        parseErrorBlock.errorSource must_== (0, 0)
      }
      blockIndex += 1

      {
        val parseErrorBlock = blocks(blockIndex).asInstanceOf[ParseErrorBlock]
        parseErrorBlock.source must_== noReportNameCellBlockSource
        parseErrorBlock.name must_== ""
        parseErrorBlock.error must_== "No Report name specified"
        parseErrorBlock.errorSource must_== (0, 0)
      }
      blockIndex += 1

      {
        val parseErrorBlock = blocks(blockIndex).asInstanceOf[ParseErrorBlock]
        parseErrorBlock.source must_== noteWithTextInFirstColumnBlockSource
        parseErrorBlock.name must_== "First Line"
        parseErrorBlock.error must_== "The first column of a Note block should always be blank after the first line"
        parseErrorBlock.errorSource must_== (2, 0)
      }
      blockIndex += 1

      {
        val parseErrorBlock = blocks(blockIndex).asInstanceOf[ParseErrorBlock]
        parseErrorBlock.source must_== noteWithNoTextBlockSource
        parseErrorBlock.name must_== ""
        parseErrorBlock.error must_== "No Note text specified"
        parseErrorBlock.errorSource must_== (0, 0)
      }
      blockIndex += 1

      {
        val parseErrorBlock = blocks(blockIndex).asInstanceOf[ParseErrorBlock]
        parseErrorBlock.source must_== noteWithNoTextCellsBlockSource
        parseErrorBlock.name must_== ""
        parseErrorBlock.error must_== "No Note text specified"
        parseErrorBlock.errorSource must_== (0, 0)
      }
      blockIndex += 1

      {
        val parseErrorBlock = blocks(blockIndex).asInstanceOf[ParseErrorBlock]
        parseErrorBlock.source must_== reportWithOutputBeforeExecutionBlockSource
        parseErrorBlock.name must_== "Some Report"
        parseErrorBlock.error must_== "Report Parameter names must be followed by an execution (!!)"
        parseErrorBlock.errorSource must_== (2, 0)
      }
      blockIndex += 1

      {
        val parseErrorBlock = blocks(blockIndex).asInstanceOf[ParseErrorBlock]
        parseErrorBlock.source must_== reportWithInlineAndInBlockExecutionBlockSource
        parseErrorBlock.name must_== "Some Report"
        parseErrorBlock.error must_== "Reports cannot have an inline execution and block executions"
        parseErrorBlock.errorSource must_== (2, 0)
      }
      blockIndex += 1

      {
        val parseErrorBlock = blocks(blockIndex).asInstanceOf[ParseErrorBlock]
        parseErrorBlock.source must_== reportWithExecutionOnParameterNamesLineBlockSource
        parseErrorBlock.name must_== "Some Report"
        parseErrorBlock.error must_== "The second line of a Report must be a Parameter name list, not an execution"
        parseErrorBlock.errorSource must_== (1, 0)
      }
    }


    // TODO: Extra cases:
    // Report with no parameters and no !!
    // Report with parameters but no values
    // Report with no output okay?

  }

}