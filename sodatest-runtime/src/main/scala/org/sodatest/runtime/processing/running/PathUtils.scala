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

package org.sodatest.runtime.processing.running

import java.io.File
import annotation.tailrec

object PathUtils {
  def collectFilesRecursive(inputDirectory: File, fileFilter: File => Boolean): List[File] = {
    inputDirectory.listFiles.filter(fileFilter).toList ++
      inputDirectory.listFiles.filter(_.isDirectory).map(collectFilesRecursive(_, fileFilter)).toList.flatten
  }

  def getOutputPath(inputPath: File, inputRoot: File, outputRoot: File, newSuffix: String = ""): File = {
    new File(outputRoot, relativeToInputPath(inputPath, inputRoot) + newSuffix)
  }

  def relativeToInputPath(path: File, inputRoot: File): String = {
    val inputRootSize = asList(inputRoot).size
    val relativePathList = asList(path).drop(inputRootSize)
    val pathRelativeToInput: String = relativePathList.mkString(File.separator)
    pathRelativeToInput
  }

  @tailrec
  def asList(file: File, list: List[String] = Nil): List[String] = {
    file.getParentFile match {
      case null => list
      case p => asList(p, file.getName :: list)
    }
  }

}