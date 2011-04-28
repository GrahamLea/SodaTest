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
