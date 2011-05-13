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

package org.sodatest.runtime
package processing.parsing.blocks

import org.specs.SpecificationWithJUnit
import data.blocks._

class BlockFactoryErrorsSpec extends SpecificationWithJUnit {

  implicit val log = NoOpLog
  val blockFactory = new BlockFactory

  "BlockFactory" should {

    "create parse error blocks" in {
      val unknownBlockTypeBlockSource = BlockSource(List(
        Line(1, List("Junk", "Is not recognised")),
        Line(2, List("", "Should collect")),
        Line(3, List("", "all the lines"))
      ))

      val emptyBlockTypeBlockSource = BlockSource(List(
        Line(5, List("", "No type at all")),
        Line(6, List("", "A bit silly"))
      ))

      val blocks = blockFactory.create(List(
        unknownBlockTypeBlockSource,
        emptyBlockTypeBlockSource
      ))

      var blockIndex = 0;
      {
        val parseErrorBlock = blocks(blockIndex).asInstanceOf[ParseErrorBlock]
        parseErrorBlock.source must_== unknownBlockTypeBlockSource
        parseErrorBlock.name must_== "Is not recognised"
        parseErrorBlock.error must_== "Unknown Block type: 'Junk'"
        parseErrorBlock.errorSource must_== (0, 0)
      }
      blockIndex += 1

      {
        val parseErrorBlock = blocks(blockIndex).asInstanceOf[ParseErrorBlock]
        parseErrorBlock.source must_== emptyBlockTypeBlockSource
        parseErrorBlock.name must_== "No type at all"
        parseErrorBlock.error must_== "No Block type specified"
        parseErrorBlock.errorSource must_== (0, 0)
      }
      blockIndex += 1

    }


    // TODO: Extra cases:
    // Event with no parameters and no !!
    // Event with parameters but no values
    // Report with no parameters and no !!
    // Report with parameters but no values
    // Report with no output okay?
    // Blank block type cell

  }

}