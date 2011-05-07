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

public abstract class ReflectiveSodaEventForJava extends JavaParameterConverter implements ReflectiveSodaEvent {

    protected abstract void executeEvent();

    protected void preBinding(Map<String, String> parameters) {
    }

    @Override
    public final void apply() {
        executeEvent();
    }

    @Override
    public final void apply(Map<String, String> parameters) throws ParameterBindingException {
        preBinding(parameters);
        ReflectiveSodaEvent$class.apply(this, parameters);
    }
}
