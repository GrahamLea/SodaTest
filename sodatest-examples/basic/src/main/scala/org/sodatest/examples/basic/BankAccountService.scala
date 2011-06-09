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

import collection.Set
import collection.immutable.List._
import Money._

class BankAccountService {

  private var accountsByName: Map[AccountName, BankAccount] = Map()

  def accountNames: Set[AccountName] = accountsByName.keySet

  def accountFor(accountName: AccountName): Option[BankAccount] = accountsByName.get(accountName)

  def statementFor(accountName: AccountName): List[StatementLine] = {
    accountFor(accountName) match {
      case Some(account) =>
        account.transactions.map(t => {
          if (t.amount > "0")
            new StatementLine(t.ref, t.description, Some(t.amount), None, t.balance)
          else
            new StatementLine(t.ref, t.description, None, Some(t.amount.negate), t.balance)
        })
      case None => error("Unknown Account: " + accountName)
    }
  }

  def openAccount(account: BankAccount): Unit = {
    accountsByName += account.name -> account
  }

  def deposit(accountName: AccountName, amount: Money): Unit = {
    accountsByName.get(accountName) match {
      case Some(account) => account.deposit(amount)
      case None => error("Unknown Account: " + accountName)
    }
  }

  def withdraw(accountName: AccountName, amount: Money): Unit = {
    accountsByName.get(accountName) match {
      case Some(account) => account.withdraw(amount)
      case None => error("Unknown Account: " + accountName)
    }
  }

  def endOfMonth(): Unit = {
    accountsByName.values.foreach(_.addInterest())
  }
}

class StatementLine(val ref: Int, val description: String, val credit: Option[Money], val debit: Option[Money], val balance: Money)
