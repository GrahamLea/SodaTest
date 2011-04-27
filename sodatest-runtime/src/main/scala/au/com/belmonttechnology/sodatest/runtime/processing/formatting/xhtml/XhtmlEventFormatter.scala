// Copyright (c) 2010 Belmont Technology Pty Ltd. All rights reserved.

package au.com.belmonttechnology.sodatest
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
          {formatter.parameterNames}
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
                    formatter.errorDetails(error.message, error.causeString, None, error.causeTrace)
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