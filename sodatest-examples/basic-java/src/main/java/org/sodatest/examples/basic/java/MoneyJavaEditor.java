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

import java.beans.PropertyEditorSupport;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MoneyJavaEditor extends PropertyEditorSupport {

    private final Pattern dollarPrefix = Pattern.compile("^\\$(.*)$");

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        Matcher matcher = dollarPrefix.matcher(text);
        if (matcher.matches()) {
            setValue(new MoneyJava(matcher.group(1)));
        } else {
            setValue(new MoneyJava(text));
        }
    }

    @Override
    public String getAsText() {
        return "$" + ((MoneyJava) getValue()).getAmount().toPlainString();
    }
}
