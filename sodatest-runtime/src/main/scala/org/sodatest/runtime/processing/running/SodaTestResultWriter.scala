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
package processing
package running

import org.sodatest.api.SodaTestLog
import data.results.SodaTestResult
import java.io.File
import collection.immutable.List

trait SodaTestResultWriter {
  def createOutputDirectories(inputRoot: File, files: scala.List[File], outputRoot: File): Unit
  def writeResultsFiles(filesAndResults: List[(File, SodaTestResult)], inputRoot: File, outputRoot: File)(implicit log: SodaTestLog): Unit
}

