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

import org.sodatest.api.SodaEvent;
import org.sodatest.coercion.CoercionBindingException;

import java.util.Map;

/**
 * Abstract base class for Java-based {@link org.sodatest.api.SodaEvent}s that do not require
 * reflection-based coercion and binding.
 *
 * The majority of Events will more easily be written by extending {@link org.sodatest.api.java.reflection.ReflectiveSodaEventForJava}
 *
 * @see org.sodatest.api.java.reflection.ReflectiveSodaEventForJava
 */
public abstract class SodaEventForJava extends JavaParameterConverter implements SodaEvent {

    /**
     * Applies this event to the System under test using the given parameters.
     *
     * @param parameters A map of parameter names and values that should be used in applying the
     * Event.
     *
     * @throws CoercionBindingException if an error occurs while attempting to translate one of the
     * string values in the parameter map into a value that can be used by the Event.
     *
     * @throws java.lang.Throwable if anything else goes wrong while executing the Event.
     */
    public abstract void executeEvent(Map<String, String> parameters) throws CoercionBindingException;

    /**
     * Converts the parameters to a Java Map and passes them to {@link #executeEvent(java.util.Map)}
     *
     * You can override {@link #convertParameters(scala.collection.immutable.Map)} if you want to use
     * a different method of conversion.
     */
    @Override
    public final void apply(scala.collection.immutable.Map<String, String> parameters) throws CoercionBindingException {
        executeEvent(convertParameters(parameters));
    }
}
