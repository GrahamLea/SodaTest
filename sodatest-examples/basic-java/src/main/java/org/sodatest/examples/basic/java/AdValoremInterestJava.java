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

public class AdValoremInterestJava implements InterestFormulaJava {

    private final BigDecimal interestRate;

    public AdValoremInterestJava(String interestRate) {
        this.interestRate = new BigDecimal(interestRate).movePointLeft(2);
    }

    @Override
    public MoneyJava interstOn(MoneyJava currentBalance) {
        return currentBalance.multiply(interestRate);
    }
}
