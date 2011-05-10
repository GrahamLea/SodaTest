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
 * A Report that queries the status of the System under test and returns the result of that query
 * as a table.
 *
 * Reports represent the 'Query' element of the Command-Query Separation principle that is encouraged
 * by SodaTest. It reports to the invoker some status about the System, but does not perform any
 * action on the System which might change its state.
 *
 * SodaReports are a one-shot object. The apply() method will only ever be called once.
 *
 * The majority of Reports will be more easily written by extending [[org.sodatest.api.reflection.ReflectiveSodaReport]]
 * 
 * @see [[org.sodatest.api.reflection.ReflectiveSodaReport]]
 * @see [[org.sodatest.api.java.reflection.SodaReportForJava]]
 * @see [[org.sodatest.api.java.reflection.ReflectiveSodaReportForJava]]
 */
trait SodaReport {
  /**
   * Executes this Report against the System under test using the given parameters.
   *
   * @param parameters A map of parameter names and values that should be used in executing the
   * Report.
   *
   * @throws ParameterBindingException if an error occurs while attempting to translate one of the
   * string values in the parameter map into a value that can be used by the Report.
   *
   * @throws java.lang.Throwable if anything else goes wrong while executing the Report.
   */
  @throws(classOf[ParameterBindingException])
  def apply(parameters: Map[String, String]): Seq[Seq[String]]
}