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

/**
 * Provides helper methods for converting objects, and collections of objects into the
 * <code>Seq[Seq[String]]</code> type expected to be returned from [[org.sodatest.api.SodaReport.apply]]
 */
object SodaReport {

  /** Converts a single object of any type to List[List[String]] using String.valueOf() */
  def toSingleCellReport(item: Any): List[List[String]] = List(List(String.valueOf(item)))

  /**
   * Converts a collection of objects of any type to a List[List[String]] using String.valueOf()
   * such that all the values appears as a single row.
   */
  def toSingleRowReport(items: Iterable[Any]): List[List[String]] = List(items.toList.map(item => String.valueOf(item)))

  /**
   * Converts a collection of objects of any type to a List[List[String]] using String.valueOf()
   * such that all the values appears as a single column.
   */
  def toSingleColumnReport(items: Iterable[Any]): List[List[String]] = items.toList.map(item => List(String.valueOf(item)))

  /**
   * Converts a colleciton of collections of objects of any type to a List[List[String]] using String.valueOf().
   * The outer colleciton of the input represents rows in the output table, while the inner collections
   * of the input correspond to cells within each of the rows.
   */
  def toReport(table: Iterable[_ <: Iterable[Any]]): List[List[String]] = table.toList.map(row => row.toList.map(cell => String.valueOf(cell)))

  /**
   * A pimping object which, in conjunction with the implicit def <code>any2ItemWrapper</code>,
   * allows <code>toSingleCellReport</code> to be called on any value.
    */
  class ItemWrapper(val item: Any) {
    def toSingleCellReport: List[List[String]] = SodaReport.toSingleCellReport(item)
  }

  /**
   * A pimping object which, in conjunction with the implicit def <code>anyList2ItemListWrapper</code>,
   * allows <code>toSingleRowReport</code> or <code>toSingleColumnReport</code> to be called on any collection.
    */
  class ItemListWrapper(val items: Iterable[Any]) {
    def toSingleRowReport: List[List[String]] = SodaReport.toSingleRowReport(items)
    def toSingleColumnReport: List[List[String]] = SodaReport.toSingleColumnReport(items)
  }

  /**
   * A pimping object which, in conjunction with the implicit def <code>anyListList2ItemTableWrapper</code>,
   * allows <code>toReport</code> to be called on any collection of collections.
    */
  class ItemTableWrapper(val table: Iterable[_ <: Iterable[Any]]) {
    def toReport: List[List[String]] = SodaReport.toReport(table)
  }

  implicit def any2ItemWrapper(item: Any): ItemWrapper = new ItemWrapper(item)
  implicit def anyList2ItemListWrapper(items: Iterable[Any]): ItemListWrapper = new ItemListWrapper(items)
  implicit def anyListList2ItemTableWrapper(table: Iterable[_ <: Iterable[Any]]): ItemTableWrapper = new ItemTableWrapper(table)

}