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

package org.sodatest.api.java.reflection;

import org.sodatest.api.SodaEvent;
import org.sodatest.api.SodaReport;
import org.sodatest.api.reflection.ReflectiveSodaFixture;
import org.sodatest.api.reflection.ReflectiveSodaFixture$class;
import scala.Option;

public abstract class ReflectiveSodaFixtureForJava implements ReflectiveSodaFixture {

    @Override
    public Option<SodaEvent> createEvent(String name) {
        return ReflectiveSodaFixture$class.createEvent(this, name);
    }

    @Override
    public Option<SodaReport> createReport(String name) {
        return ReflectiveSodaFixture$class.createReport(this, name);
    }
}
