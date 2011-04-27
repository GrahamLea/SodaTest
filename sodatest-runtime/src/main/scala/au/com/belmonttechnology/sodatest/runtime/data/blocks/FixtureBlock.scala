// Copyright (c) 2010 Belmont Technology Pty Ltd. All rights reserved.

package au.com.belmonttechnology.sodatest.runtime {
package data.blocks {

import processing.execution.SodaTestExecutionContext
import execution.activity.FixtureBlockExecutor

class FixtureBlock(source: BlockSource, val fixtureName: String) extends Block(source, fixtureName, inline = true) {
  def execute(context: SodaTestExecutionContext) = FixtureBlockExecutor.execute(context, this)

  override def toString = "FixtureBlock: " + fixtureName
}
}}