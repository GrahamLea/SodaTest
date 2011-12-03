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

package org.sodatest.coercion { package test {

import _root_.java.{lang => jl, util => ju, beans => jb}
import jl.reflect.Type
import ju.Arrays
import jb.PropertyEditorSupport
import org.specs.SpecificationWithJUnit

class CoercionSpec extends SpecificationWithJUnit {

  val optionString: Option[String] = None
  val optionOptionString: Option[Option[String]] = None
  val optionInt: Option[Int] = None
  val optionStringConstructor: Option[ClassWithStringConstructor] = None
  val optionAmount: Option[Amount] = None
  val optionWithIntConstructorAndCoercion: Option[ClassWithIntConstructor] = None
  val listString: List[String] = Nil
  val listInt: List[Int] = Nil
  val javaListString: ju.List[String] = null
  val javaListInt: ju.List[Int] = null
  val listOptionString: List[Option[String]] = Nil
  val optionListString: Option[List[String]] = None

  "Coercion" should {

    "coerce a String to the same string" in {
      Coercion.coerce("A String", classOf[String]) must_== "A String"
    }

    "coerce a number String to an Int" in {
      Coercion.coerce("123", classOf[Int]) must_== 123
    }

    "coerce a String to an instance of a class with an <init>(String) constructor" in {
      Coercion.coerce("345", classOf[ClassWithStringConstructor]) must beLike {
        case o: ClassWithStringConstructor => o.value == 345
      }
    }

    "coerce a String to an instance of a Java enum" in {
      Coercion.coerce("FirstValue", classOf[TestJavaEnum]) must beLike {
        case e: TestJavaEnum => e == TestJavaEnum.FirstValue
      }
    }

    "coerce a String with extra space and different case to an instance of a Java enum" in {
      Coercion.coerce("first value", classOf[TestJavaEnum]) must beLike {
        case e: TestJavaEnum => e == TestJavaEnum.FirstValue
      }
    }

    "coerce a String without underscores to an instance of a Java enum whose name has underscores" in {
      Coercion.coerce("SecondValue", classOf[TestJavaEnum]) must beLike {
        case e: TestJavaEnum => e == TestJavaEnum.SECOND_VALUE
      }
    }

//    "coerce a String to an instance of a Scala enum" in {
//      import TestScalaEnum._
//      println("TestScalaEnum.FirstValue = " + FirstValue)
//      println("TestScalaEnum.FirstValue.getClass = " + FirstValue.getClass)
//      println("TestScalaEnum.FirstValue.getClass = " + classOf[TestScalaEnum])
//      Coercion.coerce("FirstValue", classOf[TestScalaEnum]) must beLike {
//        case e: TestScalaEnum.Value => e == TestScalaEnum.FirstValue
//      }
//    }
//
//    "coerce a String with extra space and different case to an instance of a Scala enum" in {
//      Coercion.coerce("first value", classOf[TestScalaEnum.Value]) must beLike {
//        case e: TestScalaEnum.Value => e == TestScalaEnum.FirstValue
//      }
//    }
//
//    "coerce a String without underscores to an instance of a Scala enum whose name has underscores" in {
//      Coercion.coerce("SecondValue", classOf[TestScalaEnum.Value]) must beLike {
//        case e: TestScalaEnum.Value => e == TestScalaEnum.SECOND_VALUE
//      }
//    }

    "coerce a String to a JavaBean using a PropertyEditor class discovered by appending 'Editor' to the target class name" in {
      Coercion.coerce("567", classOf[Amount]) must beLike {
        case a: Amount => a.value == 567
      }
    }

    "coerce using a Coercion implementation in a provided CoercionRegister" in {
      Coercion.coerce("789", classOf[ClassWithIntConstructor], new CoercionRegister(ClassWithIntConstructorCoercion)) must beLike {
        case o: ClassWithIntConstructor => o.value == 789
      }
    }

    "coerce using a Coercion implementation in a second provided CoercionRegister" in {
      Coercion.coerce("789", classOf[ClassWithIntConstructor], List(new CoercionRegister(), new CoercionRegister(ClassWithIntConstructorCoercion))) must beLike {
        case o: ClassWithIntConstructor => o.value == 789
      }
    }

    "provide an implicit Function to Coercion converter function for use with CoercionRegister" in {
      Coercion.coerce("789", classOf[ClassWithIntConstructor], new CoercionRegister((s: String) => {new ClassWithIntConstructor(s.toInt)})) must beLike {
        case o: ClassWithIntConstructor => o.value == 789
      }
    }

    "coerce a String to a Some(String)" in {
      Coercion.coerce("A String", typeOf("optionString")) must_== Some("A String")
    }

    "coerce a String to an Option[Option[String]]" in {
      Coercion.coerce("A String", typeOf("optionOptionString")) must_== Some(Some("A String"))
    }

    "coerce a number String to an Option[Int]" in {
      Coercion.coerce("234", typeOf("optionInt")) must_== Some(234)
    }

    "coerce to an Option around a class with a String constructor" in {
      Coercion.coerce("456", typeOf("optionStringConstructor")) must beLike {
        case Some(o: ClassWithStringConstructor) => o.value == 456
      }
    }

    "coerce to an Option around a JavaBean using a PropertyEditor" in {
      Coercion.coerce("678", typeOf("optionAmount")) must beLike {
        case Some(a: Amount) => a.value == 678 
      }
    }

    "coerce to an Option using a Coercion implementation in a provided CoercionRegister" in {
      Coercion.coerce("890", typeOf("optionWithIntConstructorAndCoercion"), new CoercionRegister(ClassWithIntConstructorCoercion)) must beLike {
        case Some(o: ClassWithIntConstructor) => o.value == 890
      }
    }
    
    "coerce an empty String to Option[_] as None" in {
      Coercion.coerce("", typeOf("optionString")) must_== None
      Coercion.coerce("", typeOf("optionInt")) must_== None
      Coercion.coerce("", typeOf("optionStringConstructor")) must_== None
      Coercion.coerce("", typeOf("optionAmount")) must_== None
      Coercion.coerce("", typeOf("optionWithIntConstructorAndCoercion")) must_== None
    }

    "coerce a String of whitespace to Option[_] as None" in {
      Coercion.coerce(" \t\n", typeOf("optionString")) must_== None
      Coercion.coerce(" \t\n \t\n", typeOf("optionInt")) must_== None
      Coercion.coerce(" \t\n", typeOf("optionStringConstructor")) must_== None
      Coercion.coerce("", typeOf("optionAmount")) must_== None
      Coercion.coerce("", typeOf("optionWithIntConstructorAndCoercion")) must_== None
    }

    "coerce a single String to a scala.List[String]" in {
      Coercion.coerce("A String", typeOf("listString")) must_== List("A String")
    }

    "coerce a single String to a java.util.List[String]" in {
      Coercion.coerce("A String", typeOf("javaListString")) must_== Arrays.asList("A String")
    }

    "coerce a comma-separated list of Strings to a scala.List[String]" in {
      Coercion.coerce("A String,Another String,One last String", typeOf("listString")) must beLike {
        case List("A String", "Another String", "One last String") => true
      }
    }

    "coerce a comma-separated list of Strings to a java.util.List[String]" in {
      Coercion.coerce("A String,Another String,One last String", typeOf("javaListString")) must_==
              Arrays.asList("A String", "Another String", "One last String")
    }

    "coerce a comma-separated list of number Strings to a scala.List[Int]" in {
      Coercion.coerce("123,234,345", typeOf("listInt")) must beLike { case List(123, 234, 345) => true }
    }

    "coerce a comma-separated list of number Strings to a java.util.List[Int]" in {
      Coercion.coerce("123,234,345", typeOf("javaListInt")) must_== Arrays.asList(123, 234, 345)
    }

    "coerce an empty string to a scala.List[Int] as Nil" in {
      Coercion.coerce("", typeOf("listInt")) must beLike { case Nil => true }
    }

    "coerce an empty string to a java.util.List[Int] as an empty list" in {
      Coercion.coerce("", typeOf("javaListInt")) must_== Arrays.asList[jl.Integer]()
    }

    "coerce a blank string to a scala.List[Int] as Nil" in {
      Coercion.coerce(" \t ", typeOf("listInt")) must beLike { case Nil => true }
    }

    "coerce a blank string to a java.util.List[Int] as an empty list" in {
      Coercion.coerce(" \t ", typeOf("javaListInt")) must_== Arrays.asList[Integer]()
    }

  }

  private def typeOf(fieldName: String): Type = {
    getClass.getDeclaredField(fieldName).getGenericType
  }

}

class ClassWithStringConstructor(input: String) {
  private[test] val value = Integer.parseInt(input);
}

class Amount() {
  private[test] var value: Int = -1;
  def changeValue(s: String) { value = Integer.parseInt(s) }
}

class AmountEditor() extends PropertyEditorSupport {
  override def getAsText = String.valueOf(getValue.asInstanceOf[Amount].value)
  override def setAsText(text: String) { getValue.asInstanceOf[Amount].changeValue(text) }
}


class ClassWithIntConstructor(val value: Int)

object ClassWithIntConstructorCoercion extends Coercion[ClassWithIntConstructor] {
  def apply(s: String) = new ClassWithIntConstructor(Integer.parseInt(s))
}

object TestScalaEnum extends Enumeration {
  type TestScalaEnum = Value
  val FirstValue, SECOND_VALUE = Value
}

}}

