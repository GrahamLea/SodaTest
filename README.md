SodaTest: Spreadsheet-Driven Integration Testing
================================================

SodaTest is an open-source framework for creating Executable Requirements for Integration, Functional and Acceptance testing.

SodaTest allows the creation of executable test cases as spreadsheets in a format that is easily
readable by non-programmers, with the goal of being easily understood, edited or even authored by the
non-technical Customers of the software under test.

The input format is CSV files, the output format is pretty HTML, and the programming model in between for
creating [Fixtures](http://en.wikipedia.org/wiki/Test_fixture#Software) is kept as simple as possible.

SodaTest is written primarily in [Scala](http://www.scala-lang.org/).
While it will likely be most easy (and fun!) to write Fixtures using Scala, in theory SodaTest can
be used to test any software that runs on the Java Virtual Machine.
The ability to easily write Fixtures for SodaTest using nothing but **Java** is a chief aim of the project.


Motivation
----------

SodaTest was begun as an exercise to create a test framework that improves on Ward Cunningham's
[Framework for Integration Testing, "FIT"](http://fit.c2.com/).
As an 'Executable Requirements' tool, it can also be considered as an alternative to
[Fitnesse](http://fitnesse.org/),
[Concordion](http://www.concordion.org/),
[RSpec](http://rspec.info/),
[Cucumber](http://cukes.info/),
[JDave](http://jdave.org/),
[JBehave](http://jbehave.org/),
[SpecFlow](http://www.specflow.org/),
and [Thoughtworks' Twist](http://www.thoughtworks-studios.com/agile-test-automation).

The core design of SodaTest is focused around resolving a number of niggles experienced with FIT over
the years, namely:

* HTML as an input format is annoying for developers to manage
* HTML as an input format prevents Customers from getting involved in test writing
* Formatting test output based on the input formatting results in ugly and inconsistent test output
* An overly-flexible API can make poor practices possible and bring developers too close to the input format
* The depth of the ecosystem of FIT, fitlibrary and Fitnesse can confuse developers trying to achieve something simple
* Mixing classes from across the fractured ecosystem results in unresolvable classpath errors

In order to resolve the input format problems, spreadsheets were chosen as the input format,
in the form of CSV files, for these reasons:

1. Spreadsheets are "the language of business" (not accounting, as some would have you think);
2. Creating and editing spreadsheets is dead easy on any platform;
3. Spreadsheets (especially in CSV format) are extremely portable across platforms;
4. CSV files are very easy to edit in source format.

Some great ideas from FIT which are maintained in SodaTest are:

* Tables for giving format and structure to large amounts of information
* Reflection for automating a lot of string-conversion boilerplate for the Fixture author
* HTML as an excellent format for test output

Other things that SodaTest tries to achieve are:

* [Command-Query Separation](http://en.wikipedia.org/wiki/Command-query_separation) is built into the
  API by making the distinction between Events, which cause side-effects within the System, and Reports,
  which only query the state of the System.
* Powerful and flexible (yet simple!) coercion of strings to strong types
* Simple and localised control of Report formatting from strong types to strings (**Not done yet**)
* Less strictness when binding input strings to programmattic symbols, e.g. case-agnosticism


Screenshots and Code Samples
----------------------------

Here is a [screenshot of a SodaTest input spreadsheet](https://github.com/GrahamLea/SodaTest/raw/master/images/Basic%20Example%20Source%20Screenshot.png "Screenshot of SodaTest input spreadsheet"):

[![Screenshot of a SodaTest input spreadsheet](https://github.com/GrahamLea/SodaTest/raw/master/images/Basic%20Example%20Source%20Screenshot%20Thumbnail.png)](https://github.com/GrahamLea/SodaTest/raw/master/images/Basic%20Example%20Source%20Screenshot.png)

Here is a [screenshot of SodaTest's output HTML for the above input](https://github.com/GrahamLea/SodaTest/raw/master/images/Basic%20Example%20Output%20Screenshot.png "Screenshot of SodaTest's output HTML"):

[![Screenshot of SodaTest's output HTML](https://github.com/GrahamLea/SodaTest/raw/master/images/Basic%20Example%20Output%20Screenshot%20Thumbnail.png)](https://github.com/GrahamLea/SodaTest/raw/master/images/Basic%20Example%20Output%20Screenshot.png)

Here is a sample of the type of Fixture code used to execute the above test:

(You can browse the [full SodaTest Basic Example here](https://github.com/GrahamLea/SodaTest/tree/master/sodatest-examples/basic)
and you can read the [full source of BankAccountFixture.scala here](https://github.com/GrahamLea/SodaTest/blob/master/sodatest-examples/basic/src/main/scala/org/sodatest/examples/basic/fixtures/BankAccountFixture.scala).)

    class BankAccountFixture extends ReflectiveSodaFixture {
      val service = new BankAccountService()
      def openAccount = new OpenAccountEvent(service)
      def balance = new BalanceReport(service)
    }

    class OpenAccountEvent(val service: BankAccountService) extends ReflectiveSodaEvent {

      val coercionRegister = new CoercionRegister(InterestFormulaCoercion)

      var accountName: AccountName = null;
      var initialDeposit: Option[Money] = None;
      var tags: List[String] = Nil;
      var interestFormula: InterestFormula = null;

      def apply() {
        val newAccount: BankAccount = new BankAccount(accountName, tags, interestFormula)
        service.accountsByName += accountName -> newAccount
        initialDeposit match {
          case Some(amount) => newAccount.deposit(amount)
          case None =>
        }
      }
    }

    class BalanceReport(val service: BankAccountService) extends ReflectiveSodaReport {
      var accountName: AccountName = null;

      def apply(): Seq[Seq[String]] = {
        service.accountsByName.get(accountName) match {
          case Some(account: BankAccount) => account.balance.toSingleCellReport
          case None => "Unknown Account".toSingleCellReport
        }
      }
    }

    object InterestFormulaCoercion extends Coercion[InterestFormula] {
      def apply(s: String) = InterestFormula.fromString(s)
    }


Project Sections
----------------

The SodaTest project is made up of the following modules:

* [SodaTest API](https://github.com/GrahamLea/SodaTest/tree/master/sodatest-examples)
  is the only module on which your test code should depend at compile-time.
  The `org.sodatest.api` package contains the traits to be implemented in order to implement Fixtures,
  though the `Reflective*` traits in the `org.sodatest.api.reflection` package are what you will
  probably want to use 99% of the time.

* [Coercion](https://github.com/GrahamLea/SodaTest/tree/master/coercion)
  is a module used by the reflection parts of the API to coerce strings into strong types

* [SodaTest Runtime](https://github.com/GrahamLea/SodaTest/tree/master/sodatest-runtime)
  contains applications that can be used to execute SodaTests.
  The SodaFolderRunner class in the org.sodatest.runtime.processing.running is currently the main
  entry point for running tests.

* [SodaTest Examples](https://github.com/GrahamLea/SodaTest/tree/master/sodatest-examples)
  contains examples of how to use different features of SodaTest.


Tasks on the Roadmap
--------------------

A roadmap of features that it might make sense to add in the medium termis listed
in [Roadmap.md](https://github.com/GrahamLea/SodaTest/blob/master/Roadmap.md).

If you think you'd like to try your hand at helping out with some of this stuff, get in touch!


Licence
-------

SodaTest is Copyright (c) 2010-2011 Belmont Technology Pty Ltd.

It is licensed under the Apache License, Version 2.0 (the "License")
You may obtain a copy of the License in the root directory of the project or at [http://www.apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0)


Disclaimer
----------

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.


Copyright (c) 2011 Belmont Technology Pty Ltd. All rights reserved.
