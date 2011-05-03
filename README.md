SodaTest: Spreadsheet-Driven Integration Testing
================================================

SodaTest is an open-source framework for Integration and Acceptance testing.

SodaTest allows the creation of executable test cases as spreadsheets in a format that is easily
readable by non-programmers, with the goal of being easily understood, edited or even authored by the
non-technical Customers of the software under test.

The input format is CSV files, the output format is pretty HTML, and the programming model in between
for creating [fixtures](http://en.wikipedia.org/wiki/Test_fixture#Software) is kept as simple as possible.

SodaTest is written primarily in [Scala](http://www.scala-lang.org/).
While it will likely be most easy (and fun!) to write Fixtures using Scala, in theory SodaTest can
be used to test any software that runs on the Java Virtual Machine.
The ability to easily write Fixtures for SodaTest using nothing but **Java** is a chief aim of the project.


Motivation
----------

SodaTest was begun as an exercise to create a test framework that improves on Ward Cunningham's
[Framework for Integration Testing, "FIT"](http://fit.c2.com/).

The core design of SodaTest focusses around resolving a number of niggles experienced with FIT over
the years, namely:

* HTML as an input format is annoying for developers to manage
* HTML as an input format prevents Customers from getting involved in test writing
* Formatting test output based on the input formatting results in ugly and inconsistent test output
* The very flexible API brings developers too close to the input format while making poor practices possible
* The depth of the ecosystem of FIT, fitlibrary and Fitnesse can confuse developers trying to achieve something simple
* Mixing classes from across the fractured ecosystem results in unresolvable classpath errors

In order to resolve the input format problems, spreadsheets were chosen (in the form of CSV files, at
present) due to spreadsheets being "the language of business" (not accounting, as some would have you
think).
The simplicity of creating spreadsheets on any platform sealed the deal.

Good ideas froms FIT which are maintained in SodaTest are:

* The use of tables to format and structure lots of information
* Using reflection to automate a lot of string-conversion boilerplate for the fixture author
* HTML as an excellent format for test output

Other things that SodaTest tries to achieve are:

* [Command-Query Separation](http://en.wikipedia.org/wiki/Command-query_separation) is built into the
  API by making the distinction between Events, which cause side-effects within the System, and Reports,
  which merely query the state of the System.
* Powerful and flexible (yet simple!) coercion of strings to strong types, including support for
  co-located [PropertyEditor](http://download.oracle.com/javase/6/docs/api/java/beans/PropertyEditor.html) implementations
* Simple and localised control of Report formatting from strong types to strings (**Not done yet**)
* Case-agnosticism when binding input strings to programmattic symbols


Project Sections
----------------

The SodaTest project is made up of the following modules:

* [SodaTest API](https://github.com/GrahamLea/SodaTest/tree/master/sodatest-examples)
  is the only module on which your test code should depend at compile-time.
  The `org.sodatest.api` package contains the traits to be implemented in order to implement fixtures,
  though the 'Reflective*' traits in the `org.sodatest.api.reflection` package are what you will
  probably want to use 99% of the time.

* [SodaTest Coercion](https://github.com/GrahamLea/SodaTest/tree/master/coercion)
  is a module used by the reflection parts of the API to coerce strings into strong types

* [SodaTest Runtime](https://github.com/GrahamLea/SodaTest/tree/master/sodatest-runtime)
  contains applications that can be used to execute SodaTests.
  The SodaFolderRunner class in the org.sodatest.runtime.processing.running is currently the main
  entry point for running tests.

* [SodaTest Examples](https://github.com/GrahamLea/SodaTest/tree/master/sodatest-examples)
  contains examples of how to use different features of SodaTest.


Tasks on the Roadmap
--------------------

A roadmap for features that need to be implemented to make the framework more complete is listed in Roadmap.txt.
If you think you'd like to try your hand at helping out with some of this stuff, get in touch!


Licence
-------

SodaTest is Copyright (c) 2010-2011 Belmont Technology Pty Ltd.

It is licensed under the Apache License, Version 2.0 (the "License")
You may obtain a copy of the License at [http://www.apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0)

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.


Copyright (c) 2011 Belmont Technology Pty Ltd. All rights reserved.
