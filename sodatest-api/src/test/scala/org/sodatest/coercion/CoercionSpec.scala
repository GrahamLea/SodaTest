// Copyright (c) 2010 Belmont Technology Pty Ltd. All rights reserved.

package org.sodatest.coercion { package test {

import java.lang.reflect.Type
import java.util.Arrays
import java.beans.PropertyEditorSupport
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
  val javaListString: java.util.List[String] = null
  val javaListInt: java.util.List[Int] = null
  val listOptionString: List[Option[String]] = Nil
  val optionListString: Option[List[String]] = None

  "Coercion" should {

    "coerce a String to the same string" in {
      Coercion.coerce("A String", classOf[String]) must_== "A String"
    }

    "coerce a number String to an Int" in {
      Coercion.coerce("123", classOf[Int]) must_== 123
    }

    "coerce to a String to an instance of a class with an <init>(String) constructor" in {
      Coercion.coerce("345", classOf[ClassWithStringConstructor]) must beLike {
        case o: ClassWithStringConstructor => o.value == 345
      }
    }

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

}}