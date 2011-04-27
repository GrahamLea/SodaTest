// Copyright (c) 2010 Belmont Technology Pty Ltd. All rights reserved.

package au.com.belmonttechnology.sodatest
package runtime.processing.running.testFixtures

import api.SodaFixture._
import api.{SodaReport, SodaEvent, SodaFixture}
import collection.immutable.TreeMap
import runtime.data.results.{ParameterBindingException, ParameterBindFailure}

class ReportTestFixture extends SodaFixture {

  private var lastExecutedEventName: Option[String] = None
  private var executedParameters: List[List[String]] = Nil

  def createEvent(name: String): Option[SodaEvent] = name match {
    case "Record data for report" => {
      lastExecutedEventName = None
      executedParameters = Nil
      new SodaEvent {
        def apply(parameters: Map[String, String]): Unit = {
          lastExecutedEventName = Some(name)
          executedParameters :+= (new TreeMap[String, String] ++ parameters).values.toList
        }
      }
    }
    case _ => None
  }

  def createReport(name: String): Option[SodaReport] = name match {
    case "Data collected from Events" => {
      new SodaReport {
        def apply(parameters: Map[String, String]): List[List[String]] = lastExecutedEventName match {
          case Some(eventName) => executedParameters
          case _ => List(List("NO EVENT EXECUTION"))
        }

      }
    }

    case "Data lines collected from Events" => {
      new SodaReport {

        def apply(parameters: Map[String, String]): List[List[String]] = {
          lastExecutedEventName match {

            case Some(eventName) => (parameters.get("First Line"), parameters.get("Last Line")) match {
              case Integers(first, last) => executedParameters.drop(first - 1).take(last - first + 1)
              case _ => throw new RuntimeException("Invalid parameters")
            }
            case _ => List(List("NO EVENT EXECUTION"))
          }
        }

        private object Integers {
          def unapply(firstAndLastStrings: (Option[String], Option[String])): Option[(Int, Int)] = {
            firstAndLastStrings match {
              case (Some(firstString), Some(lastString)) => {
                try { Some((firstString.toInt, lastString.toInt)) } catch {
                  case e => None
                }
              }
              case _ => None
            }
          }
        }
      }
    }

    case _ => {
      if (name.endsWith("bind exception")) {
        new SodaReport {
          def apply(parameters: Map[String, String]): List[List[String]] = {
            val bindFailures = parameters.flatMap((kv) => {
              if (kv._2 startsWith "Error")
                Some(new ParameterBindFailure(kv._1, kv._2, "This fixture is programmed to do this"))
              else
                None
            })

            bindFailures match {
              case head :: tail =>
                throw new ParameterBindingException(
                  new ParameterBindFailure(head.parameterName, head.parameterValue, head.errorMessage, Some(new RuntimeException("An exception causing a bind failure"))) :: tail)
              case _ => Nil
            }
          }
        }

      } else if (name endsWith "throwing exception") {
        new SodaReport {
          def apply(parameters: Map[String, String]): List[List[String]] = {
            throw new RuntimeException("Report exception from ReportTestFixture")
          }
        }
      } else {
        None
      }
    }
  }
}