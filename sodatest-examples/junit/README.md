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
To run your tests as multiple suites, you place subclasses of JUnitSodaTestLauncherTestBase in
packages under src/test/scala that correspond to parts of the tree under src/test/sodatest
containing the tests.
If you want to run all your tests in one suite, you can just place one subclass of
JUnitSodaTestLauncherTestBase directly under src/test/scala.
By default, the output of the tests will be written to target/sodatest-junit in the working directory.

If you want to change the base directory, file pattern or output directory, you can override the
annotations on JUnitSodaTestLauncherTestBase in your own subclass.
