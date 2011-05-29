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

package org.sodatest.junit

import org.junit.runner.RunWith

/**
 * An abstract test base class which all clients of JUnit SodaTest execution should subclass.
 *
 * To use this class, first place your SodaTests under src/test/sodatest. There are then two
 * ways to organise your tests...
 *
 * If you want to run all your tests in one suite, you can just place one subclass of
 * JUnitSodaTestLauncherTestBase directly under src/test/scala.
 * By default, the output of the tests will be written under target/sodatest-junit in the working
 * directory.
 *
 * To run your tests as multiple suites, you should place subclasses of JUnitSodaTestLauncherTestBase in
 * packages under src/test/scala that correspond to parts of the tree under src/test/sodatest
 * containing the tests. By convention, the Runner will look for fixtures using the package of this
 * JUnitSodaTestLauncherTestBase subclass as the fixture root.
 *
 * You can override any of the three annotations applied to this class, and also apply the
 * @link JUnitSodaTestLauncherFixtureRoot} annotation, in order to change parameters affecting the
 * execution of your tests.
 */
@RunWith(classOf[JUnitSodaTestRunner])
@JUnitSodaTestLauncherBaseDir
@JUnitSodaTestLauncherFilePattern
@JUnitSodaTestLauncherOutputDirectory
abstract class JUnitSodaTestLauncherTestBase

