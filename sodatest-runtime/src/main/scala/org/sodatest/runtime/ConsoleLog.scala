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

import org.sodatest.api.SodaTestLog


object ConsoleLog {
  object Level extends Enumeration {
    type Level = Value
    val Error, Info, Debug, Verbose = Value
  }

  def apply(): ConsoleLog =
    new ConsoleLog(Level.withName(System.getProperty("sodatest.log.level", "Info")))
}

class ConsoleLog(val level: ConsoleLog.Level.Level) extends SodaTestLog {

  import ConsoleLog.Level._

  def error(message: String) = if (level >= Error)     println("SodaTest: ERROR: " + message)
  def info(message: String) = if (level >= Info)       println("SodaTest: " + message)
  def debug(message: String) = if (level >= Debug)     println("SodaTest: " + message)
  def verbose(message: String) = if (level >= Verbose) println("SodaTest: " + message)

}