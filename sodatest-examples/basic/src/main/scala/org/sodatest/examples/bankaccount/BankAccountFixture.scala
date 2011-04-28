// Copyright (c) 2010 Belmont Technology Pty Ltd. All rights reserved.

package org.sodatest.examples.bankaccount

import _root_.java.lang.String
import org.sodatest.api.reflection._

class BankAccountFixture extends ReflectiveSodaFixture {
  val service = new BankAccountService()

  def openAccount = new OpenAccountAction(service)
  def deposit = new DepositAction(service)
  def withdraw = new WithdrawAction(service)
  def endOfMonth = new EndOfMonthAction(service)

  def balance = new BalanceReport(service)
}

class BalanceReport(val service: BankAccountService) extends ReflectiveSodaReport {
  var accountName: String = null;

  def apply() = service.accountsByName.get(accountName) match {
    case Some(a: BankAccount) => List(List(a.balance.toString)) // TODO: Auto-format List[List[Any]] -> List[List[String]]
    case None => List(List("Unknown Account"))
  }
}

class OpenAccountAction(val service: BankAccountService) extends ReflectiveSodaEvent {
  var accountName: String = null;

  def apply() {
    service.accountsByName += accountName -> new BankAccount
  }
}

class DepositAction(val service: BankAccountService) extends ReflectiveSodaEvent {
  var accountName: String = null;
  var amount: Money = null;

  def apply() {
    service.accountsByName.get(accountName) match {
      case Some(account) => account.balance = account.balance + amount
      case None => throw new RuntimeException("Unknown account")
    }
  }
}

class WithdrawAction(val service: BankAccountService) extends ReflectiveSodaEvent {
  var accountName: String = null;
  var amount: Money = null;

  def apply() {
    service.accountsByName.get(accountName) match {
      case Some(account) => account.balance = account.balance - amount
      case None => throw new RuntimeException("Unknown account")
    }
  }
}

class EndOfMonthAction(val service: BankAccountService) extends ReflectiveSodaEvent {
  def apply() {
    service.accountsByName.values.foreach(b => {b.balance = b.balance + (b.balance * "0.1") })
  }
}
