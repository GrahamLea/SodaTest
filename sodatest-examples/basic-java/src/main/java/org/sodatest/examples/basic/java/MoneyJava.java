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

import java.math.BigDecimal;

public class MoneyJava {

    public static final MoneyJava ZERO = new MoneyJava(BigDecimal.ZERO);

    private final BigDecimal amount;

    public MoneyJava(BigDecimal amount) {
        this.amount = amount.setScale(2);
    }

    public MoneyJava(String amount) {
        this(new BigDecimal(amount));
    }

    public MoneyJava() {
        this(BigDecimal.ZERO);
    }

    public MoneyJava add(MoneyJava that) {
        return new MoneyJava(this.amount.add(that.amount));
    }

    public MoneyJava subtract(MoneyJava that) {
        return new MoneyJava(this.amount.subtract(that.amount));
    }

    public MoneyJava multiply(BigDecimal multiplicand) {
        return new MoneyJava(this.amount.multiply(multiplicand));
    }

    public MoneyJava multiply(String multiplicand) {
        return multiply(new BigDecimal(multiplicand));
    }

    public boolean greaterThan(MoneyJava that) {
        return this.amount.compareTo(that.amount) > 0;
    }

    public MoneyJava negate() {
        return new MoneyJava(amount.negate());
    }

    public BigDecimal getAmount() {
        return amount;
    }

    @Override
    public String toString() {
        return "$" + amount.toPlainString();
    }
}
