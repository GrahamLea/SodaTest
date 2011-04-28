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

package org.sodatest.examples.bankaccount

import _root_.java.beans.{PropertyEditorSupport}
import _root_.java.lang.String
import _root_.java.math.BigDecimal

class Money(a: BigDecimal) {
  val amount = a.setScale(2);

  def this() = this(BigDecimal.ZERO)
  def this(s: String) = this(new BigDecimal(s))

  def +(newAmount: Money) = new Money(amount.add(newAmount.amount))
  def -(newAmount: Money) = new Money(amount.subtract(newAmount.amount))
  def *(multiplicand: String): Money = *(new BigDecimal(multiplicand))
  def *(multiplicand: BigDecimal): Money = new Money(amount.multiply(multiplicand))

  override def toString = "$" + amount.toString
}

object Money {
  val ZERO = new Money(new BigDecimal(0))
}

class MoneyEditor extends PropertyEditorSupport {
  val dollarPrefix = "^\\$.*".r

  override def setAsText(text: String) = setValue(new Money(new BigDecimal(text match {
    case dollarPrefix() => text.substring(1)
    case _ => text
  })))

  override def getAsText = "$" + getValue.asInstanceOf[Money].amount.toPlainString
}

class BankAccount {
  var balance: Money = Money.ZERO
}

class BankAccountService {
  var accountsByName: Map[String, BankAccount] = Map()
}
