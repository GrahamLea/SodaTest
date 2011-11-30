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

package org.sodatest.runtime.processing.running

import java.io.File
import org.hamcrest.CoreMatchers._
import xml.factory.XMLLoader
import javax.xml.parsers.{SAXParserFactory, SAXParser}
import xml.{Text, Node, Elem}
import org.junit.{After, Before, Test}
import org.junit.Assert._
import java.lang.String

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
    val extension: String = if (test endsWith ".html") "" else ".csv.html"
    val testOutput = loadFile(new File(results + "/" + test + extension))
    val expectedOutput = loadFile(new File(expectedResults + "/" + test + extension))
    assertEquals(test, toStringWithExecutionDependentPartsRemoved(expectedOutput), toStringWithExecutionDependentPartsRemoved(testOutput))
  }

  @Test
  def runFolderRunner() {
    cleanOnShutdown()

    var success: Option[Boolean] = None
    SodaFolderRunner.main(Array(fixtureRoot, source, results), (b) => {success = Some(b)})
    assertThat(success.get, is(false))
      
    assertThat(targetFolder.exists, is(true))

    checkOutputOf("index.html")

    checkOutputOf("fixtureTests/GoodFixtureTest")
    checkOutputOf("fixtureTests/FixtureErrorsTest")

    checkOutputOf("eventTests/GoodEventsTest")
    checkOutputOf("eventTests/EventErrorsTest")

    checkOutputOf("reportTests/GoodReportsTest")
    checkOutputOf("reportTests/MismatchReportsTest")
    checkOutputOf("reportTests/ReportErrorsTest")
    
    checkOutputOf("noteTests/NotesTest")

    checkOutputOf("parseErrorTests/ParseErrorsTest")
  }

  def toStringWithExecutionDependentPartsRemoved(document: Node): String = {
    def f(n: Node): Node = {
      n match {
        case e: Elem => e.label match {
          case "style" => Text("")
          case "link" if (e.attribute("href") != None) => Text("")
          case "p" if (e.attribute("class") == Some(Text("stackTrace"))) => shortCleanStackTrace(e)
          case "h2" if (e.attribute("class") == Some(Text("timestamp"))) => <h2 class="timestamp">Timestamp Remove by Test</h2>
          case _ => e.copy(child = e.child.map(f(_)))
        }
        case a => a
      }
    }
    f(document).toString.trim.replaceAll("\n +", "\n").replaceAll("\n\n+", "\n")
  }

  private def cleanOnShutdown(): Unit = {
    Runtime.getRuntime.addShutdownHook(new Thread(new Runnable() {
      def run() {
        clean(targetFolder)
      }

      def clean(f: File) {
        f.listFiles.filter(_.isDirectory).foreach(clean(_))
        f.listFiles.filter(!_.isDirectory).foreach(_.delete)
        f.listFiles.filter(_.isDirectory).foreach(_.delete)
      }
    }))
  }

  private def shortCleanStackTrace(e: Elem): Elem = {
    e.copy(child = Text(firstTwoLinesOf(e.text.replaceAll(":[0-9]+\\)", ":)"))))  // Regex removes line numbers
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

