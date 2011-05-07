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

package org.sodatest.examples.basic.fixtures

import _root_.java.lang.String
import org.sodatest.api.reflection._
import org.sodatest.api.SodaReport
import collection.immutable.Map
import org.sodatest.coercion.{Coercion, CoercionRegister}
import org.sodatest.examples.basic._

class BankAccountFixture extends ReflectiveSodaFixture {
  val service = new BankAccountService()

  def openAccount = new OpenAccountEvent(service)
  def deposit = new DepositEvent(service)
  def withdraw = new WithdrawEvent(service)
  def interestAccruedAtTheEndOfTheMonth = new AddInterestEvent(service)

  def balance = new BalanceReport(service)
  def customers = new CustomersReport(service)
  def statement = new StatementReport(service)
  def customerTags = new TagsReport(service)
}

abstract class AbstractCustomerReport(val service: BankAccountService) extends ReflectiveSodaReport {
  var accountName: AccountName = null;

  def apply(account: BankAccount): Seq[Seq[String]]

  def apply(): Seq[Seq[String]] = {
    service.accountsByName.get(accountName) match {
      case Some(a: BankAccount) => apply(a)
      case None => List(List("Unknown Account"))
    }
  }
}

class BalanceReport(service: BankAccountService) extends AbstractCustomerReport(service) {
  def apply(account: BankAccount) = List(List(account.balance.toString)) // TODO: Auto-format List[List[Any]] -> Seq[Seq[String]]
}

class TagsReport(service: BankAccountService) extends AbstractCustomerReport(service) {
  def apply(account: BankAccount) = account.tags map (List(_))
}

class CustomersReport(val service: BankAccountService) extends SodaReport {
  def apply(parameters: Map[String, String]): Seq[Seq[String]] = {
    service.accountsByName.keys.toList.map(accountName => List(accountName.name))
  }
}

class StatementReport(service: BankAccountService) extends AbstractCustomerReport(service) {
  def apply(account: BankAccount) = account.statement
}

class OpenAccountEvent(val service: BankAccountService) extends ReflectiveSodaEvent {

  val coercionRegister = new CoercionRegister(InterestFormulaCoercion)

  var accountName: AccountName = null;
  var initialDeposit: Option[Money] = None;
  var tags: List[String] = Nil;
  var interestFormula: InterestFormula = null;

  def apply() {
    val newAccount: BankAccount = new BankAccount(accountName, tags, interestFormula)
    service.accountsByName += accountName -> newAccount
    initialDeposit match {
      case Some(amount) => newAccount.deposit(amount)
      case None =>
    }
  }
}

class DepositEvent(val service: BankAccountService) extends ReflectiveSodaEvent {
  var accountName: AccountName = null;
  var amount: Money = null;

  def apply() {
    service.accountsByName.get(accountName) match {
      case Some(account) => account.deposit(amount)
      case None => throw new RuntimeException("Unknown account")
    }
  }
}

class WithdrawEvent(val service: BankAccountService) extends ReflectiveSodaEvent {
  var accountName: AccountName = null;
  var amount: Money = null;

  def apply() {
    service.accountsByName.get(accountName) match {
      case Some(account) => account.withdraw(amount)
      case None => throw new RuntimeException("Unknown account")
    }
  }
}

class AddInterestEvent(val service: BankAccountService) extends ReflectiveSodaEvent {
  def apply() {
    service.accountsByName.values.foreach(account => {account.addInterest()})
  }
}

object InterestFormulaCoercion extends Coercion[InterestFormula] {
  def apply(s: String) = InterestFormula.fromString(s)
}

