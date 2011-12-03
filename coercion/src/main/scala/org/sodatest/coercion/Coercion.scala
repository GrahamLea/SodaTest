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

package org.sodatest {
package coercion {

import _root_.java.{lang => jl, beans => jb, util => ju}
import jl.reflect.Modifier
import jl.reflect.{ParameterizedType, Type, Constructor}
import jb.PropertyEditor
import collection._
import mutable.ArrayBuffer

/**
 * Superclass for types that can coerce a string into a 'strong type'/value object.
 */
abstract class Coercion[A](implicit manifest: Manifest[A]) {
  // TODO: Pretty sure using 'erasure' here means that we can't be specific enough
  //       Test case is probably choosing between two coercions for different types having same generic type
  val resultClass = manifest.erasure
  def apply(s: String): A
}

/**
 * Contains a map of Coercions that are applicable within some context.
 */
class CoercionRegister(coercions: Iterable[Coercion[_]]) {

  def this(coercions: Coercion[_]*) = this(coercions.toList)

  private val coercionMap: Map[Class[_], Coercion[_]] = Map(coercions.map(c => {(c.resultClass, c)}).toList:_*)

  /**
   * Returns a {Some[Coercion]} from this register capable of producing an instance of the specified class,
   * or {None} if no matching Coercion is registered. 
   */
  def get[A](targetClass: Class[A]): Option[Coercion[A]] = { coercionMap.get(targetClass).asInstanceOf[Option[Coercion[A]]] }

  /**
   * Creates and returns a new CoercionRegister containing all the Coercions of this instance as
   * well as the one provided as an argument.
   */
  def + (c: Coercion[_]): CoercionRegister = new CoercionRegister(c :: coercionMap.values.toList)

  /**
   * Creates and returns a new CoercionRegister containing all the Coercions of this instance as
   * well as all the coercions in the register passed in as an argument.
   */
  def ++ (cr: CoercionRegister): CoercionRegister = new CoercionRegister(cr.coercionMap.values.toList ::: coercionMap.values.toList)
}

/**
 * Functions for coercing string values into strongly typed value objects.
 *
 * Coercion attempts to use the following types of coercion (in order of highest precedence):
 * <ol>
 *   <li>Strings are returned unchanged</li>
 *   <li>Any applicable instance of [[org.sodatest.coercion.Coercion]] in the given [[org.sodatest.coercion.CoercionRegister]]</li>
 *   <li>The wrapper type for Java primitives</li>
 *   <li>A [[java.beans.PropertyEditor]] instance (having the name of the type with 'Editor' appended; the type must also have a no-args constructor)</li>
 *   <li>A constructor on the target type accepting one String.</li>
 * </ol>
 *
 * The first of these coercion types that is found to be applicable is applied and, if it failes,
 * an error is thrown immediately.
 *
 * Where the target type is an [[scala.Option]], Coercion will attempt to coerce any non-whitespacestring
 * to the type parameter of the Option, while empty or whitespace-only strings are coerced to [[scala.None]]
 *
 * Where the target type is a scala.collection.immutable.List or a java.util.List,
 * Coercion will split the string at each and every comma and attempt to coerce each split value to
 * the type parameter of the List. 
 */
object Coercion {

  implicit def function2Coercion[A](f: (String) => A)(implicit manifest: Manifest[A]): Coercion[A] =
    new Coercion[A] { def apply(s: String) = f(s) }

  private val wrapperTypes: Map[Class[_], Class[_]] = Map(
    jl.Boolean.TYPE ->   classOf[jl.Boolean],
    jl.Character.TYPE -> classOf[jl.Character],
    jl.Byte.TYPE ->      classOf[jl.Byte],
    jl.Short.TYPE ->     classOf[jl.Short],
    jl.Integer.TYPE ->   classOf[jl.Integer],
    jl.Long.TYPE ->      classOf[jl.Long],
    jl.Float.TYPE ->     classOf[jl.Float],
    jl.Double.TYPE ->    classOf[jl.Double]
  )

  /**
   * Coerce the given String value to the specified target Type, using any Coercions in the given
   * CoercionRegister, if applicable.
   */
  @throws(classOf[UnableToCoerceException])
  def coerce(value: String, targetType: Type, register: CoercionRegister): Any = coerce(value, targetType, List(register))

  /**
   * Coerce the given String value to the specified target Type, using any Coercions in the given
   * CoercionRegisters, if applicable.
   */
  @throws(classOf[UnableToCoerceException])
  def coerce(value: String, targetType: Type, registers: List[CoercionRegister]): Any = coerce(value, targetType, Some(registers))

  /**
   * Coerce the given String value to the specified target Type, using any Coercions in the given
   * CoercionRegister, if applicable.
   */
  @throws(classOf[UnableToCoerceException])
  def coerce(value: String, targetType: Type, registers: Option[List[CoercionRegister]] = None): Any = (targetType match {
    case c: Class[_] => coerceToClass(value, c)(registers);
    case OptionType(wrappedType) =>
      if (value == null || value.trim.isEmpty) None else Some(coerce(value, wrappedType, registers));
    case ScalaListType(wrappedType) => coerceCSV(value, wrappedType, registers).toList;
    case JavaListType(wrappedType) => JavaConversions.asJavaList(ArrayBuffer(coerceCSV(value, wrappedType, registers): _*));
  })

  private def coerceCSV(value: String, targetType: Type, registers: Option[List[CoercionRegister]] = None): Array[_] =
    if (value.trim == "") Array() else value.split(',').map(coerce(_, targetType, registers))

  private abstract class GenericTypeMatcher(val rawType: Class[_]) {
    def unapply(t: Type): Option[Type] = t match {
      case p: ParameterizedType if (p.getRawType == rawType) => Some(p.getActualTypeArguments()(0))
      case _ => None
    }
  }

  private object OptionType extends GenericTypeMatcher(classOf[Option[_]])
  private object ScalaListType extends GenericTypeMatcher(classOf[List[_]])
  private object JavaListType extends GenericTypeMatcher(classOf[ju.List[_]])

  /**
   * Coerce the given String value to the specified Class, using any Coercions in the given
   * CoercionRegister, if applicable.
   *
   * Unlike the version that accepts a type, this function will not attempt to perform special
   * handling for Options or Lists.
   *
   * @throws UnableToCoerceException if no applicable coercion method can be found for the type,
   * or if the first selected coercion method results in an error
   */
  @throws(classOf[UnableToCoerceException])
  def coerceToClass[A](value: String, targetType: Class[A])(implicit registers: Option[List[CoercionRegister]] = None): A = (targetType match {
    case ClassWithCoercion(coercion) => coercion(value)
    case StringClass(c) => value.asInstanceOf[A]
    case PrimitiveClass(wrapperClass) => coerce(value, wrapperClass).asInstanceOf[A]
    case JavaEnumClass(enumClass) => coerceToJavaEnum(value, enumClass).asInstanceOf[A]
//    case ScalaEnumClass(enumClass) => coerceToScalaEnum(value, enumClass).asInstanceOf[A]
    case ClassWithNoArgConstructorAndPropertyEditor(constructor, propertyEditorClass) =>
      coerceUsingPropertyEditor(value, constructor, propertyEditorClass, targetType)
    case ClassWithStringConstructor(constructor) =>
      coerceUsingStringConstructor(value, constructor, targetType)
    case _ => throw new UnableToCoerceException("No applicable coercion strategy available", value, targetType)
  })

  private def coerceUsingStringConstructor[A](value: String, constructor: Constructor[A], targetType: Class[A]): A = {
    try {
      constructor.newInstance(value)
    } catch {
      case e: Throwable =>
        throw new UnableToCoerceException("error invoking constructor " + constructor, value, targetType, Some(e))
    }
  }

  private def coerceToJavaEnum[A](value: String, enumClass: Class[A]): A = {
    val enumValues = enumClass.getDeclaredFields.toList
            .filter(f  => {
              (Modifier isPublic f.getModifiers) && (Modifier isStatic f.getModifiers) && (f.getType == enumClass)
            })

    enumValues.filter(_.getName == value) match {
      case matchingValue :: Nil => matchingValue.get(null).asInstanceOf[A]
      case _ => {
        val canonisedValue = canonisedEnumName(value)
        enumValues.filter(f => {canonisedEnumName(f.getName) == canonisedValue}) match {
          case matchingValue :: Nil => matchingValue.get(null).asInstanceOf[A]
          case matchingValue :: moreMatchingValues =>
            throw new UnableToCoerceException(
              "Multiple enum values match the input when canonised: " + (matchingValue :: moreMatchingValues),
              value, enumClass)
          case Nil =>
            throw new UnableToCoerceException("No matching enum values", value, enumClass)

        }
      }
    }
  }

  private def coerceToScalaEnum[A](value: String, enumClass: Class[A]): A = {
    println("enumClass = " + enumClass)

    val enumValues = enumClass.getDeclaredFields.toList
            .filter(f  => {
              (Modifier isPublic f.getModifiers) && (Modifier isStatic f.getModifiers) && (f.getType == enumClass)
            })

    enumValues.filter(_.getName == value) match {
      case matchingValue :: Nil => matchingValue.get(null).asInstanceOf[A]
      case _ => {
        val canonisedValue = canonisedEnumName(value)
        enumValues.filter(f => {canonisedEnumName(f.getName) == canonisedValue}) match {
          case matchingValue :: Nil => matchingValue.get(null).asInstanceOf[A]
          case matchingValue :: moreMatchingValues =>
            throw new UnableToCoerceException(
              "Multiple enum values match the input when canonised: " + (matchingValue :: moreMatchingValues),
              value, enumClass)
          case Nil =>
            throw new UnableToCoerceException("No matching enum values", value, enumClass)

        }
      }
    }
  }

  private def canonisedEnumName(s: String): String = {
    s.toLowerCase.replaceAll("_", "").replaceAll(" ", "")
  }

  private def coerceUsingPropertyEditor[A](value: String, constructor: Constructor[A], propertyEditorClass: Class[_ <: PropertyEditor], targetType: Class[A]): A = {
    def error(attemptedAction: String, e: Throwable) =
      new UnableToCoerceException("failed to " + attemptedAction + " PropertyEditor (" + propertyEditorClass.getSimpleName + ")", value, targetType, Some(e))

    var propertyEditor: PropertyEditor = null
    try { propertyEditor = propertyEditorClass.newInstance }
    catch { case e: Throwable => throw error("create", e) }

    var initialValue: A = null.asInstanceOf[A]
    try { initialValue = constructor.newInstance() }
    catch { case e: Throwable => throw new UnableToCoerceException("error invoking constructor " + constructor, value, targetType, Some(e)) }

    try { propertyEditor.setValue(initialValue) }
    catch { case e: Throwable => throw error("set initial value into", e) }

    try { propertyEditor.setAsText(value) }
    catch {case e: Throwable => throw error("set text value of", e) }

    try { propertyEditor.getValue().asInstanceOf[A] }
    catch { case e: Throwable => throw error("get value from", e) }
  }

  private object StringClass {
    def unapply[A](c: Class[A]): Option[Class[A]] =
      if (c == classOf[jl.String]) Some(c) else None
  }

  private object PrimitiveClass {
    def unapply[A](c: Class[A]): Option[Class[A]] =
      if (c.isPrimitive) wrapperTypes.get(c).asInstanceOf[Option[Class[A]]] else None
  }

  private object JavaEnumClass {
    def unapply[A](c: Class[A]): Option[Class[A]] =
      if (classOf[Enum[_]].isAssignableFrom(c)) Some(c) else None
  }

  private object ScalaEnumClass {
    def unapply[A](c: Class[A]): Option[Class[A]] =
      if (classOf[Enumeration#Value].isAssignableFrom(c)) Some(c) else None
  }

  private object ClassWithCoercion {
    def unapply[A](c: Class[A])(implicit register: Option[List[CoercionRegister]]): Option[Coercion[A]] = register match {
      case Some(registerList) => registerList.flatMap({_ get c}).headOption
      case None => None
    }
  }

  private object ClassWithStringConstructor {
    def unapply[A](c: Class[A]): Option[Constructor[A]] =
      try { Some(c.getConstructor(classOf[jl.String])) } catch { case _ => None }
  }

  private object ClassWithNoArgConstructorAndPropertyEditor {
    def unapply[A](c: Class[A]): Option[(Constructor[A], Class[_ <: PropertyEditor])] =
      try {
        Class.forName(c.getName + "Editor") match {
          case editorClass if (classOf[PropertyEditor].isAssignableFrom(editorClass)) =>
            Some((c.getConstructor(), editorClass.asInstanceOf[Class[PropertyEditor]]))
          case _ => None
        }
      } catch { case _ => None }
  }
}

class UnableToCoerceException(val reason: String, val value: String, val targetType: Class[_], cause: Option[Throwable] = None)
  extends RuntimeException("Unable to coerce value '" + value + "' to type " + targetType.getName + ": " + reason, cause.getOrElse(null))

}}
