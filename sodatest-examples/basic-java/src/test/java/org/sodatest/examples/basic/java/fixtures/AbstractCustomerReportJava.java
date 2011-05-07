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

import org.sodatest.api.java.reflection.ReflectiveSodaReportForJava;
import org.sodatest.examples.basic.java.AccountNameJava;
import org.sodatest.examples.basic.java.BankAccountJava;
import org.sodatest.examples.basic.java.BankAccountServiceJava;

import java.util.List;

public abstract class AbstractCustomerReportJava extends ReflectiveSodaReportForJava {

    private final BankAccountServiceJava service;

    private AccountNameJava accountName;

    protected AbstractCustomerReportJava(BankAccountServiceJava service) {
        this.service = service;
    }

    protected abstract List<List<String>> getReport(BankAccountJava account);

    @Override
    protected final List<List<String>> getReport() {
        BankAccountJava account = service.get(accountName);
        if (account != null) {
            return getReport(account);
        } else {
            return list(list("Unknown Account"));
        }
    }

    public void setAccountName(AccountNameJava accountName) {
        this.accountName = accountName;
    }
}
