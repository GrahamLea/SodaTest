// Copyright (c) 2010 Belmont Technology Pty Ltd. All rights reserved.

package org.sodatest
package runtime
package processing.formatting.xhtml

import api.SodaTestLog
import data.results.FixtureBlockResult
import xml.Elem

object XhtmlFixtureFormatter {
  def format(result: FixtureBlockResult)(implicit log: SodaTestLog): Elem = {
    log.debug("   Formatting: " + result)
    val formatter = new XhtmlBlockFormatter(result)
    <div class={"blockResult fixture " + (if (result.succeeded) "success" else "failure")}>
      <table>
        <colgroup><col class="lineNumbers"/></colgroup>
        <thead>
          {formatter.columnHeaders}
        </thead>
        <tbody>
          {formatter.blockHeader}
          {formatter.resultError}
        </tbody>
      </table>
    </div>
  }
}