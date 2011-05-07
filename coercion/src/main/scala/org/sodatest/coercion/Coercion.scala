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

import java.lang.reflect.{ParameterizedType, Type, Constructor}
import java.beans.PropertyEditor
import collection._
import mutable.ArrayBuffer

abstract class Coercion[A](implicit manifest: Manifest[A]) {
  // TODO: Pretty sure using 'erasure' here means that we can't be specific enough
  //       Test case is probably choosing between two coercions for different types having same generic type
  val resultClass = manifest.erasure
  def apply(s: String): A
}

class CoercionRegister(c: Coercion[_]*) {

  private var coercions: Map[Class[_], Coercion[_]] = Map()

  c.foreach(add(_))

  def add(c: Coercion[_]): Unit = { coercions += ((c.resultClass, c)) }

  def get[A](targetClass: Class[A]): Option[Coercion[A]] = { coercions.get(targetClass).asInstanceOf[Option[Coercion[A]]] }
}

object Coercion {

  private val wrapperTypes: Map[Class[_], Class[_]] = Map(
    java.lang.Boolean.TYPE ->   classOf[java.lang.Boolean],
    java.lang.Character.TYPE -> classOf[java.lang.Character],
    java.lang.Byte.TYPE ->      classOf[java.lang.Byte],
    java.lang.Short.TYPE ->     classOf[java.lang.Short],
    java.lang.Integer.TYPE ->   classOf[java.lang.Integer],
    java.lang.Long.TYPE ->      classOf[java.lang.Long],
    java.lang.Float.TYPE ->     classOf[java.lang.Float],
    java.lang.Double.TYPE ->    classOf[java.lang.Double]
  )

  def coerce(value: String, targetType: Type, register: CoercionRegister): Any = coerce(value, targetType, Some(register))

  def coerce(value: String, targetType: Type, register: Option[CoercionRegister] = None): Any = (targetType match {
    case c: Class[_] => coerceToClass(value, c)(register);
    case OptionType(wrappedType) =>
      if (value == null || value.trim.isEmpty) None else Some(coerce(value, wrappedType, register));
    case ScalaListType(wrappedType) => coerceCSV(value, wrappedType, register).toList;
    case JavaListType(wrappedType) => JavaConversions.asJavaList(ArrayBuffer(coerceCSV(value, wrappedType, register): _*));
  })

  private def coerceCSV(value: String, targetType: Type, register: Option[CoercionRegister] = None): Array[_] =
    if (value.trim == "") Array() else value.split(',').map(coerce(_, targetType, register))

  private abstract class GenericTypeMatcher(val rawType: Class[_]) {
    def unapply(t: Type): Option[Type] = t match {
      case p: ParameterizedType if (p.getRawType == rawType) => Some(p.getActualTypeArguments()(0))
      case _ => None
    }
  }

  private object OptionType extends GenericTypeMatcher(classOf[Option[_]])
  private object ScalaListType extends GenericTypeMatcher(classOf[List[_]])
  private object JavaListType extends GenericTypeMatcher(classOf[java.util.List[_]])

  def coerceToClass[A](value: String, targetType: Class[A])(implicit register: Option[CoercionRegister] = None): A = (targetType match {
    case StringClass(c) => value.asInstanceOf[A]
    case ClassWithCoercion(coercion) => coercion(value)
    case PrimitiveClass(wrapperClass) => coerce(value, wrapperClass).asInstanceOf[A]
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
      if (c == classOf[java.lang.String]) Some(c) else None
  }

  private object PrimitiveClass {
    def unapply[A](c: Class[A]): Option[Class[A]] =
      if (c.isPrimitive) wrapperTypes.get(c).asInstanceOf[Option[Class[A]]] else None
  }

  private object ClassWithCoercion {
    def unapply[A](c: Class[A])(implicit register: Option[CoercionRegister]): Option[Coercion[A]] = register match {
      case Some(r) => r.get(c)
      case None => None
    }
  }

  private object ClassWithStringConstructor {
    def unapply[A](c: Class[A]): Option[Constructor[A]] =
      try { Some(c.getConstructor(classOf[java.lang.String])) } catch { case _ => None }
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