// Copyright (c) 2010 Belmont Technology Pty Ltd. All rights reserved.

package org.sodatest.runtime
package processing.execution

import data.SodaTest
import processing.SodaTestContext
import data.results.{BlockResult, SodaTestResult}
import annotation.tailrec

class SodaTestExecutor() {
  def execute(test: SodaTest, context: SodaTestContext): SodaTestResult = {
    context.log.info("Executing...")
    val executionContext = new SodaTestExecutionContext(context)
    val blockResults = test.blocks.map(b => {context.log.info("   Executing " + b); b.execute(executionContext) } )
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