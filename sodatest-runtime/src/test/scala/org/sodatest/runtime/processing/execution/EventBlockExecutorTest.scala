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

import org.sodatest.runtime.data.blocks.{Line, BlockSource, EventExecution, EventBlock}
import org.sodatest.runtime.processing.SodaTestContext
import org.sodatest.runtime.data.results.EventBlockResult
import org.junit.Test
import org.junit.Assert._
import org.hamcrest.CoreMatchers._

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
}