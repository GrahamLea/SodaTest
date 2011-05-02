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

  @throws(classOf[ParameterBindingException])
  def apply(parameters: Map[String, String]) = {
    ReflectionUtil.setByReflection(parameters, this)
    apply()
  }
}

trait ReflectiveSodaReport extends SodaReport {
  def apply(): List[List[String]]

  @throws(classOf[ParameterBindingException])
  def apply(parameters: Map[String, String]) = {
    ReflectionUtil.setByReflection(parameters, this)
    apply()
  }
}

private[reflection] object ReflectionUtil {

  def invokeNoParamFunctionReturning[A](requiredClass: Class[A], name: String, target: Object) = {
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

  @throws(classOf[ParameterBindingException])
  def setByReflection(parameters: Map[String, String], target: Object): Unit = {
    setByReflection(parameters, target, coercionRegisterIn(target), target.getClass.getMethods.toList)
  }

  private def coercionRegisterIn(target: Object) : Option[CoercionRegister] = {
    val coercionRegisterField = target.getClass.getDeclaredFields.toList.filter(_.getType == classOf[CoercionRegister]).headOption
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

  @throws(classOf[ParameterBindingException])
  private def setByReflection(
      parameters: Map[String, String], target: Object, coercionRegister: Option[CoercionRegister], methods: List[Method]): Unit = {

    val assignmentMethodsMap: Map[String, Method] = methods.flatMap(m => m match {
          case AssignmentMethod(fieldName) => Some((canonizedName(fieldName), m))
          case _ => None
    }) toMap

    val bindFailures: Iterable[Option[ParameterBindFailure]] = for (val (parameterName, parameterValue) <- parameters) yield {
      assignmentMethodsMap.get(canonizedName(parameterName)) match {
        case None => Some(new ParameterBindFailure(parameterName, parameterValue,
                            String.format("Parameter '%s' could not be found on %s (%s)", parameterName, target.getClass.getSimpleName, target.getClass.getPackage.getName)))
        case Some(method) => {
          method.setAccessible(true)
          try {
            method.invoke(target, Coercion.coerce(parameterValue, getSingleParameterTypeFrom(method), coercionRegister).asInstanceOf[Object])
            None
          }
          catch {
            case e => Some(new ParameterBindFailure(parameterName, parameterValue, e.toString, Some(e)))
          }
        }
      }
    }

    bindFailures flatten match {
      case Nil => {}
      case failureList => throw new ParameterBindingException(failureList toList)
    }
  }

  private def getSingleParameterTypeFrom(method: Method): Type = method.getGenericParameterTypes.toList match {
    case oneParameterType :: Nil => oneParameterType
    case _ => error("Method does not have one parameter: " + method)
  }

  private object AssignmentMethod {
    val assignMethodRegex = "^(.*)_\\$eq$".r

    def unapply(method: Method): Option[String] = method.getGenericParameterTypes.toList match {
      case oneParameterType :: Nil => method.getName match {
        case assignMethodRegex(fieldName) => Some(fieldName)
        case _ => None
      }
      case _ => None
    }
  }
}

class ReflectiveFieldSetException(message: String, cause: Throwable) extends RuntimeException(message, cause)

}}