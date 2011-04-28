/*
 * Copyright (c) 2011 Belmont Technology Pty Ltd. All rights reserved.
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

package org.sodatest.runtime.data.results

class ParameterBindFailure(
  val parameterName: String,
  val parameterValue: String,
  val errorMessage: String,
  val exception: Option[Throwable] = None
) {}

class ParameterBindingException(val bindFailures: List[ParameterBindFailure])
  extends RuntimeException

object ContainsParameterBindingException {
  def unapply(error: Option[ExecutionError]) = {
    error.flatMap(_.cause) match {
      case Some(e: ParameterBindingException) => Some(e)
      case _ => None
    }
  }
}