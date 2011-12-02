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
import org.sodatest.runtime.data.results.EventBlockResult
import org.sodatest.coercion.{NameMatchesMoreThanOneMethodException, ReflectionTargetReturnsTheWrongTypeException}
import org.sodatest.runtime.data.blocks._

class EventBlockExecutorTest {
  @Test
  def shouldReturnAnExecutionErrorForNoFixture {
    val source = new BlockSource(List(Line(1, List("Event", "My Event")), Line(2, List("", "Param One")), Line(3, List("", "Value One"))))
    val eventBlock = new EventBlock(source, "My Event", false, List("Param One"), List(new EventExecution(Some(source.lines(2)))))
    val result: EventBlockResult = EventBlockExecutor.execute(eventBlock, new SodaTestExecutionContext(new SodaTestContext))
    assertThat(result.block, is(sameInstance(eventBlock)))
    result.blockError match {
      case None => fail("Expecting Block Error")
      case Some(error) => {
        assertThat(error.message, is("No Fixture has been declared before this Event"))
        assertThat(error.causeString, is(None.asInstanceOf[Option[String]]))
        assertThat(error.cause, is(None.asInstanceOf[Option[Throwable]]))
      }
    }
  }

  @Test
  def shouldReturnABlockErrorForAReflectiveReportReturningAnEvent {
    val source = new BlockSource(List(Line(1, List("Event", "Event Name Returning Report"))))
    val eventBlock = new EventBlock(source, "Event Name Returning Report", true, Nil, List(new EventExecution(None)))
    val executionContext: SodaTestExecutionContext = new SodaTestExecutionContext(new SodaTestContext)
    executionContext.currentFixture = Some(new FixtureThatCausesErrorsWhenCreatingStuff())
    val result: EventBlockResult = EventBlockExecutor.execute(eventBlock, executionContext)
    assertThat(result.block, is(sameInstance(eventBlock)))
    result.blockError match {
      case None => fail("Expecting Block Error")
      case Some(error) => {
        assertThat(error.message, is("The function that matches this name does not return an Event"))
        assertThat(error.causeString, is(Some("org.sodatest.coercion.ReflectionTargetReturnsTheWrongTypeException: Function 'eventNameReturningReport' does not return a SodaEvent").asInstanceOf[Option[String]]))
        assertThat(error.cause.get, is(instanceOf(classOf[ReflectionTargetReturnsTheWrongTypeException])))
      }
    }
  }

  @Test
  def shouldReturnABlockErrorForAReflectiveReportWithAmbiguousNames {
    val source = new BlockSource(List(Line(1, List("Event", "Event Name Different By Case", "!!"))))
    val EventBlock = new EventBlock(source, "Event Name Different By Case", true, Nil, List(new EventExecution(None)))
    val executionContext: SodaTestExecutionContext = new SodaTestExecutionContext(new SodaTestContext)
    executionContext.currentFixture = Some(new FixtureThatCausesErrorsWhenCreatingStuff())
    val result: EventBlockResult = EventBlockExecutor.execute(EventBlock, executionContext)
    assertThat(result.block, is(sameInstance(EventBlock)))
    result.blockError match {
      case None => fail("Expecting Block Error")
      case Some(error) => {
        assertThat(error.message, is("The Event name is ambiguous in the current Fixture"))
        assertThat(error.causeString, is(Some("org.sodatest.coercion.NameMatchesMoreThanOneMethodException: SodaEvent name 'Event Name Different By Case' (canonized to 'eventnamedifferentbycase') matches more than one method: List(FixtureThatCausesErrorsWhenCreatingStuff.eventNameDifferentByCase, FixtureThatCausesErrorsWhenCreatingStuff.eventNameDifferentByCASE)").asInstanceOf[Option[String]]))
        assertThat(error.cause.get, is(instanceOf(classOf[NameMatchesMoreThanOneMethodException])))
      }
    }
  }

  @Test
  def shouldReturnABlockErrorForAnErrorWhileCreatingAnyEvent {
    val source = new BlockSource(List(Line(1, List("Event", "Creating This Event Throws An Error", "!!"))))
    val EventBlock = new EventBlock(source, "Creating This Event Throws An Error", true, Nil, List(new EventExecution(None)))
    val executionContext: SodaTestExecutionContext = new SodaTestExecutionContext(new SodaTestContext)
    executionContext.currentFixture = Some(new FixtureThatCausesErrorsWhenCreatingStuff())
    val result: EventBlockResult = EventBlockExecutor.execute(EventBlock, executionContext)
    assertThat(result.block, is(sameInstance(EventBlock)))
    result.blockError match {
      case None => fail("Expecting Block Error")
      case Some(error) => {
        assertThat(error.message, is("An error occurred while creating the Event"))
        assertThat(error.causeString, is(Some("java.lang.RuntimeException: I refuse to be initisliased").asInstanceOf[Option[String]]))
        assertThat(error.cause.get, is(instanceOf(classOf[RuntimeException])))
      }
    }
  }
}
