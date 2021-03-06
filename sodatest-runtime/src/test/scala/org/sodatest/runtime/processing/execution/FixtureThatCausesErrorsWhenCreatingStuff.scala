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

package org.sodatest.runtime.processing.execution

import org.sodatest.api.reflection._

class FixtureThatCausesErrorsWhenCreatingStuff extends ReflectiveSodaFixture() {
  def reportNameReturningEvent() = new ReflectiveSodaEvent() {
    protected def apply() {}
  }

  def eventNameReturningReport() = new ReflectiveSodaReport() {
    protected def apply() = List(List())
  }

  def eventNameDifferentByCase() = new ReflectiveSodaEvent() {
    protected def apply() {}
  }

  def eventNameDifferentByCASE() = new ReflectiveSodaEvent() {
    protected def apply() {}
  }

  def reportNameDifferentByCase() = new ReflectiveSodaReport() {
    protected def apply() = List(List())
  }

  def reportNameDifferentByCASE() = new ReflectiveSodaReport() {
    protected def apply() = List(List())
  }

  def creatingThisReportThrowsAnError() = new ReflectiveSodaReport() {
    throw new RuntimeException("I refuse to be initisliased")
    protected def apply() = List(List())
  }

  def creatingThisEventThrowsAnError() = new ReflectiveSodaEvent() {
    throw new RuntimeException("I refuse to be initisliased")
    protected def apply() {}
  }
}

