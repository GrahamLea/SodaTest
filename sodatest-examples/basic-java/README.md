SodaTest Basic Example
======================

This basic example shows most of the typical things you would do with SodaTest.

Running the Example
-------------------

The Basic Example is executed in the test phase of the Maven build.
You can look at the pom.xml for an example of how to execute SodaTests from Maven using the [Exec Maven Plugin](http://mojo.codehaus.org/exec-maven-plugin/examples/example-exec-for-java-programs.html).

If you are using IntelliJ IDEA, there is a Run Configuration called 'Basic Example' that should allow
you to run the test from within the IDE.

What the Example Demonstrates
-----------------------------

Some of the elements of SodaTest demonstrated by the Basic Example are...

* The formats for writing a SodaTest in a spreadsheet, including:
  * Selecting a Fixture
  * Inline Events
  * Inline Reports
  * Parameterised Events
  * Parameterised Reports
  * Multiple Executions of a parameterised Event or Report

* How to create a SodaFixture using `ReflectiveSodaFixtureForJava`, `ReflectiveSodaEventForJava` and `ReflectiveSodaReportForJava`
