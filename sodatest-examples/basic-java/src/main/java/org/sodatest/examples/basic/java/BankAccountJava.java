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

package org.sodatest.examples.basic.java;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;

public class BankAccountJava {

    private final AccountNameJava name;
    private final List<String> tags;
    private final InterestFormulaJava interestFormula;
    private final List<TransactionJava> transactions = new ArrayList<TransactionJava>();

    public BankAccountJava(AccountNameJava name, List<String> tags, InterestFormulaJava interestFormula) {
        this.name = name;
        this.tags = tags;
        this.interestFormula = interestFormula;
    }

    public MoneyJava getBalance() {
        MoneyJava total = MoneyJava.ZERO;
        for (TransactionJava transaction : transactions) {
            total = total.add(transaction.getAmount());
        }
        return total;
    }

    public List<TransactionJava> getTransactions() {
        return Collections.unmodifiableList(transactions);
    }

    public void deposit(MoneyJava amount) {
        transactions.add(new TransactionJava(transactions.size() + 1, "Deposit", amount, getBalance().add(amount)));
    }

    public void withdraw(MoneyJava amount) {
        transactions.add(new TransactionJava(transactions.size() + 1, "Withdrawal", amount.negate(), getBalance().subtract(amount)));
    }

    public void addInterest() {
        MoneyJava interest = interestFormula.interstOn(getBalance());
        transactions.add(new TransactionJava(transactions.size() + 1, "Interest", interest, getBalance().add(interest)));
    }

    public List<List<String>> getStatement() {
        ArrayList<List<String>> lines = new ArrayList<List<String>>();
        lines.add(asList("Ref", "Description", "Credit", "Debit", "Balance"));
        for (TransactionJava t : transactions) {
            if (t.getAmount().greaterThan(MoneyJava.ZERO)) {
                lines.add(asList(String.valueOf(t.getRef()), t.getDescription(), t.getAmount().toString(), "", t.getBalance().toString()));
            } else {
                lines.add(asList(String.valueOf(t.getRef()), t.getDescription(), "", t.getAmount().negate().toString(), t.getBalance().toString()));
            }
        }
        return lines;
    }

    public AccountNameJava getName() {
        return name;
    }

    public List<String> getTags() {
        return tags;
    }
}
