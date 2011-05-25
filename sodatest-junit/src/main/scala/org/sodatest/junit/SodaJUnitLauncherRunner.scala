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
import org.junit.runners.model.InitializationError
import java.io.{FileNotFoundException, File}
import collection.JavaConversions
import org.junit.runner.Description
import org.junit.runner.notification.RunNotifier
import org.junit.internal.AssumptionViolatedException
import org.sodatest.runtime.processing.running.{SodaFileRunner, PathUtils}
import org.sodatest.runtime.processing.SodaTestContext
import java.util.Collections._
import org.junit.internal.runners.model.{MultipleFailureException, EachTestNotifier}
import java.lang.RuntimeException
import org.sodatest.runtime.data.results.{EventBlockResult, ReportBlockResult, SodaTestResult}

class SodaJUnitLauncherRunner(testClass: Class[_ <: SodaJUnitLauncherTestBase]) extends ParentRunner[File](testClass) {

  def getChildren: java.util.List[File] = {
    val baseDirName = testClass.getAnnotation(classOf[SodaJUnitLauncherBaseDir]) match {
      case null => throw new TestClassMissingAnnotationException("Tests to be run using SodaTestJUnit4Runner must be annotated with @SodaTestJunit4BaseDir");
      case annotation: SodaJUnitLauncherBaseDir => annotation.value
    }
    val filePattern = testClass.getAnnotation(classOf[SodaJUnitLauncherFilePattern]) match {
      case null => throw new TestClassMissingAnnotationException("Tests to be run using SodaTestJUnit4Runner must be annotated with @SodaTestJunit4FilePattern");
      case annotation: SodaJUnitLauncherFilePattern => annotation.value
    }

    val baseDir: File = new File(baseDirName)
    if (!baseDir.exists())
      throw new FileNotFoundException(baseDir.getAbsolutePath)

    val testSearchDir: File = new File(baseDir, testClass.getPackage.getName.replaceAll("\\.", "/"))
    if (!testSearchDir.exists())
      new FileNotFoundException(baseDir.getAbsolutePath)

    val filePatternRegex = filePattern.r
    JavaConversions.asJavaList(
      PathUtils.collectFilesRecursive(testSearchDir, file => {filePatternRegex.unapplySeq(file.getName) != None}))
  }

  private implicit def throwable2ThrowableList(t: Throwable): java.util.List[Throwable] = singletonList(t)

  def describeChild(child: File): Description =
    Description.createTestDescription(testClass,
      child.getParent match { case null => child.getName; case parent => child.getName + " (" + parent + ")"})

  def runChild(testFile: File, notifier: RunNotifier) {
    val eachNotifier: EachTestNotifier = new EachTestNotifier(notifier, describeChild(testFile))
    eachNotifier.fireTestStarted()
    try {
      val result: SodaTestResult = runTest(testFile)
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
    // TODO: Annotation for the fixture root
    // TODO: Hook into JUnit logging?
    implicit val context = new SodaTestContext(fixtureRoot = "org.sodatest.examples.junit.fixtures")
    SodaFileRunner.run(testFile)
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