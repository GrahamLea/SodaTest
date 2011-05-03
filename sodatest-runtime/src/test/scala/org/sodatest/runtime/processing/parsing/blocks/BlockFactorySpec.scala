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

package org.sodatest.runtime
package processing.parsing.blocks

import org.specs.SpecificationWithJUnit
import data.blocks._

class BlockFactorySpec extends SpecificationWithJUnit {

  implicit val log = NoOpLog
  val blockFactory = new BlockFactory

  "BlockFactory" should {

    "create blocks" in {
      val fixtureBlockSource = BlockSource(List(
        Line(1, List("Fixture", "TestFixtureName"))
      ))

      val noteBlockSource = BlockSource(List(
        Line(3, List("Note", "Some Text")),
        Line(4, List("", "Some more text"))
      ))

      val junkBlockSource = BlockSource(List(
        Line(6, List("Junk", "What's this?")),
        Line(7, List("", "Who knows - still a blocks!"))
      ))

      val basicEventBlockSource = BlockSource(List(
        Line(9, List("Event", "Basic Event"))
      ))

      val basicReportBlockSource = BlockSource(List(
        Line(11, List("Report", "Basic Report", "!!")),
        Line(12, List("", "Text"))
      ))

      val parameterisedEventsBlockSource = BlockSource(List(
        Line(14, List("Event", "Parameterised Events")),
        Line(15, List("", "Parameter 1", "Parameter 2")),
        Line(16, List("", "argument one", "argument two")),
        Line(17, List("", "argument one2", "argument two2"))
      ))

      val parameterisedInlineReportBlockSource = BlockSource(List(
        Line(19, List("Report", "Parameterised Inline Report")),
        Line(20, List("", "Parameter 1", "Parameter 2", "!!")),
        Line(21, List("", "argument one", "argument two", "some result")),
        Line(22, List("", "argument one2", "argument two2", "some other result"))
      ))

      val parameterisedReportsBlockSource = BlockSource(List(
        Line(24, List("Report", "Parameterised Report")),
        Line(25, List("", "Parameter 1", "Parameter 2")),
        Line(26, List("!!", "argument one", "argument two")),
        Line(27, List("", "some result")),
        Line(28, List("!!", "argument one2", "argument two2")),
        Line(29, List("", "some other result", "with a second column")),
        Line(30, List("", "and a second row"))
      ))

      val parameterisedEventWithBlankValueBlockSource = BlockSource(List(
        Line(32, List("Event", "Parameterised Event With Blank Value")),
        Line(33, List("", "Parameter 1", "Parameter 2")),
        Line(34, List("", "argument one", ""))
      ))

      val blocks = blockFactory.create(List(
          fixtureBlockSource,
           noteBlockSource,
//           junkBlockSource,
          basicEventBlockSource,
          basicReportBlockSource,
          parameterisedEventsBlockSource,
//           parameterisedInlineReportBlockSource,
          parameterisedReportsBlockSource,
          parameterisedEventWithBlankValueBlockSource
      ))

      var blockIndex = 0;
      {
        val fixtureBlock = blocks(blockIndex).asInstanceOf[FixtureBlock]
        fixtureBlock.source must_== fixtureBlockSource
        fixtureBlock.fixtureName must_== "TestFixtureName"
      }
      blockIndex += 1

      {
        val noteBlock = blocks(blockIndex).asInstanceOf[NoteBlock]
        noteBlock.source must_== noteBlockSource
      }
      blockIndex += 1

//      {
//        val recordParseError = blocks(blockIndex++).asInstanceOf[RecordParseError]
//        recordParseError.lineNumber must_== 6
//        recordParseError.source must_== junkBlock
//        recordParseError.message must_== "The instruction 'Junk' in td A6 is not valid."
//        recordParseError.cause must_== None
//      }
//      blockIndex += 1
//
      
      {
        val basicEventBlock = blocks(blockIndex).asInstanceOf[EventBlock]
        basicEventBlock.source must_== basicEventBlockSource
        basicEventBlock.eventName must_== "Basic Event"
        basicEventBlock.inline must_== true
        basicEventBlock.parameterNames must_== List.empty
        basicEventBlock.executions must beLike {
          case List(e) => e.parameterValues == None
        }
      }
      blockIndex += 1

      {
        val basicReportBlock = blocks(blockIndex).asInstanceOf[ReportBlock]
        basicReportBlock.source must_== basicReportBlockSource
        basicReportBlock.reportName must_== "Basic Report"
        basicReportBlock.inline must_== true
        basicReportBlock.executions must beLike {
          case List(r) => r.parameterValues == None && r.expectedResult == List(Line(12, List("", "Text")))
        }
      }
      blockIndex += 1

      {
        val parameterisedEventsBlock = blocks(blockIndex).asInstanceOf[EventBlock]
        parameterisedEventsBlock.source must_== parameterisedEventsBlockSource
        parameterisedEventsBlock.eventName must_== "Parameterised Events"
        parameterisedEventsBlock.inline must_== false
        parameterisedEventsBlock.parameterNames must_== List("Parameter 1", "Parameter 2")
        parameterisedEventsBlock.executions must beLike {
          case List(e1, e2) => {
            e1.parameterValues == Some(Line(16, List("", "argument one", "argument two"))) &&
            e2.parameterValues == Some(Line(17, List("", "argument one2", "argument two2")))
          }
        }
      }
      blockIndex += 1

      {
        val parameterisedReportsBlock = blocks(blockIndex).asInstanceOf[ReportBlock]
        parameterisedReportsBlock.source must_== parameterisedReportsBlockSource
        parameterisedReportsBlock.reportName must_== "Parameterised Report"
        parameterisedReportsBlock.inline must_== false
        parameterisedReportsBlock.parameterNames must_== List("Parameter 1", "Parameter 2")
        parameterisedReportsBlock.executions must beLike {
          case List(e1, e2) => {
            e1.parameterValues == Some(Line(26, List("!!", "argument one", "argument two"))) &&
            e1.expectedResult == List(Line(27, List("", "some result"))) &&
            e2.parameterValues == Some(Line(28, List("!!", "argument one2", "argument two2"))) &&
            e2.expectedResult == List(Line(29, List("", "some other result", "with a second column")), Line(30, List("", "and a second row")))
          }
        }
      }
      blockIndex += 1

      {
        val parameterisedEventWithBlankValueBlock = blocks(blockIndex).asInstanceOf[EventBlock]
        parameterisedEventWithBlankValueBlock.source must_== parameterisedEventWithBlankValueBlockSource
        parameterisedEventWithBlankValueBlock.eventName must_== "Parameterised Event With Blank Value"
        parameterisedEventWithBlankValueBlock.inline must_== false
        parameterisedEventWithBlankValueBlock.parameterNames must_== List("Parameter 1", "Parameter 2")
        parameterisedEventWithBlankValueBlock.executions must beLike {
          case List(e1) => {
            e1.parameterValues == Some(Line(34, List("", "argument one", "")))
          }
        }
      }
      blockIndex += 1

    }

    // TODO: Extra cases:
    // Event with no parameters and no !!
    // Event with parameters but no values
    // Report with no parameters and no !!
    // Report with parameters but no values
    // Report with no output okay?

  }

}