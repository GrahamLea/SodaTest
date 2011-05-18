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

package org.sodatest.runtime {
package data.blocks {

import processing.execution.SodaTestExecutionContext
import StringExtension._
import data.results.ParseErrorBlockResult

class ParseErrorBlock(source: BlockSource, val error: String, val errorSource: (Int, Int))
  extends Block(source,
                source.lines(0).cells match {case blockType :: name :: tail => name.truncate(20); case _ => "" },
                inline = true) {

  def execute(context: SodaTestExecutionContext) = new ParseErrorBlockResult(this)

  override def toString = super.toString + " (" + error + " @ " + errorSource + ")"
}
}}