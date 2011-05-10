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

/**
 * Java {@link org.sodatest.api.SodaFixture} base class that supports the discovery of {@link SodaEvent}s
 * and {@link SodaReport}s by reflecting on the subclass' functions.
 *
 * ReflectiveSodaFixtureForJava is probably the easiest way to implement the {@link org.sodatest.api.SodaFixture}
 * interface in a Java environment.
 * Simply extend this class and then define in the subclass public methods that have no parameters
 * and which return either a {@link SodaEvent} or {@link SodaReport} as required.
 * ReflectiveSodaFixtureForJava will canonize the incoming Event or Report name and then discover and invoke
 * a method on the subclass that has a name which, when also canonized, matches the Event or
 * Report name.
 *
 * (Names in SodaTest are canonized by removing all non-alpha-numeric characters and
 * converting all alpha characters to lower-case. e.g. canonized("Secret Report #2") -> "secretreport2")
 *
 * <b>Example</b>
 * <pre>
 * public class MyFixutre extends ReflectiveSodaFixtureForJava {
 *     public SodaReport secretReport2() {
 *         return new SecretReport2();
 *     }
 * }
 * </pre>
 */
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
