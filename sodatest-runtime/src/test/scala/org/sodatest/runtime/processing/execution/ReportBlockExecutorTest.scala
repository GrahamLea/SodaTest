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

package org.sodatest.runtime.processing.execution

import org.sodatest.runtime.processing.SodaTestContext
import org.junit.Test
import org.junit.Assert._
import org.hamcrest.CoreMatchers._
import org.sodatest.runtime.data.blocks._
import org.sodatest.api.reflection._
import java.lang.UnsupportedOperationException
import org.sodatest.api.{SodaReport, SodaFixture}
import org.sodatest.runtime.data.results.{ReportLineMatch, ReportBlockResult}

class ReportBlockExecutorTest {
  @Test
    def shouldTrimEmptyLinesFromTheEndOfOutputLines {
    val sodaTestExecutionContext = new SodaTestExecutionContext(new SodaTestContext)
    sodaTestExecutionContext.currentFixture = Some(new SodaFixture {
      def createReport(name: String) = Some(new SodaReport {
        def apply(parameters: Map[String, String]) = List(List("Text", " ", "More text", " ", " "))
      })
      def createEvent(name: String) = throw new UnsupportedOperationException()
    })

    val source = new BlockSource(List(Line(1, List("Report", "My Report", "!!"))))
    val reportBlock = new ReportBlock(source, "My Report", true, Nil, List(new ReportExecution(None, List(Line(0, List("", "Text", " ", "More text"))))))
    val result: ReportBlockResult = ReportBlockExecutor.execute(reportBlock, sodaTestExecutionContext)
    assertThat(result.block, is(sameInstance(reportBlock)))
    println("result.executionResults = " + result.executionResults)
    assertThat(result.succeeded, is(true))
    assertThat(result.executionResults.head.matchResult.passed, is(true))
    assertThat(result.executionResults.head.matchResult.lineResults.head.asInstanceOf[ReportLineMatch].cells, is(List("", "Text", " ", "More text")))
  }

  @Test
  def shouldReturnAnExecutionErrorForNoFixture {
    val source = new BlockSource(List(Line(1, List("Report", "My Report", "!!"))))
    val reportBlock = new ReportBlock(source, "My Report", true, Nil, List(new ReportExecution(None, Nil)))
    val result: ReportBlockResult = ReportBlockExecutor.execute(reportBlock, new SodaTestExecutionContext(new SodaTestContext))
    assertThat(result.block, is(sameInstance(reportBlock)))
    result.blockError match {
      case None => fail("Expecting Block Error")
      case Some(error) => {
        assertThat(error.message, is("No Fixture has been declared before this Report"))
        assertThat(error.causeString, is(None.asInstanceOf[Option[String]]))
        assertThat(error.cause, is(None.asInstanceOf[Option[Throwable]]))
      }
    }
  }

  @Test
  def shouldReturnABlockErrorForAReflectiveReportReturningAnEvent {
    val source = new BlockSource(List(Line(1, List("Report", "Report Name Returning Event", "!!"))))
    val reportBlock = new ReportBlock(source, "Report Name Returning Event", true, Nil, List(new ReportExecution(None, Nil)))
    val executionContext: SodaTestExecutionContext = new SodaTestExecutionContext(new SodaTestContext)
    executionContext.currentFixture = Some(new FixtureThatCausesErrorsWhenCreatingStuff())
    val result: ReportBlockResult = ReportBlockExecutor.execute(reportBlock, executionContext)
    assertThat(result.block, is(sameInstance(reportBlock)))
    result.blockError match {
      case None => fail("Expecting Block Error")
      case Some(error) => {
        assertThat(error.message, is("The function that matches this name does not return a Report"))
        assertThat(error.causeString, is(Some("org.sodatest.api.reflection.ReflectionTargetReturnsTheWrongTypeException: Function 'reportNameReturningEvent' does not return a SodaReport").asInstanceOf[Option[String]]))
        assertThat(error.cause.get, is(instanceOf(classOf[ReflectionTargetReturnsTheWrongTypeException])))
      }
    }
  }

  @Test
  def shouldReturnABlockErrorForAReflectiveReportWithAmbiguousNames {
    val source = new BlockSource(List(Line(1, List("Report", "Report Name Different By Case", "!!"))))
    val reportBlock = new ReportBlock(source, "Report Name Different By Case", true, Nil, List(new ReportExecution(None, Nil)))
    val executionContext: SodaTestExecutionContext = new SodaTestExecutionContext(new SodaTestContext)
    executionContext.currentFixture = Some(new FixtureThatCausesErrorsWhenCreatingStuff())
    val result: ReportBlockResult = ReportBlockExecutor.execute(reportBlock, executionContext)
    assertThat(result.block, is(sameInstance(reportBlock)))
    result.blockError match {
      case None => fail("Expecting Block Error")
      case Some(error) => {
        assertThat(error.message, is("The Report name is ambiguous in the current Fixture"))
        assertThat(error.causeString, is(Some("org.sodatest.api.reflection.NameMatchesMoreThanOneMethodException: SodaReport name 'Report Name Different By Case' (canonized to 'reportnamedifferentbycase') matches more than one method: List(FixtureThatCausesErrorsWhenCreatingStuff.reportNameDifferentByCase, FixtureThatCausesErrorsWhenCreatingStuff.reportNameDifferentByCASE)").asInstanceOf[Option[String]]))
        assertThat(error.cause.get, is(instanceOf(classOf[NameMatchesMoreThanOneMethodException])))
      }
    }
  }

  @Test
  def shouldReturnABlockErrorForAnErrorWhileCreatingAnyReport {
    val source = new BlockSource(List(Line(1, List("Report", "Creating This Report Throws An Error", "!!"))))
    val reportBlock = new ReportBlock(source, "Creating This Report Throws An Error", true, Nil, List(new ReportExecution(None, Nil)))
    val executionContext: SodaTestExecutionContext = new SodaTestExecutionContext(new SodaTestContext)
    executionContext.currentFixture = Some(new FixtureThatCausesErrorsWhenCreatingStuff())
    val result: ReportBlockResult = ReportBlockExecutor.execute(reportBlock, executionContext)
    assertThat(result.block, is(sameInstance(reportBlock)))
    result.blockError match {
      case None => fail("Expecting Block Error")
      case Some(error) => {
        assertThat(error.message, is("An error occurred while creating the Report"))
        assertThat(error.causeString, is(Some("java.lang.RuntimeException: I refuse to be initisliased").asInstanceOf[Option[String]]))
        assertThat(error.cause.get, is(instanceOf(classOf[RuntimeException])))
      }
    }
  }
}

