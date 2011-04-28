// Copyright (c) 2010 Belmont Technology Pty Ltd. All rights reserved.

package org.sodatest { package runtime.execution.activity {

import api.SodaFixture

import java.lang.reflect.InvocationTargetException
import java.lang.Class
import runtime.processing.execution.SodaTestExecutionContext
import runtime.data.blocks.FixtureBlock
import runtime.data.results.{ExecutionError, FixtureBlockResult}

object FixtureBlockExecutor {

  def execute(context: SodaTestExecutionContext, fixtureBlock: FixtureBlock) : FixtureBlockResult = {
    implicit val f = fixtureBlock
    val fixtureRoot = context.testContext.properties.fixtureRoot
    val fixtureName = fixtureBlock.fixtureName
    val qualifiedFixtureName = if (fixtureRoot == "") fixtureName else fixtureRoot + "." + fixtureName
    println("qualifiedFixtureName: " + qualifiedFixtureName)

    def creationError(explanation: String, error: Throwable): FixtureBlockResult = new FixtureBlockResult(Some(new ExecutionError(explanation, error.toString)))
    
    def creationErrorWithTrace(explanation: String, error: Throwable): FixtureBlockResult = new FixtureBlockResult(Some(new ExecutionError(explanation, error)))

    try {
      val newFixture: SodaFixture = Class.forName(qualifiedFixtureName).asInstanceOf[Class[_ <: SodaFixture]].getDeclaredConstructor().newInstance()
      context.currentFixture = Some(newFixture)
      new FixtureBlockResult()
    } catch {
      case t => {
        context.currentFixture = None
        t match {
          case e: ClassNotFoundException =>     creationError("Fixture class not found", e)
          case e: IllegalAccessException =>     creationError("Couldn't access Fixture class or constructor: " + qualifiedFixtureName, e)
          case e: NoSuchMethodException =>      creationError("Couldn't find Fixture class no-arg constructor: " + qualifiedFixtureName, e)
          case e: ClassCastException =>         creationError("Class doesn't implement SodaFixture: " + qualifiedFixtureName, e)
          case e: InvocationTargetException =>  creationErrorWithTrace("Error while creating Fixture: " + qualifiedFixtureName, e.getCause)
          case e => throw new RuntimeException("Uncaught error while creating Fixture: " + qualifiedFixtureName + " : " + e + "\n!!! PLEASE REPORT THIS ERROR AS A BUG !!!", e)
        }
      }
    }
  }

}

}}