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

package org.sodatest
package runtime.processing.formatting.xhtml

import api.SodaTestLog
import xml.Elem
import runtime.data.results.{ContainsParameterBindingException, EventBlockResult}

object XhtmlEventFormatter {

  import XhtmlBlockFormatter._

  def format(result: EventBlockResult)(implicit log: SodaTestLog): Elem = {
    log.debug("   Formatting: " + result)
    val formatter = new XhtmlParameterisedBlockFormatter(result)
    <div class={"blockResult event " + (if (result.block.inline) "inline " else "") + (if (result.succeeded) "success" else "failure")}>
      <table>
        <colgroup><col class="lineNumbers"/></colgroup>
        <thead>
          {formatter.columnHeaders}
        </thead>
        <tbody>
          {formatter.blockHeader}
          {formatter.resultError}
          {if (result.error == None) formatter.parameterNames else NO_XML }
          {
            for (val executionResult <- result.executionResults) yield {
              formatter.parameterValuesRow(executionResult) ++
              (executionResult.error match {
                case None => NO_XML

                case ContainsParameterBindingException(bindException) => {
                  formatter.failureDetails(() => {
                    formatter.bindFailureDetails(bindException, result)
                  }) ++ NEWLINE
                }

                case Some(error) => {
                  formatter.failureDetails(() => {
                    formatter.errorDetails(error.message, error.causeString, None, error.cause)
                  }) ++ NEWLINE
                }
              })
            }
          }
        </tbody>
      </table>
    </div>
  }
}