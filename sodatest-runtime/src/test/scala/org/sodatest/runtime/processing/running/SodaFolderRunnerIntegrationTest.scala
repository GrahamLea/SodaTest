// Copyright (c) 2010 Belmont Technology Pty Ltd. All rights reserved.

package org.sodatest.runtime.processing.running

import java.io.File
import org.junit.Assert._
import org.hamcrest.CoreMatchers._
import xml.factory.XMLLoader
import javax.xml.parsers.{SAXParserFactory, SAXParser}
import xml.{Text, Node, Elem}
import org.junit.{After, Before, Test}

class SodaFolderRunnerIntegrationTest extends XMLLoader[Elem] {

  val source = "src/test/resources/testSources/"
  val expectedResults = "src/test/resources/expectedResults/"
  val results = "target-soda-integration-test/testOutput/"
  val fixtureRoot = "org.sodatest.runtime.processing.running.testFixtures"
  val targetFolder = new File(results)

  @Before
  def setUnixPathsOnWindows() {
    System.setProperty("sodatest.outputUnixPathsOnWindows", "true")
  }

  @After
  def unsetUnixPathsOnWindows() {
    System.clearProperty("sodatest.outputUnixPathsOnWindows")
  }

  def checkOutputOf(test: String): Unit = {
    val testOutput = loadFile(new File(results + "/" + test + ".csv.html"))
    val expectedOutput = loadFile(new File(expectedResults + "/" + test + ".csv.html"))
//    assertThat(test, toStringWithoutStyle(testOutput), is(toStringWithoutStyle(expectedOutput)))
    assertEquals(test, toStringWithoutStyle(expectedOutput), toStringWithoutStyle(testOutput))
  }

  @Test
  def runFolderRunner() {
//    cleanOnShutdown

    SodaFolderRunner.main(Array(fixtureRoot, source, results))

    assertThat(targetFolder.exists, is(true))

    checkOutputOf("fixtureTests/GoodFixtureTest")
    checkOutputOf("fixtureTests/FixtureErrorsTest")

    checkOutputOf("eventTests/GoodEventsTest")
    checkOutputOf("eventTests/EventErrorsTest")

    checkOutputOf("reportTests/GoodReportsTest")
    checkOutputOf("reportTests/MismatchReportsTest")
    checkOutputOf("reportTests/ReportErrorsTest")
  }

  def toStringWithoutStyle(document: Node): String = {
    def f(n: Node): Node = {
      n match {
        case e: Elem => e.label match {
          case "style" => Text("")
          case "link" if (e.attribute("href") != None) => Text("")
          case "p" if (e.attribute("class") == Some(Text("stackTrace"))) => firstTwoLinesOf(e)
          case _ => e.copy(child = e.child.map(f(_)))
        }
        case a => a
      }
    }
    f(document).toString.trim.replaceAll("\n +", "\n").replaceAll("\n\n+", "\n")
  }

  private def cleanOnShutdown: Unit = {
    Runtime.getRuntime.addShutdownHook(new Thread(new Runnable() {
      def run {
        clean(new File("soda-target"))
      }

      def clean(f: File) {
        f.listFiles.filter(_.isDirectory).foreach(clean(_))
        f.listFiles.filter(!_.isDirectory).foreach(_.delete)
        f.listFiles.filter(_.isDirectory).foreach(_.delete)
      }
    }))
  }

  private def firstTwoLinesOf(e: Elem): Elem = {
    e.copy(child = Text(firstTwoLinesOf(e.text)))
  }

  private def firstTwoLinesOf(s: String): String = {
    val firstNewline = s.indexOf('\n')
    val secondNewline = s.indexOf('\n', firstNewline + 1)
    if (secondNewline > 0) s.substring(0, secondNewline) else s
  }

  override def parser: SAXParser = {
    val f = SAXParserFactory.newInstance()
    f.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
    f.setNamespaceAware(false)
    f.setValidating(false)
    f.newSAXParser()
  }
}

