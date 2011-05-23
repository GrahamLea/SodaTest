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

package org.sodatest.examples.basic.java.fixtures;

import org.sodatest.examples.basic.java.BankAccountJava;
import org.sodatest.examples.basic.java.BankAccountServiceJava;
import org.sodatest.examples.basic.java.MoneyJava;
import org.sodatest.examples.basic.java.TransactionJava;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

public class StatementReportJava extends AbstractCustomerReportJava {

    public StatementReportJava(BankAccountServiceJava service) {
        super(service);
    }

    @Override
    public List<List<String>> getReport(BankAccountJava account) {
        ArrayList<List<String>> lines = new ArrayList<List<String>>();
        lines.add(asList("Ref", "Description", "Credit", "Debit", "Balance"));
        for (TransactionJava t : account.getTransactions()) {
            if (t.getAmount().greaterThan(MoneyJava.ZERO)) {
                lines.add(asList(String.valueOf(t.getRef()), t.getDescription(), t.getAmount().toString(), "", t.getBalance().toString()));
            } else {
                lines.add(asList(String.valueOf(t.getRef()), t.getDescription(), "", t.getAmount().negate().toString(), t.getBalance().toString()));
            }
        }
        return lines;
    }


}
