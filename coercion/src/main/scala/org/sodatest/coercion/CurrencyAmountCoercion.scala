/*
 * Copyright (c) 2011 Belmont Technology Pty Ltd. All rights reserved.
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

package org.sodatest.coercion

import _root_.java.{text => jt, lang => jl, math => jm}
import jt.DecimalFormat
import jl.reflect.Constructor
import jm.{BigDecimal => JBigDecimal}
import reflect.Manifest

object CurrencyAmountCoercion {
  /*
   * Provides a list of DecimalFormat pattern strings for matching the most typical formats of
   * currency amount strings.
   *
   * Using the default parameters, the following patterns are produced:
   * #,##0.00
   * -#,##0.00
   * (#,##0.00)
   * $#,##0.00
   * -$#,##0.00
   * $-#,##0.00
   * ($#,##0.00)
   * $(#,##0.00)
   *
   * Specifying parameters to the function will replace the dollar sign (currencySymbol),
   * the comma (thousandSeparator), the period (decimalSeparator) and the number of decimal places
   * (numberOfDecimalPlaces), respectively. If numberOfDecimalPlaces is 0, the decimalSeparator
    * will not be used.
   */
  def currencyPatterns(currencySymbol: Char = '$', thousandSeparator: Char = ',', decimalSeparator: Char = '.', numberOfDecimalPlaces: Int = 2) = {
    val minorCurrencyFormat = if (numberOfDecimalPlaces == 0) "" else (decimalSeparator + ("0" * numberOfDecimalPlaces))
    List(String.format("#%1$s##0%2$s;(#%1$s##0%2$s)", thousandSeparator.asInstanceOf[AnyRef], minorCurrencyFormat),
         String.format("#%1$s##0%2$s;-#%1$s##0%2$s", thousandSeparator.asInstanceOf[AnyRef], minorCurrencyFormat),
         String.format("%1$s#%2$s##0%3$s;-%1$s#%2$s##0%3$s", currencySymbol.asInstanceOf[AnyRef], thousandSeparator.asInstanceOf[AnyRef], minorCurrencyFormat),
         String.format("%1$s#%2$s##0%3$s;%1$s-#%2$s##0%3$s", currencySymbol.asInstanceOf[AnyRef], thousandSeparator.asInstanceOf[AnyRef], minorCurrencyFormat),
         String.format("%1$s#%2$s##0%3$s;(%1$s#%2$s##0%3$s)", currencySymbol.asInstanceOf[AnyRef], thousandSeparator.asInstanceOf[AnyRef], minorCurrencyFormat),
         String.format("%1$s#%2$s##0%3$s;%1$s(#%2$s##0%3$s)", currencySymbol.asInstanceOf[AnyRef], thousandSeparator.asInstanceOf[AnyRef], minorCurrencyFormat)
    )
  }
}

/**
 * Coerces strings that are currency amounts (e.g. "($25,000.00)") to a specified strong type for
 * storing such amounts.
 *
 * <b>Example</b>
 * {{{
 * class MySodaEvent extends ReflectiveSodaEvent {
 *    var coercionRegister = new CoercionRegister(new CurrencyAmountCoercion(classOf[MyMoneyClass]))
 *
 *    ...
 * }}}
 *
 * The target class for the Coercion must provide a public, one-parameter constructor that accepts
 * either a {scala.math.BigDecimal}, a {java.math.BigDecimal} or a String.
 *
 * By default, the patterns accepted by the coercion are:
 * #,##0.00
 * -#,##0.00
 * (#,##0.00)
 * $#,##0.00
 * -$#,##0.00
 * $-#,##0.00
 * ($#,##0.00)
 * $(#,##0.00)
 *
 * You can, if necessary, specify the patterns to be used by passing into the constructor the
 * decimalFormatPatterns parameter, which must be a list of patterns acceptable to
 * {java.text.DecimalFormat}. The {org.sodatest.coercion.CurrencyAmountCoercion.currencyPatterns}
 * function is provided to aid in the construction of such pattern lists.
 *
 * See also {org.sodatest.coercion.java.CurrencyAmountCoercionForJava}
 */
class CurrencyAmountCoercion[A](
    val targetClass: Class[A],
    decimalFormatPatterns: Iterable[String] = CurrencyAmountCoercion.currencyPatterns())
  extends Coercion[A]()(Manifest.classType(targetClass)) {

  private val scalaBigDecimalClass = classOf[BigDecimal]
  private val javaBigDecimalClass = classOf[JBigDecimal]
  private val stringClass = classOf[String]

  private val formats = decimalFormatPatterns.map(s => {
    val format = new DecimalFormat(s)
    format.setParseBigDecimal(true)
    format
  })

  private val constructor: (Class[_], Constructor[A]) =
    List(scalaBigDecimalClass, javaBigDecimalClass, stringClass).toStream
      .flatMap(c => { try { Some((c, targetClass.getConstructor(c))) } catch { case _ => None.asInstanceOf[Option[(Class[_], Constructor[A])]] } })
      .headOption match {
        case Some(classAndConstructor) => classAndConstructor
        case None => throw new IllegalArgumentException(
            String.format("In order to be used with CurrencyAmountCoercion, class '%s' must provide a single-parameter constructor that takes either a scala.math.BigDecimal, a java.math.BigDecimal or String", targetClass.getSimpleName)
        )
      }

  def apply(s: String): A = {
    formats.toStream.flatMap(format => {
      try {
        Some(format.parse(s))
      } catch {
        case e => None
      }
    }).headOption match {
      case Some(value: JBigDecimal) => {
        if (constructor._1 == javaBigDecimalClass)
          constructor._2.newInstance(value)
        else if (constructor._1 == scalaBigDecimalClass)
          constructor._2.newInstance(BigDecimal(value))
        else
          constructor._2.newInstance(value.toString)
      }
      case None => throw new IllegalArgumentException(
        String.format("Value '%s' cannot be parsed by any of the formats: %s", s, decimalFormatPatterns.mkString("\"", "\", \"", "\'")))
    }

  }
}
