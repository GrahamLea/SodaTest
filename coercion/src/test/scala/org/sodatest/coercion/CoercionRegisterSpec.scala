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

package org.sodatest.coercion { package test {

import org.specs.SpecificationWithJUnit

class CoercionRegisterSpec extends SpecificationWithJUnit {

  val innerClass1Coercion = new Coercion[InnerClass1] {
    def apply(s: String) = new InnerClass1(s.toInt)
  }

  val innerClass2Coercion = new Coercion[InnerClass2] {
    def apply(s: String) = new InnerClass2(s.toInt)
  }

  "CoercionRegister" should {
    "support the '+' operator to produce a new CoercionRegister with an additional Coercion " in {
      val originalRegister = new CoercionRegister(innerClass1Coercion)
      originalRegister.get(classOf[InnerClass1]) must_== Some(innerClass1Coercion)
      originalRegister.get(classOf[InnerClass2]) must_== None

      val newRegister = originalRegister + innerClass2Coercion
      newRegister.get(classOf[InnerClass1]) must_== Some(innerClass1Coercion)
      newRegister.get(classOf[InnerClass2]) must_== Some(innerClass2Coercion)
      originalRegister.get(classOf[InnerClass1]) must_== Some(innerClass1Coercion)
      originalRegister.get(classOf[InnerClass2]) must_== None

    }
  }

  case class InnerClass1(value: Int)
  case class InnerClass2(value: Int)
}

}}