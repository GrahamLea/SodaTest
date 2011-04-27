// Copyright (c) 2010 Belmont Technology Pty Ltd. All rights reserved.

package au.com.belmonttechnology.sodatest.runtime
package data.blocks

import processing.execution.SodaTestExecutionContext
import data.results.BlockResult

abstract class Block(val source: BlockSource, val name: String, val inline: Boolean) {
  def execute(context: SodaTestExecutionContext): BlockResult[_]
}

abstract class ParamterisedBlock(source: BlockSource, name: String, inline: Boolean, val parameterNames: List[String])
extends Block(source, name, inline)