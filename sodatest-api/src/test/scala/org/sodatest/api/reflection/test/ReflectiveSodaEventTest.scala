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

import org.sodatest.coercion.Coercion
import org.sodatest.coercion.CoercionRegister
import java.beans.PropertyEditorSupport
import java.lang.String
import org.hamcrest.core.Is._
import org.hamcrest.core.IsNull._
import collection.immutable.Map
import org.junit.Assert._
import org.junit.Test
;

class ReflectiveSodaEventTest {

  @Test
  def apply_String() {

    val action = new Object() with ReflectiveSodaEvent {
      var value1: String = null
      var value2: String = null
      var value3: String = null

      def apply() = { }
    }
    action.apply(Map("value1" -> "First value", "value2" -> "Second value"))

    assertThat(action.value1, is("First value"))
    assertThat(action.value2, is("Second value"))
    assertThat(action.value3, nullValue())
  }

  @Test
  def apply_OptionString() {

    val action = new Object() with ReflectiveSodaEvent {
      var value1: Option[String] = None
      var value2: Option[String] = None
      var value3: Option[String] = None

      def apply() = { }
    }
    action.apply(Map("value1" -> "First value", "value2" -> "Second value"))

    assertThat(action.value1, is(Some("First value").asInstanceOf[Option[String]]))
    assertThat(action.value2, is(Some("Second value").asInstanceOf[Option[String]]))
    assertThat(action.value3, is(None.asInstanceOf[Option[String]]))
  }

  @Test
  def apply_Int() {

    val action = new Object() with ReflectiveSodaEvent {
      var value1: Int = -1
      var value2: Int = -2
      var value3: Int = -3

      def apply() = { }
    }
    action.apply(Map("value1" -> "12", "value2" -> "34"))

    assertThat(action.value1, is(12))
    assertThat(action.value2, is(34))
    assertThat(action.value3, is(-3))
  }

  @Test
  def apply_OptionInt() {

    val action = new Object() with ReflectiveSodaEvent {
      var value1: Option[Int] = None
      var value2: Option[Int] = None
      var value3: Option[Int] = None

      def apply() = { }
    }
    action.apply(Map("value1" -> "12", "value2" -> "34"))

    assertThat(action.value1, is(Some(12).asInstanceOf[Option[Int]]))
    assertThat(action.value2, is(Some(34).asInstanceOf[Option[Int]]))
    assertThat(action.value3, is(None.asInstanceOf[Option[Int]]))
  }

  @Test
  def apply_UsingStringConstructor() {

    val action = new Object() with ReflectiveSodaEvent {
      var value1: ClassWithStringConstructor = null
      var value2: ClassWithStringConstructor = null
      var value3: ClassWithStringConstructor = null

      def apply() = { }
    }
    action.apply(Map("value1" -> "12", "value2" -> "34"))

    assertThat(action.value1.value, is(12))
    assertThat(action.value2.value, is(34))
    assertThat(action.value3, nullValue())
  }

  @Test
  def apply_OptionUsingStringConstructor() {

    val action = new Object() with ReflectiveSodaEvent {
      var value1: Option[ClassWithStringConstructor] = None
      var value2: Option[ClassWithStringConstructor] = None
      var value3: Option[ClassWithStringConstructor] = None

      def apply() = { }
    }
    action.apply(Map("value1" -> "12", "value2" -> "34"))

    assertThat(action.value1.get.value, is(12))
    assertThat(action.value2.get.value, is(34))
    assertThat(action.value3, is(None.asInstanceOf[Option[Any]]))
  }

  @Test
  def apply_UsingJavaBeanWithEditor() {
    val action = new Object() with ReflectiveSodaEvent {
      var value1: Amount = null
      var value2: Amount = null
      var value3: Amount = null

      def apply() = { }
    }
    action.apply(Map("value1" -> "12", "value2" -> "34"))

    assertThat(action.value1.value, is(12))
    assertThat(action.value2.value, is(34))
    assertThat(action.value3, nullValue())
  }

  @Test
  def apply_OptionUsingJavaBeanEditor() {
    val action = new Object() with ReflectiveSodaEvent {
      var value1: Option[Amount] = None
      var value2: Option[Amount] = None
      var value3: Option[Amount] = None

      def apply() = { }
    }
    action.apply(Map("value1" -> "12", "value2" -> "34"))

    assertThat(action.value1.get.value, is(12))
    assertThat(action.value2.get.value, is(34))
    assertThat(action.value3, is(None.asInstanceOf[Option[Amount]]))
  }

  @Test
  def apply_UsingCoercionRegister() {
    val action = new Object() with ReflectiveSodaEvent {
      val coercionRegister = new CoercionRegister(ClassWithIntConstructorCoercion)

      var value1: ClassWithIntConstructor = null
      var value2: ClassWithIntConstructor = null
      var value3: ClassWithIntConstructor = null

      def apply() = { }
    }
    action.apply(Map("value1" -> "12", "value2" -> "34"))

    assertThat(action.value1, is(ClassWithIntConstructor(12)))
    assertThat(action.value2, is(ClassWithIntConstructor(34)))
    assertThat(action.value3, nullValue())
  }

  @Test
  def apply_OptionUsingCoercionRegister() {
    val action = new Object() with ReflectiveSodaEvent {
      val coercionRegister = new CoercionRegister(ClassWithIntConstructorCoercion)

      var value1: Option[ClassWithIntConstructor] = None
      var value2: Option[ClassWithIntConstructor] = None
      var value3: Option[ClassWithIntConstructor] = None

      def apply() = { }
    }
    action.apply(Map("value1" -> "12", "value2" -> "34"))

    assertThat(action.value1.get, is(ClassWithIntConstructor(12)))
    assertThat(action.value2.get, is(ClassWithIntConstructor(34)))
    assertThat(action.value3, is(None.asInstanceOf[Option[ClassWithIntConstructor]]))
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