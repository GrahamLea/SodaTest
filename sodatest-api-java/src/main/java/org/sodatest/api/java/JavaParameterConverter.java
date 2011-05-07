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

public abstract class JavaParameterConverter {

    protected Map<String, String> convertParameters(scala.collection.immutable.Map<String, String> parameters) {
        return JavaConversions.asJavaMap(parameters);
    }

    protected static final <T> Option<T> None() {
        return (Option<T>) scala.None$.MODULE$;
    }

    protected static final <T> List<T> list(T... values) {
        return Arrays.asList(values);
    }
}
