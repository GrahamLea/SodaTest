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

package org.sodatest.runtime.data.results

class ExecutionError(val message: String, val causeString: Option[String] = None, val cause: Option[Throwable] = None) {
  def this(message: String, cause: String) = this(message, Some(cause))
  def this(message: String, cause: Option[Throwable]) = this(message, cause.map(_.toString), cause)
  def this(message: String, cause: Throwable) = this(message, Some(cause.toString), Some(cause))

  val causeTrace: Option[Array[StackTraceElement]] = cause.map(_.getStackTrace)

  override def toString = "ExecutionError: " + message + causeString.map(": " + _).getOrElse("")
}