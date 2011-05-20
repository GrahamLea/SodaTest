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
import java.nio.charset.Charset
import java.nio.ByteBuffer
import java.io.{Reader, InputStreamReader, InputStream}

trait CellSplitter {
  def split(in: java.io.InputStream)(implicit context: SodaTestContext): List[List[String]]
}

object CsvCellSplitter extends CellSplitter {
  def split(in: java.io.InputStream)(implicit context: SodaTestContext): List[List[String]] = {
    val charBuffer: Array[Char] = Array.ofDim(1);

    @scala.annotation.tailrec
    def split(in: Reader, inQuotes: Boolean, escaped: Boolean,
              currentCell: StringBuilder, currentRow: List[String], result: List[List[String]]): List[List[String]] = {
      in.read(charBuffer) match {
        case -1 => ((currentCell.toString :: currentRow).reverse :: result).reverse
        case _ => charBuffer(0) match {
          case c if escaped =>      split(in, inQuotes, false, currentCell.append(c), currentRow, result)
          case '"' =>               split(in, !inQuotes, false, currentCell, currentRow, result)
          case '\\' =>              split(in, inQuotes, true, currentCell, currentRow, result)
          case ',' if !inQuotes =>  split(in, inQuotes, false, new StringBuilder(), currentCell.toString :: currentRow, result)
          case '\n' if !inQuotes => split(in, inQuotes, false, new StringBuilder(), List(), (currentCell.toString :: currentRow).reverse :: result)
          case c =>                 split(in, inQuotes, false, currentCell.append(c), currentRow, result)
        }
      }
    }
    
    context.log.debug("   Splitting CSV...")
    val charset = System.getProperty("sodatest.charset", "default") match {
      case "Default" => Charset.defaultCharset()
      case charsetName => Charset.forName(charsetName)
    }
    split(new InputStreamReader(in, charset), false, false, new StringBuilder(), List(), List())
  }
}