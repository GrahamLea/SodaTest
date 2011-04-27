// Copyright (c) 2010 Belmont Technology Pty Ltd. All rights reserved.

package au.com.belmonttechnology.sodatest.api

trait SodaFixture {
  def createEvent(name: String): Option[SodaEvent]
  def createReport(name: String): Option[SodaReport]
}

object SodaFixture {
  implicit def sodaEvent2Option(e: SodaEvent): Option[SodaEvent] = Some(e)
  implicit def sodaReport2Option(r: SodaReport): Option[SodaReport] = Some(r)

  implicit def function2EventOption(f: (Map[String, String]) => Unit): Option[SodaEvent] = new SodaEvent {
    def apply(parameters: Map[String, String]) = f(parameters)
  }

  implicit def function2ReportOption(f: (Map[String, String]) => List[List[String]]): Option[SodaReport] = new SodaReport {
    def apply(parameters: Map[String, String]) = f(parameters)
  }
}
