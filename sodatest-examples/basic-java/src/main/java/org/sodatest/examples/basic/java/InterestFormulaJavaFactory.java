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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InterestFormulaJavaFactory {

    private static final Pattern adValoremRegex = Pattern.compile("(\\d+(\\.\\d+)?)%");
    private static final Pattern fixedRegex = Pattern.compile("\\$(\\d+(\\.\\d+)?)");

    public static InterestFormulaJava fromString(String formulaString) {
        Matcher adValoremMatcher = adValoremRegex.matcher(formulaString);
        if (adValoremMatcher.matches()) {
            return new AdValoremInterestJava(adValoremMatcher.group(1));
        }
        Matcher fixedMatcher = fixedRegex.matcher(formulaString);
        if (fixedMatcher.matches()) {
            return new FixedInterestJava(new MoneyJava(fixedMatcher.group(1)));
        }
        throw new RuntimeException("Unparseable interest formula: " + formulaString);
    }
}
