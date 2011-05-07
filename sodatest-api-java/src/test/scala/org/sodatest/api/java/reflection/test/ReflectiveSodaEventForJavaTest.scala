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

package org.sodatest.api.java.reflection.test

import java.lang.String
import org.hamcrest.CoreMatchers._
import collection.immutable.Map
import org.junit.Assert._
import org.junit.Test
import org.sodatest.api.ParameterBindingException
import org.sodatest.coercion.UnableToCoerceException
import java.math.BigDecimal
;

class ReflectiveSodaEventForJavaTest {

  @Test
  def apply_PublicFields() {
    val event = new EventWithPublicFields
    event.apply(Map(
      "AmounT" -> "7",
      "Big Decimal" -> "12.345",
      "String Option One" -> "Foo",
      "String Option Two" -> "",
      "String On Superclass" -> "Super"
    ))

    assertThat(event.amount.getValue, is(7))
    assertThat(event.bigDecimal, is(new BigDecimal("12.345")))
    assertThat(event.anotherAmount, nullValue())
    assertThat(event.stringOptionOne.get, is("Foo"))
    assertThat(event.stringOptionTwo, is(None.asInstanceOf[Option[String]]))
    assertThat(event.stringOnSuperclass, is("Super"))
    // TODO: Test Coercion?
  }

  @Test
  def apply_PublicFields_ThrowExceptionForMissingParameter() {
    val event = new EventWithPublicFields
    try {
      event.apply(Map("Not There" -> "Five"))
    }
    catch {
      case e: ParameterBindingException => {
        e.bindFailures match {
          case List(failure) => {
            assertThat(failure.parameterName, is("Not There"))
            assertThat(failure.parameterValue, is("Five"))
            assertThat(failure.errorMessage, is("Parameter 'Not There' could not be found on EventWithPublicFields (org.sodatest.api.java.reflection.test)"))
            assertThat(failure.exception, is(None.asInstanceOf[Option[Throwable]]))
          }
          case _ => fail("Expecting one bind failure")
        }
      }
    }
  }

  @Test
  def apply_PublicFields_ThrowsParameterBindExceptionsForFailureToConvertInt() {
    val event = new EventWithPublicFields

    try {
      event.apply(Map(
        "AmounT" -> "xyz",
        "Big Decimal" -> "abc"
      ))
      fail("Expecting ParameterBindingException")
    }
    catch {
      case pbe: ParameterBindingException => {
        pbe.bindFailures match {
          case List(failure1, failure2) => {
            assertThat(failure1.parameterName, is("AmounT"))
            assertThat(failure1.parameterValue, is("xyz"))
            assertThat(failure1.errorMessage, is("org.sodatest.coercion.UnableToCoerceException: Unable to coerce value 'xyz' to type org.sodatest.api.java.reflection.test.Amount: failed to set text value of PropertyEditor (AmountEditor)"))
            assertThat(failure1.exception.get, is(instanceOf(classOf[UnableToCoerceException])))
            assertThat(failure2.parameterName, is("Big Decimal"))
            assertThat(failure2.parameterValue, is("abc"))
            assertThat(failure2.errorMessage, is("org.sodatest.coercion.UnableToCoerceException: Unable to coerce value 'abc' to type java.math.BigDecimal: error invoking constructor public java.math.BigDecimal(java.lang.String)"))
            assertThat(failure2.exception.get, is(instanceOf(classOf[UnableToCoerceException])))
          }
          case _ => fail("Expecting two bind failures")
        }
      }
    }
  }

  @Test
  def apply_Setters() {
    val event = new EventWithSetters
    event.apply(Map(
      "AmounT" -> "7",
      "Big Decimal" -> "12.345",
      "String Option One" -> "Foo",
      "String Option Two" -> "",
      "String On Superclass" -> "Super"
    ))

    assertThat(event.amount.getValue, is(7))
    assertThat(event.bigDecimal, is(new BigDecimal("12.345")))
    assertThat(event.anotherAmount, nullValue())
    assertThat(event.stringOptionOne.get, is("Foo"))
    assertThat(event.stringOptionTwo, is(None.asInstanceOf[Option[String]]))
    assertThat(event.stringOnSuperclass, is("Super"))
    // TODO: Test Coercion?
  }

  @Test
  def apply_Setters_ThrowExceptionForMissingParameter() {
    val event = new EventWithSetters
    try {
      event.apply(Map("Not There" -> "Five"))
    }
    catch {
      case e: ParameterBindingException => {
        e.bindFailures match {
          case List(failure) => {
            assertThat(failure.parameterName, is("Not There"))
            assertThat(failure.parameterValue, is("Five"))
            assertThat(failure.errorMessage, is("Parameter 'Not There' could not be found on EventWithSetters (org.sodatest.api.java.reflection.test)"))
            assertThat(failure.exception, is(None.asInstanceOf[Option[Throwable]]))
          }
          case _ => fail("Expecting one bind failure")
        }
      }
    }
  }

  @Test
  def apply_Setters_ThrowsParameterBindExceptionsForFailureToConvertInt() {
    val event = new EventWithSetters

    try {
      event.apply(Map(
        "AmounT" -> "xyz",
        "Big Decimal" -> "abc"
      ))
      fail("Expecting ParameterBindingException")
    }
    catch {
      case pbe: ParameterBindingException => {
        pbe.bindFailures match {
          case List(failure1, failure2) => {
            assertThat(failure1.parameterName, is("AmounT"))
            assertThat(failure1.parameterValue, is("xyz"))
            assertThat(failure1.errorMessage, is("org.sodatest.coercion.UnableToCoerceException: Unable to coerce value 'xyz' to type org.sodatest.api.java.reflection.test.Amount: failed to set text value of PropertyEditor (AmountEditor)"))
            assertThat(failure1.exception.get, is(instanceOf(classOf[UnableToCoerceException])))
            assertThat(failure2.parameterName, is("Big Decimal"))
            assertThat(failure2.parameterValue, is("abc"))
            assertThat(failure2.errorMessage, is("org.sodatest.coercion.UnableToCoerceException: Unable to coerce value 'abc' to type java.math.BigDecimal: error invoking constructor public java.math.BigDecimal(java.lang.String)"))
            assertThat(failure2.exception.get, is(instanceOf(classOf[UnableToCoerceException])))
          }
          case _ => fail("Expecting two bind failures")
        }
      }
    }
  }

}
