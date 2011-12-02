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

import org.sodatest.coercion.CoercionBindingException

/**
 * An Event that, when applied, causes some input to be applied to the system under test.
 *
 * Events represent the 'Command' element of the Command-Query Separation principle that is encouraged
 * by SodaTest. It performs actions on the System under test, but reports nothing back to the invoker
 * as long as the action succeeds.
 *
 * SodaEvents are a one-shot object. The apply() method will only ever be called once.
 *
 * The majority of Events will be more easily written by extending [[org.sodatest.api.reflection.ReflectiveSodaEvent]]
 *
 * @see [[org.sodatest.api.reflection.ReflectiveSodaEvent]]
 * @see [[org.sodatest.api.java.reflection.SodaEventForJava]]
 * @see [[org.sodatest.api.java.reflection.ReflectiveSodaEventForJava]]
 */
trait SodaEvent {
  /**
   * Applies this event to the System under test using the given parameters.
   *
   * @param parameters A map of parameter names and values that should be used in applying the
   * Event.
   *
   * @throws CoercionBindingException if an error occurs while attempting to translate one of the
   * string values in the parameter map into a value that can be used by the Event.
   *
   * @throws java.lang.Throwable if anything else goes wrong while executing the Event.
   */
  @throws(classOf[CoercionBindingException])
  def apply(parameters: Map[String, String]): Unit
}