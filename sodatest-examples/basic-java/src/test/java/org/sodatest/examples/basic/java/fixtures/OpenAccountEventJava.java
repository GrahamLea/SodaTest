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

import org.sodatest.api.java.reflection.ReflectiveSodaEventForJava;
import org.sodatest.coercion.CoercionRegister;
import org.sodatest.coercion.java.CoercionRegisterForJava;
import org.sodatest.examples.basic.java.*;
import scala.Option;

import java.util.List;

public class OpenAccountEventJava extends ReflectiveSodaEventForJava {
    private BankAccountServiceJava service;

    // TODO: Figure out how to write Coerdions in Java
    private final CoercionRegister coercionRegister = new CoercionRegisterForJava(new InterestFormulaJavaCoercion());

    private static final Option<MoneyJava> NoneMoney = None();

    public AccountNameJava accountName;
    public Option<MoneyJava> initialDeposit = NoneMoney;
    public List<String> tags = list();
    public InterestFormulaJava interestFormula = null;

    public OpenAccountEventJava(BankAccountServiceJava service) {
        this.service = service;
    }

    @Override
    public void executeEvent() {
        BankAccountJava newAccount = new BankAccountJava(accountName, tags, interestFormula);
        service.add(newAccount);
        if (initialDeposit != NoneMoney) {
            newAccount.deposit(initialDeposit.get());
        }
    }
}
