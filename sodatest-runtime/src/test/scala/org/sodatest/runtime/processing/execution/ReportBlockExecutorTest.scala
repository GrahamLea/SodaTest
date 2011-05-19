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
import org.sodatest.runtime.data.results.ReportBlockResult

class ReportBlockExecutorTest {
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
}