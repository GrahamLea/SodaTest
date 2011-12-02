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

package org.sodatest.api { package reflection {

import org.sodatest.coercion.CoercionReflectionUtil

/**
 * [[org.sodatest.api.SodaFixture]] base class that supports the discovery of [[org.sodatest.api.SodaEvent]]s
 * and [[org.sodatest.api.SodaReport]]s by reflecting on the subclass' functions.
 *
 * ReflectiveSodaFixture is probably the easiest way to implement the [[org.sodatest.api.SodaFixture]] trait.
 * Simply extend this trait and then define in the subclass public functions that have no parameters
 * and which return either a [[org.sodatest.api.SodaEvent]] or [[org.sodatest.api.SodaReport]] as required.
 * ReflectiveSodaFixture will canonize the incoming Event or Report name and then discover and invoke
 * a function on the subclass that has a name which, when also canonized, matches the Event or
 * Report name.
 *
 * (Names in SodaTest are canonized by removing all non-alpha-numeric characters and
 * converting all alpha characters to lower-case. e.g. canonized("Secret Report #2") -> "secretreport2")
 *
 * <b>Example</b>
 * {{{
 * class MyFixutre extends ReflectiveSodaFixture {
 *   def secretReport2: SodaReport = new SecretReport2()
 * }
 * }}}
 */
trait ReflectiveSodaFixture extends SodaFixture {
  import CoercionReflectionUtil._

  /**
   * Creates a SodaEvent by reflecting on this SodaFixture to find a function whose canonized
   * name is equivalent to the canonized version of the specified name.
   */
  def createEvent(name: String): Option[SodaEvent] = invokeNoParamFunctionReturning(classOf[SodaEvent], name, this)

  /**
   * Creates a SodaReport by reflecting on this SodaFixture to find a function whose canonized
   * name is equivalent to the canonized version of the specified name.
   */
  def createReport(name: String): Option[SodaReport] = invokeNoParamFunctionReturning(classOf[SodaReport], name, this)
}

}}