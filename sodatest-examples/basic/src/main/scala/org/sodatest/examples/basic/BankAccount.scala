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

package org.sodatest.examples.basic

import java.beans.{PropertyEditorSupport}
import java.lang.String
import java.math.BigDecimal
import collection.immutable.List._
import java.text.DecimalFormat

class BankAccount(val name: AccountName, val tags: List[String], val interestFormula: InterestFormula) {
  private var _transactions: List[Transaction] = Nil

  def balance: Money = _transactions.foldLeft(Money.ZERO)((total, transaction) => {transaction.amount + total})
  def transactions = _transactions

  def deposit(amount: Money): Unit = {
    _transactions :+= new Transaction(_transactions.size + 1, "Deposit", amount, balance + amount)
  }

  def withdraw(amount: Money): Unit = {
    _transactions :+= new Transaction(_transactions.size + 1, "Withdrawal", amount.negate, balance - amount)
  }

  def addInterest(): Unit = {
    val interest = interestFormula.interestOn(balance)
    _transactions :+= new Transaction(_transactions.size + 1, "Interest", interest, balance + interest)
  }
}

class Money(a: BigDecimal) {
  val amount = a.setScale(2);

  def this() = this(BigDecimal.ZERO)

  def +(newAmount: Money) = new Money(amount.add(newAmount.amount))
  def -(newAmount: Money) = new Money(amount.subtract(newAmount.amount))
  def *(multiplicand: String): Money = *(new BigDecimal(multiplicand))
  def *(multiplicand: BigDecimal): Money = new Money(amount.multiply(multiplicand))
  def >(value: String): Boolean = amount.compareTo(new BigDecimal(value)) > 0
  def negate = new Money(amount.negate)

  override def toString = Money.format.format(amount)
}

object Money {
  val ZERO = new Money(new BigDecimal(0))
  val format = new DecimalFormat("$#,##0.00")
}

case class AccountName(name: String) {
  override def toString = name
}

case class Transaction(val ref: Int, val description: String, val amount: Money, val balance: Money)



trait InterestFormula {
  def interestOn(currentBalance: Money): Money
}

object InterestFormula {
  val adValoremRegex = """(\d+(\.\d+)?)%""".r
  val fixedRegex = """\$(\d+(\.\d+)?)""".r

  def fromString(formulaString: String): InterestFormula = {
    formulaString match {
      case adValoremRegex(rate, decimals) => new AdValoremInterest(rate)
      case fixedRegex(amount, decimals) => new FixedInterest(new Money(new BigDecimal(amount)))
      case _ => error("Unparseable interest formula: " + formulaString)
    }
  }
}

class AdValoremInterest(interestRate: String) extends InterestFormula {
  val rate = new BigDecimal(interestRate).movePointLeft(2)
  def interestOn(currentBalance: Money): Money = currentBalance * rate
}

class FixedInterest(val interestAmount: Money) extends InterestFormula {
  def interestOn(currentBalance: Money): Money = interestAmount
}
