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

package org.sodatest.runtime.processing.parsing.csv

import org.sodatest.runtime.processing.SodaTestContext

trait CellSplitter {
  def split(in: java.io.InputStream)(implicit context: SodaTestContext): List[List[String]]
}

object CsvCellSplitter extends CellSplitter {
  //TODO: Use Reader rather than InputStream
  def split(in: java.io.InputStream)(implicit context: SodaTestContext): List[List[String]] = {

    @scala.annotation.tailrec
    def split(in: java.io.InputStream, inQuotes: Boolean, escaped: Boolean,
              currentValue: StringBuilder, currentRow: List[String], result: List[List[String]]) : List[List[String]] = {
      val c: Int = in.read()
      if (c == -1) {
        ((currentValue.toString :: currentRow).reverse :: result).reverse
      } else if (escaped) {
        split(in, inQuotes, false, currentValue.append(c.toChar), currentRow, result)
      } else c match {
        case '"' => split(in, !inQuotes, false, currentValue, currentRow, result)
        case '\\' => split(in, inQuotes, true, currentValue, currentRow, result)
        case ',' if !inQuotes =>
          split(in, inQuotes, false, new StringBuilder(), currentValue.toString :: currentRow, result)
        case '\n' if !inQuotes =>
          split(in, inQuotes, false, new StringBuilder(), List(), (currentValue.toString :: currentRow).reverse :: result)
        case _ => split(in, inQuotes, false, currentValue.append(c.toChar), currentRow, result)
      }
    }
    context.log.debug("   Splitting CSV...")
    split(in, false, false, new StringBuilder(), List(), List())
  }
}