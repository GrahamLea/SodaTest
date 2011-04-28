// Copyright (c) 2010 Belmont Technology Pty Ltd. All rights reserved.

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
