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

package org.sodatest
package runtime.processing.running.testFixtures

import api.SodaFixture._
import api.{SodaReport, SodaEvent, SodaFixture}
import collection.immutable.TreeMap
import runtime.data.results.{ParameterBindingException, ParameterBindFailure}

class EventTestFixture extends SodaFixture {

  private var lastExecutedEventName: Option[String] = None
  private var executedParameters: List[Map[String, String]] = Nil

  def createEvent(name: String): Option[SodaEvent] = {
    if (name startsWith "Non-existent") {
      None
    }
    else {
      lastExecutedEventName = None
      executedParameters = Nil
      new SodaEvent {
        def apply(parameters: Map[String, String]): Unit = {
          if (name endsWith "bind exception") {
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
              case _ => ;
            }
          }

          if (name endsWith "throwing exception") {
            throw new RuntimeException("Event exception from EventTestFixture")
          }

          lastExecutedEventName = Some(name)
          executedParameters :+= parameters
        }
      }
    }
  }

  def createReport(name: String): Option[SodaReport] = name match {
    case "Last executed event" => {
      new SodaReport {

        def apply(parameters: Map[String, String]): List[List[String]] = (lastExecutedEventName, executedParameters) match {
          case (Some(eventName), parameterMapList) => {
            val sortedMaps = parameterMapList.map(new TreeMap[String, String]() ++ _)
            var i: Int = 1;
            val a: List[List[String]] = List(
              List("Event name:", eventName),
              "Parameters:" :: sortedMaps(0).keys.toList
            )
            val b: List[List[String]] = (for (val execution <- sortedMaps) yield {
              "Execution #" + {i += 1; i - 1} + ":" :: execution.values.toList
            })
            a ::: b
          }
          case _ => List(List("NO EVENT EXECUTION"))
        }

      }
    }
    case _ => None
  }
}