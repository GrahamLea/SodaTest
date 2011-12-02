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

import org.sodatest.coercion.CoercionBindingException;
import org.sodatest.api.SodaReport;
import scala.collection.Seq;

import java.util.List;
import java.util.Map;

/**
 * Abstract base class for Java-based {@link org.sodatest.api.SodaReport}s that do not require
 * reflection-based coercion and binding.
 *
 * The majority of Reports will more easily be written by extending {@link org.sodatest.api.java.reflection.ReflectiveSodaReportForJava}
 *
 * @see org.sodatest.api.java.reflection.ReflectiveSodaEventForJava
 */
public abstract class SodaReportForJava extends JavaReportConverter implements SodaReport {

    /**
     * Executes this Report against the System under test using the given parameters.
     *
     * @param parameters A map of parameter names and values that should be used in executing the
     * Report.
     *
     * @throws CoercionBindingException if an error occurs while attempting to translate one of the
     * string values in the parameter map into a value that can be used by the Report.
     *
     * @throws java.lang.Throwable if anything else goes wrong while executing the Report.
     */
    public abstract List<List<String>> getReport(Map<String, String> parameters) throws CoercionBindingException;

    /**
     * Converts the parameters to a Java Map and passes them to {@link #getReport(java.util.Map)}
     * and converts the result of <code>getReport()</code> before returning it.
     *
     * You can override {@link #convertParameters(scala.collection.immutable.Map)} and/or
     * {@link #convertReport(java.util.List)} if you want to use different methods of conversion.
     */
    @Override
    public final Seq<Seq<String>> apply(scala.collection.immutable.Map<String, String> parameters) throws CoercionBindingException {
        return convertReport(getReport(convertParameters(parameters)));
    }
}
