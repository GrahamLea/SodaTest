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
package processing
package running

import data.results.{EventBlockResult, ReportBlockResult, SodaTestResult}
import collection.immutable.List

class SodaTestResultSummary(val testName: String, val testPath: String, val mismatchCount: Int, val errorCount: Int) {
  val failed = mismatchCount != 0 || errorCount != 0
}

object SodaTestResultSummary {

  def apply(r: SodaTestResult): SodaTestResultSummary = summarise(r)

  def summariseList(rs: List[SodaTestResult]): List[SodaTestResultSummary] = rs.map(r => summarise(r))

  def summarise(r: SodaTestResult): SodaTestResultSummary = {
    val mismatchedBlocks: Int = r.blockResults.flatMap {_ match {
        case rbr: ReportBlockResult => rbr.executionResults.map {er => if (er.matchResult.passed) 0 else 1}
        case _ => Nil
    }}.sum

    val blockErrors = r.blockResults.filter(r => r.blockError != None || (r.errorOccurred && !r.executionErrorOccurred)).size

    val executionErrors = r.blockResults.map(br => {br match {
        case rbr: ReportBlockResult => rbr.executionResults.filter(_.error != None).size
        case ebr: EventBlockResult => ebr.executionResults.filter(_.error != None).size
        case _ => 0
      }}).sum

    new SodaTestResultSummary(r.test.testName, r.test.testPath, mismatchedBlocks, blockErrors + executionErrors)
  }

}





