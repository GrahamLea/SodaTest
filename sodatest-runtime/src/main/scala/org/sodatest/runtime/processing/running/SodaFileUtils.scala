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

package org.sodatest.runtime.processing.running

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