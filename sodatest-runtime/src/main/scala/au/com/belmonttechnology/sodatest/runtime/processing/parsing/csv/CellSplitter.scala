// Copyright (c) 2010 Belmont Technology Pty Ltd. All rights reserved.

package au.com.belmonttechnology.sodatest.runtime.processing.parsing.csv

import au.com.belmonttechnology.sodatest.api.SodaTestLog

trait CellSplitter {
  def split(in: java.io.InputStream): List[List[String]]
}

class CsvCellSplitter(implicit val log: SodaTestLog) extends CellSplitter {
  //TODO: Use Reader rather than InputStream
  def split(in: java.io.InputStream): List[List[String]] = {

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
    log.info("Splitting CSV...")
    split(in, false, false, new StringBuilder(), List(), List())
  }
}