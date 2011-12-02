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

import org.sodatest.api.java.JavaReportConverter;
import org.sodatest.api.reflection.ReflectiveSodaReport;
import org.sodatest.api.reflection.ReflectiveSodaReport$class;
import org.sodatest.coercion.CoercionBindingException;
import scala.collection.Seq;
import scala.collection.immutable.Map;

import java.util.List;

/**
 * A Java [[org.sodatest.api.SodaReport]] base class that supports the automatic binding of parameters to
 * public and strongly-typed fields or setter methods.
 *
 * The documentation of {@link ReflectiveSodaReport} explains the details of coercion and binding
 * that are applied to Reflective Reports in both Scala and Java.
 *
 * <b>Example</b>
 * <pre>
 * public class MySodaReport extends ReflectiveSodaReportForJava {
 *     public BigDecimal amount = null;
 *
 *     public List&lt;List&lt;String&gt;&gt; getReport() {
 *         return ...; // Execute the Report on the System, making use of 'amount'
 *     }
 * }
 * </pre>
 */
public abstract class ReflectiveSodaReportForJava extends JavaReportConverter implements ReflectiveSodaReport {

    /**
     * Executes this Report on the System under test using the parameters that have been coerced and
     * bound by reflection into the members of this instance.
     *
     * @return a table representing the result of the Report as a List of Lists of string.
     * There is no requirement for the Lists in the second dimension to have the same length as each other.
     */
    protected abstract List<List<String>> getReport();

    /**
     * Invokes the {@link #getReport()} method and converts the result before returning it.
     *
     * You can override {@link #convertReport(java.util.List)} if you want to use a different method of conversion.
     */
    public final Seq<Seq<String>> apply() {
        return convertReport(getReport());
    }

    /**
     * Coerces and binds the parameters to this Report, then delegates to the (@link #apply()} function.
     */
    @Override
    public final Seq<Seq<String>> apply(Map<String, String> parameters) throws CoercionBindingException {
        return ReflectiveSodaReport$class.apply(this, parameters);
    }
}
