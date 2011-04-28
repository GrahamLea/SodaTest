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

package org.sodatest.api

trait SodaFixture {
  def createEvent(name: String): Option[SodaEvent]
  def createReport(name: String): Option[SodaReport]
}

object SodaFixture {
  implicit def sodaEvent2Option(e: SodaEvent): Option[SodaEvent] = Some(e)
  implicit def sodaReport2Option(r: SodaReport): Option[SodaReport] = Some(r)

  implicit def function2EventOption(f: (Map[String, String]) => Unit): Option[SodaEvent] = new SodaEvent {
    def apply(parameters: Map[String, String]) = f(parameters)
  }

  implicit def function2ReportOption(f: (Map[String, String]) => List[List[String]]): Option[SodaReport] = new SodaReport {
    def apply(parameters: Map[String, String]) = f(parameters)
  }
}
