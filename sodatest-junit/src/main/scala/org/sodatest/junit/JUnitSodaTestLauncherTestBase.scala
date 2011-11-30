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
 * An abstract test base class which, when subclassed, instructs JUnit to execute all SodaTests
 * in and under the package that contains the subclass.
 *
 * The subclasses to be placed in the test tree do not need to define any test methods.
 * Here is an example of a typical subclass:
 * {{{
 * class MyApplicationSodaTestLauncherTest extends JUnitSodaTestLauncherTestBase
 * }}}
 *
 * To make use of this class, first place your SodaTests under src/test/sodatest. There are then two
 * ways to organise the execution of your tests...
 *
 * If you want to run all your tests in one suite, you can just place one subclass of
 * JUnitSodaTestLauncherTestBase directly under src/test/scala.
 * By default, the output of the tests will be written under target/sodatest-junit in the working
 * directory.
 *
 * To run your tests as multiple suites, you should place subclasses of JUnitSodaTestLauncherTestBase in
 * packages under src/test/scala that correspond to parts of the tree under src/test/sodatest
 * containing the tests.
 *
 * The advantage of running your tests as multiple suites is that it is easy to run a subset of your SodaTests.
 * If you go the single-suite route, then to run any one of your SodaTests you will have to run them all.
 *
 * Test subclasses can be annotated with {org.sodatest.junit.JUnitSodaTestLauncherFixtureRoot}
 * to specify the root search package for fixtures.
 * By convention, the Runner will look for fixtures using the package of the JUnitSodaTestLauncherTestBase
 * subclass as the fixture root.
 *
 * You can also override any of the three annotations applied to this class in order to change parameters affecting the
 * execution of your tests.
 *
 * Lastly, if you'd like to increase or decrease the amount of information that SodaTest outputs when running through JUnit,
 * you can set the "JUnitSodaTestRunner.log.level" system property to one of Error, Info (the default), Debug or Verbose.
 *
 * The sodatest-examples/sodatest-examples-junit module contains an example of using JUnitSodaTestLauncherTestBase to run SodaTests
 * in multiple suites.
 */
@RunWith(classOf[JUnitSodaTestRunner])
@JUnitSodaTestLauncherBaseDir
@JUnitSodaTestLauncherFilePattern
@JUnitSodaTestLauncherOutputDirectory
abstract class JUnitSodaTestLauncherTestBase

