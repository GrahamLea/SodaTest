SodaTest Roadmap
================

The SodaTest project is definitely in its infantile stages at present.

These are the things that make the most sense to achieve in the near future, listed in rough priority order.

If you think you'd like to try your hand at helping out with some of this stuff, get in touch!


SodaFileRunner should have a main() method
------------------------------------------
It should be possible to use the file runner to execute a single test by name.


Extend formatting support for report output
-------------------------------------------
Helper methods in SodaReport and JavaReportConverter now aid fixture authors in getting from
Objects to two-dimensional String collections, though these are pretty rudimentary.
Some things that could be done to extend the usefulness would be:
* Interpret things like None and null as empty string instead of literally
* When handling Some(object), use the toString on object
* Allow the (implicit) provision of a list or Map of formatters


Required Parameters
-------------------
It is obvious that for many Events and Reports, some parameters will be required in order to
apply the Event or Report properly. It would be nice to have functionality built into the API
allowing a parameter to be annotated as required and have the framework ensure all required
parameters have been set before execution.


Coercion limitation
------------------
The current implementation of Coercion, which uses only the 'erasure' from the Manifest,
is probably not able to match types at runtime as powerfully as it should be.
The test case is probably to create two coercions for two different concrete types of a single
generic type.


Coercions in Java
-----------------
The Java API and example is 95% done, however the InterestFormulaJavaCoercion is written in Scala.
Need to figure out how to create a Manifest from Java in order to be able to implement the
Coercion in Java instead.
It would probalby be wise to solve the Coercion limitation above before fixing this.


Shortcut for single-parameter blocks
------------------------------------
Because of the prevalence of single-parameter Events and Reports, it would be nice to have a succinct way to
define them, i.e. without taking up three lines to define an execution. For Events the format would be:

    Event,<Event Name>,<Single Parameter>

and for Reports it would be

    Report,<Report Name>,<Single Parameter>,!!

The value of this parameter will be provided to the block implementation using a default name defined in the API.
The Reflective APIs will need a way to bind this parameter to one particular field. (Annotation?)
Also think about ParameterisedInlineReport, as documented in BlockFactorySpec


Allow processing at the end of a multiple-execution Event
---------------------------------------------------------
Some Events will require what is essentially a table of input before they perform any changes to the System Under Test.
One example would be an Event that writes a multi-line file with an auto-calculated tralier record.
This essentially requires knowledge of when the last execution in an Event block has occurred, so a good way to model
it may be to allow Listeners on the Fixture that are notified of the beginning and end of Event blocks.


Spring integration & example
----------------------------
This may be as simple as providing an example of best practice for writing Fixtures against Spring apps, but it
might require some new components to make integration possible without boilerplate.
Also need to think about a Spring+JUnit integration combo.


Maven plugin
------------
It would be good for people to be able to run SodaTests in Maven out of the box without having to use either JUnit
integration or the Exec plugin.


Copyright (c) 2011 Belmont Technology Pty Ltd. All rights reserved.
