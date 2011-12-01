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
  def patternsForSymbol(currencySymbol: Char) = {
    List("#,##0.00;-#,##0.00",
         "#,##0.00;(#,##0.00)",
         currencySymbol + "#,##0.00;-" + currencySymbol + "#,##0.00",
         currencySymbol + "#,##0.00;" + currencySymbol + "-#,##0.00",
         currencySymbol + "#,##0.00;(" + currencySymbol + "#,##0.00)",
         currencySymbol + "#,##0.00;" + currencySymbol + "(#,##0.00)"
    )
  }
}

/**
 * TODO: Document
 */
class CurrencyAmountCoercion[A](
    val targetClass: Class[A],
    decimalFormatPatterns: Iterable[String] = CurrencyAmountCoercion.patternsForSymbol('$'))
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
