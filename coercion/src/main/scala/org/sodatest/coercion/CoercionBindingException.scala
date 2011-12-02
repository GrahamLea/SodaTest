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

/**
 * Details about the failure to bind a single parameter.
 */
class CoercionBindFailure(
  val parameterName: String,
  val parameterValue: String,
  val errorMessage: String,
  val exception: Option[Throwable] = None
) {}

/**
 * An exception indicating that one or more failures occurred while attempting to bind parameter
 * values to the parameters of a target object using [[CoercionReflectionUtil]].
 *
 * Rather than failing on the first occurrence of a bind failure, Events and Reports will typically
 * attempt to bind all parameters and then report all failures at once in a single CoercionBindingException.
 */
class CoercionBindingException(val bindFailures: List[CoercionBindFailure]) extends RuntimeException {
  override def toString = "CoercionBindingException: " + bindFailures.map(
    f => f.parameterName + ": " + f.parameterValue + "(" + f.errorMessage + ")").mkString(", ")
}



