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

package org.sodatest.api { package reflection {

import java.lang.reflect._
import collection.immutable.Map
import org.sodatest.coercion.{CoercionRegister, Coercion}

trait ReflectiveSodaFixture extends SodaFixture {
  import ReflectionUtil._

  def createEvent(name: String): Option[SodaEvent] = invokeNoParamFunctionReturning(classOf[SodaEvent], name, this)

  def createReport(name: String): Option[SodaReport] = invokeNoParamFunctionReturning(classOf[SodaReport], name, this)
}

trait ReflectiveSodaEvent extends SodaEvent {
  def apply(): Unit

  def apply(parameters: Map[String, String]) = {
    ReflectionUtil.setByReflection(parameters, this).apply();
  }
}

trait ReflectiveSodaReport extends SodaReport {
  def apply(): List[List[String]]

  def apply(parameters: Map[String, String]) = {
    ReflectionUtil.setByReflection(parameters, this).apply();
  }
}

private[reflection] object ReflectionUtil {

  def invokeNoParamFunctionReturning[A](requiredClass: Class[A], name: String, target: AnyRef) = {
    val searchName = canonizedName(name)
    val candidateMethods = target.getClass.getMethods.filter(m => { canonizedName(m.getName) == searchName && m.getParameterTypes.isEmpty }).toList
    candidateMethods match {
      case Nil => None
      case method :: Nil => {
        if (requiredClass.isAssignableFrom(method.getReturnType)) {
          Some(method.invoke(target).asInstanceOf[A])
        } else {
          throw new IllegalStateException(method.getName + " does not return a " + requiredClass.getSimpleName) // TODO: Throw something more specific, catch above and handle
        }
      }
      case _ => throw new IllegalArgumentException(requiredClass.getSimpleName + " name '" + name + "' (canonized to '" + searchName + "') matches more than one method: " + candidateMethods) // TODO: Throw something more specific, catch above and handle
    }

  }

  def setByReflection[A <: Object](parameters: Map[String, String], target: A): A = {
    setByReflection(withCanonicalKeyNames(parameters), target, coercionRegisterIn(target), target.getClass.getMethods.toList)
  }

  private def coercionRegisterIn(target: AnyRef) : Option[CoercionRegister] = {
    val coercionRegisterField = target.getClass.getDeclaredFields.toList.filter(_.getType == classOf[CoercionRegister]).firstOption
    coercionRegisterField match {
      case None => None
      case Some(f) => {
        f.setAccessible(true)
        Some(f.get(target).asInstanceOf[CoercionRegister])
      }
    }
  }

  private def withCanonicalKeyNames(map: Map[String, String]) = {
    map.map(p => (canonizedName(p._1), p._2))
  }

  def canonizedName(s: String) = s.toLowerCase.replaceAll("[^a-z0-9]", "")

  @scala.annotation.tailrec
  private def setByReflection[A <: Object](
      parameters: Map[String, String], target: A, coercionRegister: Option[CoercionRegister], methods: List[Method]): A = {
    methods match {
      case Nil => target
      case method :: moreMethods => {
        method match {
          case AssignmentMethod(fieldName, parameterType) => {
            parameters.get(canonizedName(fieldName)).foreach((value: String) => {
              method.setAccessible(true)
              method.invoke(target, Coercion.coerce(value, parameterType, coercionRegister).asInstanceOf[Object])
            })
          }
          case _ => // Ignore
        }
        setByReflection(parameters, target, coercionRegister, moreMethods)
      }
    }
  }

  private object AssignmentMethod {
    val assignMethodRegex = "^(.*)_\\$eq$".r

    def unapply(method: Method): Option[(String, Type)] = method.getGenericParameterTypes.toList match {
      case oneParameterType :: Nil => method.getName match {
        case assignMethodRegex(fieldName) => Some(fieldName, oneParameterType)
        case _ => None
      }
      case _ => None
    }
  }
}

class ReflectiveFieldSetException(message: String, cause: Throwable) extends RuntimeException(message, cause)

}}