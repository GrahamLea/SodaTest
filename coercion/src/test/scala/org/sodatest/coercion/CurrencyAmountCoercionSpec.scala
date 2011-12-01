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

package org.sodatest.coercion { package test {

import org.specs.SpecificationWithJUnit

import _root_.java.math.{BigDecimal => JBigDecimal}

class CurrencyAmountCoercionSpec extends SpecificationWithJUnit {

  "CurrencyAmountCoercion" should {
    
    val coercionForBigDecimal = new CurrencyAmountCoercion(classOf[CurrencyTypeWithBigDecimalConstructor])
    val coercionForJBigDecimal = new CurrencyAmountCoercion(classOf[CurrencyTypeWithJavaBigDecimalConstructor])
    val coercionForString = new CurrencyAmountCoercion(classOf[CurrencyTypeWithStringConstructor])

    "coerce numbers without a currency to a customer currency type with a scala.math.BigDecimal constructor" in {
      coercionForBigDecimal("12.00") must beLike  {
        case CurrencyTypeWithBigDecimalConstructor(value) => value == BigDecimal("12.00")
      }
      coercionForBigDecimal("101.99") must beLike  {
        case CurrencyTypeWithBigDecimalConstructor(value) => value == BigDecimal("101.99")
      }
      coercionForBigDecimal("-101.99") must beLike  {
        case CurrencyTypeWithBigDecimalConstructor(value) => value == BigDecimal("-101.99")
      }
    }

    "coerce numbers without a currency to a customer currency type with a java.math.BigDecimal constructor" in {
      coercionForJBigDecimal("12.00") must beLike  {
        case CurrencyTypeWithJavaBigDecimalConstructor(value) => value == new JBigDecimal("12.00")
      }
      coercionForJBigDecimal("101.99") must beLike  {
        case CurrencyTypeWithJavaBigDecimalConstructor(value) => value == new JBigDecimal("101.99")
      }
      coercionForJBigDecimal("-101.99") must beLike  {
        case CurrencyTypeWithJavaBigDecimalConstructor(value) => value == new JBigDecimal("-101.99")
      }
    }

    "coerce numbers without a currency to a customer currency type with a String constructor" in {
      coercionForString("12.00") must beLike  {
        case CurrencyTypeWithStringConstructor(value) => value == "12.00"
      }
      coercionForString("101.99") must beLike  {
        case CurrencyTypeWithStringConstructor(value) => value == "101.99"
      }
      coercionForString("-101.99") must beLike  {
        case CurrencyTypeWithStringConstructor(value) => value == "-101.99"
      }
    }

    "coerce numbers with a currency symbol to a customer currency type" in {
      coercionForBigDecimal("$12.00") must beLike  {
        case CurrencyTypeWithBigDecimalConstructor(value) => value == BigDecimal("12.00")
      }
      coercionForBigDecimal("$101.99") must beLike  {
        case CurrencyTypeWithBigDecimalConstructor(value) => value == BigDecimal("101.99")
      }
      coercionForBigDecimal("-$101.99") must beLike  {
        case CurrencyTypeWithBigDecimalConstructor(value) => value == BigDecimal("-101.99")
      }
      coercionForBigDecimal("$-101.99") must beLike  {
        case CurrencyTypeWithBigDecimalConstructor(value) => value == BigDecimal("-101.99")
      }
    }

    "coerce numbers using financial negative format to a customer currency type with a scala.math.BigDecimal constructor" in {
      coercionForBigDecimal("(101.99)") must beLike  {
        case CurrencyTypeWithBigDecimalConstructor(value) => value == BigDecimal("-101.99")
      }
      coercionForBigDecimal("($101.99)") must beLike  {
        case CurrencyTypeWithBigDecimalConstructor(value) => value == BigDecimal("-101.99")
      }
      coercionForBigDecimal("$(101.99)") must beLike  {
        case CurrencyTypeWithBigDecimalConstructor(value) => value == BigDecimal("-101.99")
      }
    }

  }
}

case class CurrencyTypeWithBigDecimalConstructor(value: BigDecimal)

case class CurrencyTypeWithJavaBigDecimalConstructor(value: JBigDecimal)

case class CurrencyTypeWithStringConstructor(value: String)

}}