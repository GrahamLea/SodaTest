package au.com.belmonttechnology.sodatest.api.reflection
package test

// Copyright (c) 2010 Belmont Technology Pty Ltd. All rights reserved.

import java.lang.String
import org.hamcrest.CoreMatchers._
import collection.immutable.Map
import org.junit.Assert._
import org.junit.Test
import au.com.belmonttechnology.sodatest.api.{SodaEvent, SodaReport}
;

class ReflectiveSodaFixtureTest {

  @Test
  def createReport_vanilla() {
    val report = newMockReport()
    val fixture = new Object() with ReflectiveSodaFixture {
      def someReport = report
    }
    assertThat(fixture.createReport("someReport").get, is(sameInstance(report)))
  }

  @Test
  def createReport_differentCasing() {
    val report = newMockReport()
    val fixture = new Object() with ReflectiveSodaFixture {
      def someReport = report
    }
    assertThat(fixture.createReport("Some Report").get, is(sameInstance(report)))
  }

  @Test
  def createReport_wrongName() {
    val report = newMockReport()
    val fixture = new Object() with ReflectiveSodaFixture {
      def someReport = report
    }
    assertThat(fixture.createReport("Some Other Report"), is(None.asInstanceOf[Option[Object]]))
  }

  @Test
  def createReport_skipsNoneZeroParameterFunctions() {
    val fixture = new Object() with ReflectiveSodaFixture {
      def someReport(parameter: Int): SodaReport = throw new RuntimeException("This function shouldn't be called")
    }
    assertThat(fixture.createReport("Some Report"), is(None.asInstanceOf[Option[Object]]))
  }

  @Test
  def createReport_picksTheZeroParameterVariantOfAnOverloadedFunction() {
    val report1 = newMockReport()
    val report2 = newMockReport()
    val fixture = new Object() with ReflectiveSodaFixture {
      def someReport() = report1
      def someReport(parameter: Int) = report2
    }
    assertThat(fixture.createReport("Some Report").get, is(sameInstance(report1)))
  }

  @Test
  def createReport_wontCreateAnEvent() {
    val fixture = new Object() with ReflectiveSodaFixture {
      def someReport() = newMockEvent()
    }
    try {
      fixture.createReport("Some Report")
      fail("Expected Exception")
    } catch {
      case e: IllegalStateException => // OK
    }
  }

  @Test
  def createReport_numbersUsed() {
    val report1 = newMockReport()
    val report2 = newMockReport()
    val fixture = new Object() with ReflectiveSodaFixture {
      def someReport1 = report1
      def someReport2 = report2
    }
    assertThat(report1, is(not(sameInstance(report2))))
    assertThat(fixture.createReport("Some Report 1").get, is(sameInstance(report1)))
    assertThat(fixture.createReport("Some Report 2").get, is(sameInstance(report2)))
  }

  @Test
  def createEvent_vanilla() {
    val Event = newMockEvent()
    val fixture = new Object() with ReflectiveSodaFixture {
      def someEvent = Event
    }
    assertThat(fixture.createEvent("someEvent").get, is(sameInstance(Event)))
  }

  @Test
  def createEvent_differentCasing() {
    val Event = newMockEvent()
    val fixture = new Object() with ReflectiveSodaFixture {
      def someEvent = Event
    }
    assertThat(fixture.createEvent("Some Event").get, is(sameInstance(Event)))
  }

  @Test
  def createEvent_wrongName() {
    val Event = newMockEvent()
    val fixture = new Object() with ReflectiveSodaFixture {
      def someEvent = Event
    }
    assertThat(fixture.createEvent("Some Other Event"), is(None.asInstanceOf[Option[Object]]))
  }

  @Test
  def createEvent_skipsNoneZeroParameterFunctions() {
    val fixture = new Object() with ReflectiveSodaFixture {
      def someEvent(parameter: Int): SodaEvent = throw new RuntimeException("This function shouldn't be called")
    }
    assertThat(fixture.createEvent("Some Event"), is(None.asInstanceOf[Option[Object]]))
  }

  @Test
  def createEvent_picksTheZeroParameterVariantOfAnOverloadedFunction() {
    val Event1 = newMockEvent()
    val Event2 = newMockEvent()
    val fixture = new Object() with ReflectiveSodaFixture {
      def someEvent() = Event1
      def someEvent(parameter: Int) = Event2
    }
    assertThat(fixture.createEvent("Some Event").get, is(sameInstance(Event1)))
  }

  @Test
  def createEvent_wontCreateAnReport() {
    val fixture = new Object() with ReflectiveSodaFixture {
      def someEvent() = newMockReport()
    }
    try {
      fixture.createEvent("Some Event")
      fail("Expected Exception")
    } catch {
      case e: IllegalStateException => // OK
    }
  }

  @Test
  def createEvent_numbersUsed() {
    val Event1 = newMockEvent()
    val Event2 = newMockEvent()
    val fixture = new Object() with ReflectiveSodaFixture {
      def someEvent1 = Event1
      def someEvent2 = Event2
    }
    assertThat(Event1, is(not(sameInstance(Event2))))
    assertThat(fixture.createEvent("Some Event 1").get, is(sameInstance(Event1)))
    assertThat(fixture.createEvent("Some Event 2").get, is(sameInstance(Event2)))
  }

  private def newMockReport(): SodaReport =
    new Object() with SodaReport {
      def apply(parameters: Map[String, String]): List[List[String]] = throw new UnsupportedOperationException
    }

  private def newMockEvent(): SodaEvent =
    new Object() with SodaEvent {
      def apply(parameters: Map[String, String]): Unit = throw new UnsupportedOperationException
    }
}
