// Copyright (c) 2011 Belmont Technology Pty Ltd. All rights reserved.

package au.com.belmonttechnology.sodatest.runtime.data.results

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