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

package org.sodatest.junit;

import java.lang.annotation.*;

/**
 * Specifies the root package name that will be used by the {@link JUnitSodaTestRunner} to resolve
 * Fixture class names.
 *
 * If your {@link JUnitSodaTestLauncherTestBase} subclasses are not annotated with this annotation,
 * the package name of the subclass will be used as the fixture root for tests collected and run
 * by that subclass.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface JUnitSodaTestLauncherFixtureRoot {
    public String value();
}
