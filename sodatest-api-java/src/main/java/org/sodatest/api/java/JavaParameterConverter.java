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

package org.sodatest.api.java;

import org.sodatest.api.SodaReport;
import scala.Option;
import scala.collection.JavaConversions;
import scala.collection.Seq;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Abstract class for Java-based {@link org.sodatest.api.SodaEvent}s and {@link SodaReport}s that
 * provides helper methods for Java-Scala integration, especially around parameter conversion.
 */
public abstract class JavaParameterConverter {

    /**
     * Converts a Scala Map of parameters to a Java map.
     *
     * Subclasses can override this method if they wish to use a different conversion.
     *
     * @return the contents of 'parameters' inserted into a Java map
     */
    protected Map<String, String> convertParameters(scala.collection.immutable.Map<String, String> parameters) {
        return JavaConversions.asJavaMap(parameters);
    }

    /**
     * Returns the Scala <code>None</code> object, appropriately cast as necessary.
     */
    protected static final <T> Option<T> None() {
        return (Option<T>) scala.None$.MODULE$;
    }

    /**
     * Creates and returns a new {@link List} containing the supplied values.
     *
     * This method is just a static forwarder (i.e. a synonym) for {@link Arrays#asList}
     */
    protected static final <T> List<T> list(T... values) {
        return Arrays.asList(values);
    }
}
