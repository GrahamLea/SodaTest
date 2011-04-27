// Copyright (c) 2010 Belmont Technology Pty Ltd. All rights reserved.

package au.com.belmonttechnology.sodatest.runtime.data.results

class ExecutionError(val message: String, val causeString: Option[String] = None, val cause: Option[Throwable] = None) {
  def this(message: String, cause: String) = this(message, Some(cause))
  def this(message: String, cause: Throwable) = this(message, Some(cause.toString), Some(cause))

  val causeTrace: Option[Array[StackTraceElement]] = cause.map(_.getStackTrace)
}