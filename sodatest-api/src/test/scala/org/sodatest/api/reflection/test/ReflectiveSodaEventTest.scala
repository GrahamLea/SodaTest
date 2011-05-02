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

package org.sodatest.api.reflection { package test {

import java.beans.PropertyEditorSupport
import java.lang.String
import org.hamcrest.CoreMatchers._
import collection.immutable.Map
import org.junit.Assert._
import org.junit.Test
import org.sodatest.api.ParameterBindingException
import org.sodatest.coercion.{UnableToCoerceException, Coercion, CoercionRegister}
;

class ReflectiveSodaEventTest {

  @Test
  def apply_String() {

    val event = new Object() with ReflectiveSodaEvent {
      var value1: String = null
      var value2: String = null
      var value3: String = null

      def apply() = { }
    }
    event.apply(Map("value1" -> "First value", "value2" -> "Second value"))

    assertThat(event.value1, is("First value"))
    assertThat(event.value2, is("Second value"))
    assertThat(event.value3, nullValue())
  }

  @Test
  def apply_StringOnSuperClass() {

    abstract class SuperClassEvent extends ReflectiveSodaEvent {
      var value1: String = null
    }
    class SubClassEvent extends SuperClassEvent {
      var value2: String = null
      def apply() = { }
    }

    val event = new SubClassEvent

    event.apply(Map("value1" -> "First value", "value2" -> "Second value"))

    assertThat(event.value1, is("First value"))
    assertThat(event.value2, is("Second value"))
  }

  @Test
  def apply_OptionString() {

    val event = new Object() with ReflectiveSodaEvent {
      var value1: Option[String] = None
      var value2: Option[String] = None
      var value3: Option[String] = None

      def apply() = { }
    }
    event.apply(Map("value1" -> "First value", "value2" -> "Second value"))

    assertThat(event.value1, is(Some("First value").asInstanceOf[Option[String]]))
    assertThat(event.value2, is(Some("Second value").asInstanceOf[Option[String]]))
    assertThat(event.value3, is(None.asInstanceOf[Option[String]]))
  }

  @Test
  def apply_Int() {

    val event = new Object() with ReflectiveSodaEvent {
      var value1: Int = -1
      var value2: Int = -2
      var value3: Int = -3

      def apply() = { }
    }
    event.apply(Map("value1" -> "12", "value2" -> "34"))

    assertThat(event.value1, is(12))
    assertThat(event.value2, is(34))
    assertThat(event.value3, is(-3))
  }

  @Test
  def apply_OptionInt() {

    val event = new Object() with ReflectiveSodaEvent {
      var value1: Option[Int] = None
      var value2: Option[Int] = None
      var value3: Option[Int] = None

      def apply() = { }
    }
    event.apply(Map("value1" -> "12", "value2" -> "34"))

    assertThat(event.value1, is(Some(12).asInstanceOf[Option[Int]]))
    assertThat(event.value2, is(Some(34).asInstanceOf[Option[Int]]))
    assertThat(event.value3, is(None.asInstanceOf[Option[Int]]))
  }

  @Test
  def apply_UsingStringConstructor() {

    val event = new Object() with ReflectiveSodaEvent {
      var value1: ClassWithStringConstructor = null
      var value2: ClassWithStringConstructor = null
      var value3: ClassWithStringConstructor = null

      def apply() = { }
    }
    event.apply(Map("value1" -> "12", "value2" -> "34"))

    assertThat(event.value1.value, is(12))
    assertThat(event.value2.value, is(34))
    assertThat(event.value3, nullValue())
  }

  @Test
  def apply_OptionUsingStringConstructor() {

    val event = new Object() with ReflectiveSodaEvent {
      var value1: Option[ClassWithStringConstructor] = None
      var value2: Option[ClassWithStringConstructor] = None
      var value3: Option[ClassWithStringConstructor] = None

      def apply() = { }
    }
    event.apply(Map("value1" -> "12", "value2" -> "34"))

    assertThat(event.value1.get.value, is(12))
    assertThat(event.value2.get.value, is(34))
    assertThat(event.value3, is(None.asInstanceOf[Option[Any]]))
  }

  @Test
  def apply_UsingJavaBeanWithEditor() {
    val event = new Object() with ReflectiveSodaEvent {
      var value1: Amount = null
      var value2: Amount = null
      var value3: Amount = null

      def apply() = { }
    }
    event.apply(Map("value1" -> "12", "value2" -> "34"))

    assertThat(event.value1.value, is(12))
    assertThat(event.value2.value, is(34))
    assertThat(event.value3, nullValue())
  }

  @Test
  def apply_OptionUsingJavaBeanEditor() {
    val event = new Object() with ReflectiveSodaEvent {
      var value1: Option[Amount] = None
      var value2: Option[Amount] = None
      var value3: Option[Amount] = None

      def apply() = { }
    }
    event.apply(Map("value1" -> "12", "value2" -> "34"))

    assertThat(event.value1.get.value, is(12))
    assertThat(event.value2.get.value, is(34))
    assertThat(event.value3, is(None.asInstanceOf[Option[Amount]]))
  }

  @Test
  def apply_UsingCoercionRegister() {
    val event = new Object() with ReflectiveSodaEvent {
      val coercionRegister = new CoercionRegister(ClassWithIntConstructorCoercion)

      var value1: ClassWithIntConstructor = null
      var value2: ClassWithIntConstructor = null
      var value3: ClassWithIntConstructor = null

      def apply() = { }
    }
    event.apply(Map("value1" -> "12", "value2" -> "34"))

    assertThat(event.value1, is(ClassWithIntConstructor(12)))
    assertThat(event.value2, is(ClassWithIntConstructor(34)))
    assertThat(event.value3, nullValue())
  }

  @Test
  def apply_OptionUsingCoercionRegister() {
    val event = new Object() with ReflectiveSodaEvent {
      val coercionRegister = new CoercionRegister(ClassWithIntConstructorCoercion)

      var value1: Option[ClassWithIntConstructor] = None
      var value2: Option[ClassWithIntConstructor] = None
      var value3: Option[ClassWithIntConstructor] = None

      def apply() = { }
    }
    event.apply(Map("value1" -> "12", "value2" -> "34"))

    assertThat(event.value1.get, is(ClassWithIntConstructor(12)))
    assertThat(event.value2.get, is(ClassWithIntConstructor(34)))
    assertThat(event.value3, is(None.asInstanceOf[Option[ClassWithIntConstructor]]))
  }

  @Test
  def apply_ThrowExceptionForMissingParameter() {
    class EventWithOneParameter extends ReflectiveSodaEvent {
      var value1: String = null
      def apply() = { }
    }
    val event = new EventWithOneParameter

    try {
      event.apply(Map("value1" -> "First value", "value2" -> "Second value"))
      fail("Expecting ParameterBindingException")
    }
    catch {
      case e: ParameterBindingException => {
        e.bindFailures match {
          case List(failure) => {
            assertThat(failure.parameterName, is("value2"))
            assertThat(failure.parameterValue, is("Second value"))
            assertThat(failure.errorMessage, is("Parameter 'value2' could not be found on EventWithOneParameter$1 (org.sodatest.api.reflection.test)"))
            assertThat(failure.exception, is(None.asInstanceOf[Option[Throwable]]))
          }
          case _ => fail("Expecting one bind failure")
        }
      }
    }

  }

  @Test
  def apply_ThrowsParameterBindExceptionForFailureToConvertInt() {

    val event = new Object() with ReflectiveSodaEvent {
      var value1: Int = -1
      var value2: Int = -2
      var value3: Int = -3

      def apply() = { }
    }

    try {
      event.apply(Map("value1" -> "aa", "value2" -> "bb"))
      fail("Expecting ParameterBindingException")
    }
    catch {
      case pbe: ParameterBindingException => {
        pbe.bindFailures match {
          case List(failure1, failure2) => {
            assertThat(failure1.parameterName, is("value1"))
            assertThat(failure1.parameterValue, is("aa"))
            assertThat(failure1.errorMessage, is("org.sodatest.coercion.UnableToCoerceException: Unable to coerce value 'aa' to type java.lang.Integer: error invoking constructor public java.lang.Integer(java.lang.String) throws java.lang.NumberFormatException"))
            assertThat(failure1.exception.get, is(instanceOf(classOf[UnableToCoerceException])))
            assertThat(failure2.parameterName, is("value2"))
            assertThat(failure2.parameterValue, is("bb"))
            assertThat(failure2.errorMessage, is("org.sodatest.coercion.UnableToCoerceException: Unable to coerce value 'bb' to type java.lang.Integer: error invoking constructor public java.lang.Integer(java.lang.String) throws java.lang.NumberFormatException"))
            assertThat(failure2.exception.get, is(instanceOf(classOf[UnableToCoerceException])))
          }
          case _ => fail("Expecting one bind failure")
        }
      }
    }

    assertThat(event.value1, is(-1))
    assertThat(event.value2, is(-2))
    assertThat(event.value3, is(-3))
  }

}

class ClassWithStringConstructor(input: String) {
  private[test] val value = Integer.parseInt(input);
}

case class ClassWithIntConstructor(val value: Int)

object ClassWithIntConstructorCoercion extends Coercion[ClassWithIntConstructor] {
  def apply(s: String) = new ClassWithIntConstructor(Integer.parseInt(s))
}

class Amount() {
  private[test] var value: Int = -1;
  def changeValue(s: String) { value = Integer.parseInt(s) }
}

class AmountEditor() extends PropertyEditorSupport {
  override def getAsText = String.valueOf(getValue.asInstanceOf[Amount].value)
  override def setAsText(text: String) { getValue.asInstanceOf[Amount].changeValue(text) }
}


}}