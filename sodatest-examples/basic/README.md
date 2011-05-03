SodaTest Basic Example
======================

This basic example shows most of the typical things you would do with SodaTest.

Some of the elements covered are...

* The basics of writing a SodaTest in a spreadsheet, including:
  * Selecting a Fixture
  * Inline Events
  * Inline Reports
  * Parameterised Events
  * Parameterised Reports
  * Multiple Executions of a parameterised Event or Report

* How to create a SodaFixture using `ReflectiveSodaFixture`, `ReflectiveSodaEvent` and `ReflectiveSodaReport`

* Demonstration of some of SodaTest's coercion capabilities for reflective binding, namely:
  * Binding by `String` constructor    (c.f. `class AccountName`)
  * Binding by `PropertyEditor`        (c.f. classes `Money` and `MoneyEditor`)
  * Binding to an `Option`             (c.f. `OpenAccountEvent.initialDeposit`)
  * Binding to a `List`                (c.f. `OpenAccountEvent.tags`)
  * Binding using a custom `Coercion`  (c.f. `OpenAccountEvent.interestForumla`)
