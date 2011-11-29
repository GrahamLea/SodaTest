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

package org.sodatest.runtime.data
package results

import blocks.Block

abstract class BlockResult[T <: Block](
  val block: T,
  val errorOccurred: Boolean,
  val executionErrorOccurred: Boolean,
  val succeeded: Boolean,
  val blockError: Option[ExecutionError]) {

  def this(block: T, executionErrorOccurred: Boolean, succeeded: Boolean, blockError: Option[ExecutionError]) =
    this(block, executionErrorOccurred || blockError != None, executionErrorOccurred, succeeded, blockError)

  def this(block: T, executionErrorOccurred: Boolean, blockError: Option[ExecutionError]) =
    this(block, executionErrorOccurred, !executionErrorOccurred, blockError)

  override def toString =
    "Result for " + block.toString +
      (blockError match {case Some(e) => " [Error: " + e.message + "]"; case _ => ""})
}