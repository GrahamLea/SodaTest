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

import java.util.ArrayList;
import java.util.List;

public class TagsReportJava extends AbstractCustomerReportJava {

    public TagsReportJava(BankAccountServiceJava service) {
        super(service);
    }

    @Override
    public List<List<String>> getReport(BankAccountJava account) {
        return toSingleColumnReport(account.getTags());
    }
}
