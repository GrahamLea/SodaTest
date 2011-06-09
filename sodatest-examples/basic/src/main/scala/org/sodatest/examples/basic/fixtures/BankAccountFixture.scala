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

import org.sodatest.api.reflection.{ReflectiveSodaEvent, ReflectiveSodaReport, ReflectiveSodaFixture}
import org.sodatest.coercion.{CoercionRegister, Coercion}
import org.sodatest.api.SodaReport

import SodaReport._
import collection.immutable.List._
import org.sodatest.examples.basic._

class BankAccountFixture extends ReflectiveSodaFixture {

  val service = new BankAccountService

  def openAccount = new OpenAccountEvent(service)
  def deposit = new DepositEvent(service)
  def withdraw = new WithdrawEvent(service)
  def endOfMonth = new EndOfMonthEvent(service)

  def balance = new BalanceReport(service)
  def customers = new CustomersReport(service)
  def statement = new StatementReport(service)
  def customerTags = new CustomerTagsReport(service)

}

abstract class AbstractCustomerReport(val service: BankAccountService) extends ReflectiveSodaReport {
  var accountName: AccountName = null;

  def apply(account: BankAccount): Seq[Seq[String]]

  def apply(): Seq[Seq[String]] = {
    service.accountFor(accountName) match {
      case Some(a: BankAccount) => apply(a)
      case None => "Unknown Account".toSingleCellReport
    }
  }
}

class BalanceReport(service: BankAccountService) extends AbstractCustomerReport(service) {
  def apply(account: BankAccount) = account.balance.toSingleCellReport
}

class CustomerTagsReport(service: BankAccountService) extends AbstractCustomerReport(service) {
  def apply(account: BankAccount) = account.tags.toSingleColumnReport
}

class CustomersReport(val service: BankAccountService) extends SodaReport {
  def apply(parameters: Map[String, String]): Seq[Seq[String]] = service.accountNames.toSingleColumnReport
}

class StatementReport(val service: BankAccountService) extends ReflectiveSodaReport {
  var accountName: AccountName = null

  def apply(): List[List[String]] =
    (List("Ref", "Description", "Credit", "Debit", "Balance") ::
    service.statementFor(accountName).map(line => {
      List(line.ref, line.description, line.credit.getOrElse(""), line.debit.getOrElse(""), line.balance)
    })).toReport
}

class OpenAccountEvent(val service: BankAccountService) extends ReflectiveSodaEvent {

  val coercionRegister = new CoercionRegister(InterestFormulaCoercion)

  var accountName: AccountName = null
  var tags: List[String] = Nil
  var initialDeposit: Option[Money] = None
  var interestFormula: InterestFormula = new FixedInterest(Money.ZERO)

  def apply() {
    val account: BankAccount = new BankAccount(accountName, tags, interestFormula)
    service.openAccount(account)
    initialDeposit match {
      case Some(amount) => account.deposit(amount)
      case None =>
    }
  }
}

class DepositEvent(val service: BankAccountService) extends ReflectiveSodaEvent {
  var accountName: AccountName = null
  var amount: Money = null

  def apply() { service.deposit(accountName, amount) }
}

class WithdrawEvent(val service: BankAccountService) extends ReflectiveSodaEvent {
  var accountName: AccountName = null
  var amount: Money = null

  def apply() { service.withdraw(accountName, amount) }
}

class EndOfMonthEvent(val service: BankAccountService) extends ReflectiveSodaEvent {
  def apply() { service.endOfMonth() }
}

object InterestFormulaCoercion extends Coercion[InterestFormula] {
  def apply(s: String) = InterestFormula.fromString(s)
}

