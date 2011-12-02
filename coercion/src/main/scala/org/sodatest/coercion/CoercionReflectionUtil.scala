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

package org.sodatest.coercion

import _root_.java.lang.reflect._
import collection.immutable.Map

class ReflectionTargetReturnsTheWrongTypeException(message: String) extends RuntimeException(message)
class NameMatchesMoreThanOneMethodException(message: String) extends RuntimeException(message)

object CoercionReflectionUtil {

  def invokeNoParamFunctionReturning[A](requiredClass: Class[A], name: String, target: Object) = {
    val searchName = canonizedName(name)
    val candidateMethods = target.getClass.getMethods.filter(m => { canonizedName(m.getName) == searchName && m.getParameterTypes.isEmpty }).toList
    candidateMethods match {
      case Nil => None
      case method :: Nil => {
        if (requiredClass.isAssignableFrom(method.getReturnType)) {
          Some(method.invoke(target).asInstanceOf[A])
        } else {
          throw new ReflectionTargetReturnsTheWrongTypeException("Function '" + method.getName + "' does not return a " + requiredClass.getSimpleName)
        }
      }
      case _ => {
        // Inline this if you'd like to crash the compiler...
        val methodListString = candidateMethods.map(method => {method.getDeclaringClass.getSimpleName + "." +  method.getName})
        throw new NameMatchesMoreThanOneMethodException(
          requiredClass.getSimpleName + " name '" + name + "' (canonized to '" + searchName + "') matches more than one method: " + methodListString)
      }
    }
  }

  @throws(classOf[CoercionBindingException])
  def setByReflection(parameters: Map[String, String], target: Object): Unit = {

    val coercionRegister = coercionRegisterIn(target).map(coercionOption => List(coercionOption)) // TODO Try to get one from the fixture too

    val assignmentMethodsMap: Map[String, Method] = target.getClass.getMethods.flatMap(m => m match {
          case AssignmentMethod(fieldName) => Some((canonizedName(fieldName), m))
          case _ => None
    }).toMap

    val fieldsMap: Map[String, Field] = target.getClass.getFields.toList.map(f => (canonizedName(f.getName), f)).toMap

    val bindFailures: Iterable[Option[CoercionBindFailure]] = for (val (parameterName, parameterValue) <- parameters) yield {
      val canonizedParameterName: String = canonizedName(parameterName)
      assignmentMethodsMap.get(canonizedParameterName) match {
        case Some(method) => {
          method.setAccessible(true)
          try {
            method.invoke(target, Coercion.coerce(parameterValue, getSingleParameterTypeFrom(method), coercionRegister).asInstanceOf[Object])
            None
          }
          catch {
            case e => Some(new CoercionBindFailure(parameterName, parameterValue, e.toString, Some(e)))
          }
        }
        case None => {
          fieldsMap.get(canonizedParameterName) match {
            case Some(field) => {
              field.setAccessible(true)
              try {
                field.set(target, Coercion.coerce(parameterValue, field.getGenericType, coercionRegister).asInstanceOf[Object])
                None
              }
              catch {
                case e => Some(new CoercionBindFailure(parameterName, parameterValue, e.toString, Some(e)))
              }
            }
            case None => Some(new CoercionBindFailure(parameterName, parameterValue,
                            String.format("Parameter '%s' could not be found on %s (%s)", parameterName, target.getClass.getSimpleName, target.getClass.getPackage.getName)))
          }
        }
      }
    }

    bindFailures flatten match {
      case Nil => {}
      case failureList => throw new CoercionBindingException(failureList toList)
    }
  }

  private def coercionRegisterIn(target: Object) : Option[CoercionRegister] = {
    target.getClass.getDeclaredFields.toList
      .filter(field => classOf[CoercionRegister].isAssignableFrom(field.getType))
      .headOption match {
        case None => None
        case Some(f) => {
          f.setAccessible(true)
          Some(f.get(target).asInstanceOf[CoercionRegister])
        }
      }
  }

  def canonizedName(s: String) = s.toLowerCase.replaceAll("[^a-z0-9]", "")

  private def getSingleParameterTypeFrom(method: Method): Type = method.getGenericParameterTypes.toList match {
    case oneParameterType :: Nil => oneParameterType
    case _ => error("Method does not have one parameter: " + method)
  }

  private object AssignmentMethod {
    val scalaAssignMethodRegex = "^(.*)_\\$eq$".r
    val setterMethodRegex = "^set(.*)$".r

    def unapply(method: Method): Option[String] = method.getGenericParameterTypes.toList match {
      case oneParameterType :: Nil => method.getName match {
        case scalaAssignMethodRegex(fieldName) => Some(fieldName)
        case setterMethodRegex(fieldName) => Some(fieldName)
        case _ => None
      }
      case _ => None
    }
  }
}

