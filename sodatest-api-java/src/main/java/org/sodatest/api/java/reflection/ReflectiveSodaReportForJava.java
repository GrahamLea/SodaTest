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
import scala.collection.Seq;
import scala.collection.immutable.Map;

import java.util.List;

public abstract class ReflectiveSodaReportForJava extends JavaReportConverter implements ReflectiveSodaReport {

    protected abstract List<List<String>> getReport();

    protected void preBinding(Map<String, String> parameters) {
    }

    public final Seq<Seq<String>> apply() {
        return convertReport(getReport());
    }

    @Override
    public final Seq<Seq<String>> apply(Map<String, String> parameters) {
        preBinding(parameters);
        return ReflectiveSodaReport$class.apply(this, parameters);
    }
}
