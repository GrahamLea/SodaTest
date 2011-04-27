// Copyright (c) 2010 Belmont Technology Pty Ltd. All rights reserved.

package au.com.belmonttechnology.sodatest.runtime.processing.running

import java.io.File

object SodaFileUtils {
  def getTestName(inputFile: File): String = {
    val lastDot = inputFile.getName.lastIndexOf('.') match {
      case -1 => inputFile.getName.length
      case i => i
    }

    inputFile.getName.substring(0, lastDot)
      .replaceAll("[-_\\. ]+", " ")
      .trim
      .foldLeft(new StringBuilder)(insertSpaceBeforeEachCapital)
      .toString
      .replaceAll(" +", " ")
      .trim
  }

  private def insertSpaceBeforeEachCapital(b: StringBuilder, c: Char) = {
    object Upper {
      def unapply(c: Char) = if (Character.isUpperCase(c)) Some(c) else None
    }

    if (b.isEmpty) b.append(c.toUpper)
    else c match {
      case Upper(c) => b.append(" ").append(c)
      case c => b.append(c)
    }
  }

}