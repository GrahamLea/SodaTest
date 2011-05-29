SodaTest JUnit Integration Example
======================

This example shows a pattern for using JUnit integration to execute SodaTests.

Running the Example
-------------------

The JUnit Integration Example is executed in the test phase of the Maven build by the normal Surefire plugin.

If you are using IntelliJ IDEA, there is a Run Configuration called 'Package org.sodatest.examples.junit'
that should allow you to run the tests from within the IDE.


What the Example Demonstrates
-----------------------------

You can see in the src/test/scala tree how you can have SodaTests initiated by JUnit.
The method is quite simple:

Firstly, place your SodaTests under src/test/sodatest

If you want to run all your tests in one suite, you can just place one subclass of
JUnitSodaTestLauncherTestBase directly under src/test/scala.
By default, the output of the tests will be written under target/sodatest-junit in the working directory.

To run your tests as multiple suites, you should place subclasses of JUnitSodaTestLauncherTestBase in
packages under src/test/scala that correspond to parts of the tree under src/test/sodatest
containing the tests. By convention, the Runner will look for fixtures using the package of this
JUnitSodaTestLauncherTestBase subclass as the fixture root.

The following annotations can be applied to your JUnitSodaTestLauncherTestBase subclasses in order
to configure various test execution parameters.

@JUnitSodaTestLauncherFilePattern
Specifies a Java Pattern that will be used to search for test input files.
Default: *\.csv

@JUnitSodaTestLauncherBaseDir
Specifies the root directory, relative to the working directory, where the Runner will search
for files matching the FilePattern to use as test inputs.
Default: src/test/sodatest

@JUnitSodaTestLauncherFixtureRoot
Specifies the package under which Fixture names should be resolved.
By default, the same package as the JUnitSodaTestLauncherTestBase subclass will be used.
If this package is the same for all tests in the project, but you are using the "multiple suites"
method, you will probably want to specify this package in a single abstract test base that all other
suites inherit from. (You can see an example of this in this package.)

@JUnitSodaTestLauncherOutputDirectory
Specifies the root directory under which the output of tests will be written, either absolutely or relative to the working directory.
Default: target/sodatest-junit/
