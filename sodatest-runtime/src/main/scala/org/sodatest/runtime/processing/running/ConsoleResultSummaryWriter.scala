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

package org.sodatest.runtime.processing.running

import java.io.File

object ConsoleResultSummaryWriter extends SodaTestResultSummaryWriter {
  def writeSummaries(results: Seq[SodaTestResultSummary], inputRoot: File, outputRoot: File) {
    val totalFailedTests = results.filter(_.failed).size
    val totalErrors = results.map(_.errorCount).sum
    val totalMismatches = results.map(_.mismatchCount).sum
    println("----------------------------------------")
    if (totalErrors == 0 && totalMismatches == 0) {
      printf("%s Test%s ran\n", results.size, if (results.size == 1) "" else "s")
      println("No errors or mismatches")
      println("SodaTest Result: ALL TESTS PASSED")
    } else {
      println("Failing Tests:")
      for (failedTest <- results.filter(r => r.errorCount != 0 || r.mismatchCount != 0)) {
        printf("\t%s (%s)\n", failedTest.testName, failedTest.testPath)
      }
      printf("%s Test%s ran\n", results.size, if (results.size == 1) "" else "s")
      printf("%s Test%s failed\n", totalFailedTests, if (totalFailedTests == 1) "" else "s")
      if (totalMismatches != 0)
        println("\t" + totalMismatches + " Report(s) had mismatches")
      if (totalErrors != 0)
        println("\t" + totalErrors + " Block(s) caused errors")
      println("SodaTest Result: THERE WERE FAILURES")
    }
    println("----------------------------------------")
  }
}