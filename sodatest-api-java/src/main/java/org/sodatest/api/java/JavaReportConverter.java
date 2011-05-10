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

import scala.collection.JavaConversions;
import scala.collection.Seq;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Abstract class for Java-based {@link org.sodatest.api.SodaReport}s that provides helper methods for
 * Java-Scala integration, especially around conversion of Report results.
 */
public abstract class JavaReportConverter extends JavaParameterConverter {
    /**
     * Converts a Java <code>List<List<String>></String></code> to a Scala <code>Seq[Seq[String]]</code>
     *
     * Subclasses can override this method if they wish to use a different conversion.
     *
     * @return the contents of 'report' inserted into a Scala Seq of Seqs.
     */
    protected Seq<Seq<String>> convertReport(List<List<String>> report) {
        List<Seq<String>> convertedRows = new ArrayList<Seq<String>>();
        for (List<String> row : report) {
            convertedRows.add(JavaConversions.asScalaBuffer(row));
        }
        return JavaConversions.asScalaBuffer(convertedRows);
    }
}
