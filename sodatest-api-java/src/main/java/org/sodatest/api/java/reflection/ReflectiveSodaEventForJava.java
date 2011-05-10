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

import org.sodatest.api.ParameterBindingException;
import org.sodatest.api.java.JavaParameterConverter;
import org.sodatest.api.reflection.ReflectiveSodaEvent;
import org.sodatest.api.reflection.ReflectiveSodaEvent$class;
import scala.collection.immutable.Map;

/**
 * A Java [[org.sodatest.api.SodaEvent]] base class that supports the automatic binding of parameters to
 * public and strongly-typed fields or setter methods.
 *
 * The documentation of {@link ReflectiveSodaEvent} explains the details of coercion and binding
 * that are applied to Reflective Events in both Scala and Java.
 *
 * <b>Example</b>
 * <pre>
 * public class MySodaEvent extends ReflectiveSodaEventForJava {
 *     public BigDecimal amount = null;
 *
 *     public void executeEvent() {
 *         ... // Execute the Event on the System, making use of 'amount'
 *     }
 * }
 * </pre>
 */
public abstract class ReflectiveSodaEventForJava extends JavaParameterConverter implements ReflectiveSodaEvent {

    /**
     * Executes this Event on the System under test using the parameters that have been coerced and
     * bound by reflection into the members of this instance.
     */
    protected abstract void executeEvent();

    
    /**
     * Invokes the {@link #executeEvent()} method.
     */
    @Override
    public final void apply() {
        executeEvent();
    }

    /**
     * Coerces and binds the parameters to this Event, then delegates to the {@link #apply()} function.
     */
    @Override
    public final void apply(Map<String, String> parameters) throws ParameterBindingException {
        ReflectiveSodaEvent$class.apply(this, parameters);
    }
}
