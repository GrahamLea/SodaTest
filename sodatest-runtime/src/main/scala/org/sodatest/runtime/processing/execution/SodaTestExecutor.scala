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
package processing.execution

import data.SodaTest
import processing.SodaTestContext
import data.results.{BlockResult, SodaTestResult}
import annotation.tailrec

object SodaTestExecutor {
  def execute(test: SodaTest, context: SodaTestContext): SodaTestResult = {
    context.log.debug("   Executing...")
    val executionContext = new SodaTestExecutionContext(context)
    val blockResults = test.blocks.map(b => {context.log.verbose("      " + b); b.execute(executionContext) } )
    new SodaTestResult(test, blockResults, allSucceeded(blockResults))
  }

  @tailrec
  private def allSucceeded(blockResults: List[BlockResult[_]]): Boolean = {
    blockResults match {
      case Nil => true
      case head :: tail => {
        if (head.succeeded)
          allSucceeded(tail)
        else
          false
      }
    }
  }

}