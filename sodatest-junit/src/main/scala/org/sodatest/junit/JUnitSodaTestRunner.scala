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

package org.sodatest.junit

import org.junit.runners.ParentRunner
import collection.JavaConversions
import org.junit.runner.Description
import org.junit.runner.notification.RunNotifier
import org.junit.internal.AssumptionViolatedException
import org.sodatest.runtime.processing.SodaTestContext
import java.util.Collections._
import org.junit.internal.runners.model.{MultipleFailureException, EachTestNotifier}
import java.lang.RuntimeException
import org.sodatest.runtime.data.results.{EventBlockResult, ReportBlockResult, SodaTestResult}
import org.junit.runners.model.{Statement, InitializationError}
import org.sodatest.runtime.processing.formatting.xhtml.XhtmlSodaTestResultWriter
import org.sodatest.runtime.processing.running.{SodaFileRunner, PathUtils}
import org.sodatest.runtime.ConsoleLog
import java.io.{IOException, FileNotFoundException, File}

/**
 * A JUnit runner for executing SodaTests. You should not use this Runner directly, but should
 * instead create a subclass of {org.sodatest.junit.JUnitSodaTestLauncherTestBase}.
 */
class JUnitSodaTestRunner(testClass: Class[_ <: JUnitSodaTestLauncherTestBase]) extends ParentRunner[File](testClass) {

  private val log = new ConsoleLog(ConsoleLog.Level.Debug)
  private val baseDirName = testClass.getAnnotation(classOf[JUnitSodaTestLauncherBaseDir]).value
  private val filePattern = testClass.getAnnotation(classOf[JUnitSodaTestLauncherFilePattern]).value
  private val outputDirName = testClass.getAnnotation(classOf[JUnitSodaTestLauncherOutputDirectory]).value
  private val fixtureRoot = testClass.getAnnotation(classOf[JUnitSodaTestLauncherFixtureRoot]) match {
    case null => {
      log.debug("No JUnitSodaTestLauncherFixtureRoot annotation found. Using the test class' package of '" + testClass.getPackage.getName + "' as the fixture root.")
      testClass.getPackage.getName
    }
    case a: JUnitSodaTestLauncherFixtureRoot => a.value
  }

  private val baseDir = new File(baseDirName)
  if (!baseDir.exists())
    throw new FileNotFoundException(baseDir.getAbsolutePath)

  private val outputDir = new File(outputDirName)
  if (!outputDir.exists() && !outputDir.mkdirs())
    throw new InitializationError("Failed to create output directory " + outputDir.getAbsolutePath)

  private val testSearchDir: File = new File(baseDir, testClass.getPackage.getName.replaceAll("\\.", "/"))
  if (!testSearchDir.exists())
    new FileNotFoundException(baseDir.getAbsolutePath)

  private val filePatternRegex = filePattern.r

  private var results: List[(File, SodaTestResult)] = Nil

  private implicit val context = new SodaTestContext(fixtureRoot, log)

  def getChildren: java.util.List[File] = {
    if (!testSearchDir.exists)
      throw new FileNotFoundException("SodaTest search path does not exist: " + testSearchDir.getAbsolutePath)
    if (!testSearchDir.isDirectory)
      throw new IOException("SodaTest search path is not a directory: " + testSearchDir.getAbsolutePath)
    val files = PathUtils.collectFilesRecursive(testSearchDir, file => {filePatternRegex.unapplySeq(file.getName) != None})
    XhtmlSodaTestResultWriter.createOutputDirectories(baseDir, files, outputDir)
    JavaConversions.asJavaList(files)
  }

  private implicit def throwable2ThrowableList(t: Throwable): java.util.List[Throwable] = singletonList(t)

  def describeChild(child: File): Description =
    Description.createTestDescription(testClass,
      child.getParent match { case null => child.getName; case parent => child.getName + " (" + parent + ")"})

  override protected def childrenInvoker(notifier: RunNotifier): Statement = {
    val superInvoker: Statement = super.childrenInvoker(notifier)
    new Statement {
      def evaluate(): Unit = {
        superInvoker.evaluate()
        XhtmlSodaTestResultWriter.writeResultsFiles(results, baseDir, outputDir)
      }
    }
  }

  def runChild(testFile: File, notifier: RunNotifier) {
    val eachNotifier: EachTestNotifier = new EachTestNotifier(notifier, describeChild(testFile))
    eachNotifier.fireTestStarted()
    try {
      val result: SodaTestResult = runTest(testFile)
      results = results :+ (testFile, result)
      if (!result.passed) {
        val errors: List[Option[List[Throwable]]] = result.blockResults.map(r => {
          if (r.succeeded) None.asInstanceOf[Option[List[Throwable]]]
          else Some(r.blockError match {
            case Some(blockError) => List(new ExecutionErrorException(blockError.toString))
            case None => r match {
              case rbr: ReportBlockResult => reportBlockErrors(rbr)
              case ebr: EventBlockResult => eventBlockErrors(ebr)
              case _ => Nil
            }
          })
        })
        eachNotifier.addFailure(new MultipleFailureException(JavaConversions.asJavaList(errors.flatten.flatten)))
      }
    }
    catch {
      case e: AssumptionViolatedException => eachNotifier.addFailedAssumption(e)
      case e: Throwable => eachNotifier.addFailure(e)
    }
    finally { eachNotifier.fireTestFinished() }
  }

  private def runTest(testFile: File): SodaTestResult = {
    SodaFileRunner.runTest(testFile)
  }

  private def reportBlockErrors(rbr: ReportBlockResult): List[Throwable] = {
    rbr.executionResults.map(rer => {
      rer.error match {
        case Some(error) => new ExecutionErrorException(error.toString)
        case None if !rer.matchResult.passed =>
          new ReportMatchFailureException("Report match failure in '" + rbr.block.name + "' at line " +
            (rer.execution.parameterValues match {
              case Some(line) => line.lineNumber;
              case _ => rbr.block.source.lines(0).lineNumber
            }))
        case _ => throw new RuntimeException("Should never get here")
      }
    })
  }

  private def eventBlockErrors(ebr: EventBlockResult): List[Throwable] =
    ebr.executionResults.map(_.error.map(e => new ExecutionErrorException(e.toString))).flatten

}

class TestClassMissingAnnotationException(message: String) extends InitializationError(message)
class ReportMatchFailureException(message: String) extends java.lang.AssertionError(message)
class ExecutionErrorException(message: String) extends java.lang.AssertionError(message)