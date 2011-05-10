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

/**
 * SodaFixture is the primary interface between a Test and your code.
 *
 * Soda Tests use a SodaFixture to create [[org.sodatest.api.SodaEvent]] and [[org.sodatest.api.SodaReport]] instances that are then used
 * to affect and query the system under test. Events and Reports differ in that Events are used only
 * to affect the System under test, and not to query it, while Reports should be used only to
 * query the System, not to affect it. This is known as [[http://en.wikipedia.org/wiki/Command-query_separation Command-Query Separation]] as is an
 * important principle in maintaining the simplicity and readability of Soda Tests.
 *
 * <b>Note:</b> A SodaFixture should return a new instance of a SodaEvent or SodaReport for
 * every call to one of the create methods. Events and Reports are not intended to be re-used.
 * As a consequence, the creation of these objects should be as computationally simple as possible.
 * Any expensive initialisation whose result can be cached for efficiency should be done either in
 * the initialisation on the SodaFixture instance or somewhere else external to the Events and Reports.
 *
 * @see [[org.sodatest.api.reflection.ReflectiveSodaFixture]]
 * @see [[org.sodatest.api.java.reflection.ReflectiveSodaFixtureForJava]]
 */
trait SodaFixture {
  /**
   * Creates and returns a SodaEvent based on the specified name.
   *
   * Note that createEvent() should <b>always</b> create a new Event instance. SodaEvent instances
   * should not be re-used.
   *
   * @return a SodaEvent, wrapped in a Some, or None if the Fixture doesn't know how to create an
   * Event for the given name.
   */
  def createEvent(name: String): Option[SodaEvent]

  /**
   * Creates and returns a SodaReport based on the specified name.
   *
   * Note that createReport() should <b>always</b> create a new Report instance. SodaReport instances
   * should not be re-used.
   *
   * @return a SodaReport, wrapped in a Some, or None if the Fixture doesn't know how to create a
   * Report for the given name.
   */
  def createReport(name: String): Option[SodaReport]
}

/**
 * Implicit functions that can aid in the authoring of [[org.sodatest.api.SodaFixture]]s
 */
object SodaFixture {
  implicit def sodaEvent2Option(e: SodaEvent): Option[SodaEvent] = Some(e)
  implicit def sodaReport2Option(r: SodaReport): Option[SodaReport] = Some(r)

  implicit def function2EventOption(f: (Map[String, String]) => Unit): Option[SodaEvent] = new SodaEvent {
    def apply(parameters: Map[String, String]) = f(parameters)
  }

  implicit def function2ReportOption(f: (Map[String, String]) => Seq[Seq[String]]): Option[SodaReport] = new SodaReport {
    def apply(parameters: Map[String, String]) = f(parameters)
  }
}
