SodaTest Roadmap
================

The SodaTest project is definitely in its infantile stages at present.

These are the things that make the most sense to achieve in the near future, listed in rough priority order.

If you think you'd like to try your hand at helping out with some of this stuff, get in touch!


Sanitise test input and output for XHTML
----------------------------------------
Any non-framework text that appears in the output should be sanitised so as to not open up to
HTML injection attacks.


Parsing errors commuted to output
---------------------------------
Errors in parsing the input currently cause exceptions that halt execution.
Instead, meaningful output should always be generated that makes it easy to locate the source and cause of the failure.


SodaFolderRunner output an index page
-------------------------------------
The SodaFolderRunner should output, at the root of the output directory, an index.html that lists all of the tests
that were run and a summary of the results.


SodaFileRunner should have a main() method
------------------------------------------
It should be possible to use the file runner to execute a single test by name.


Support for automatic formatting of report output to strings
------------------------------------------------------------
The interface for SodaReports requires apply() to return a List[List[String]]
While this shouldn't change, it should be possible to return a List[List[Any]] and have SodaTest sensibly convert
the value to a List[List[String]], primarily using convention but also allowing configuration. Thinking is that a
val of type Map[Class[_], Formatter] could be provided in the Report and/or in the Fixture.


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


Java Example
------------
The Java example is 95% done, however the InterestFormulaJavaCoercion is written in Scala.
Need to figure out how to create a Manifest from Java in order to be able to implement the
Coercion in Java instead.
It would probalby be wise to solve the Coercion limitation before fixing this.


More appropriate logging level out of the box
---------------------------------------------
At them moment, there is a lot of output even for a single file. This is good for development but should change
for the long term such that only the name of the file being processed and its result be output at Info level.


Shortcut for single-parameter blocks
------------------------------------
Because of the prevalence of single-parameter Events and Reports, it would be nice to have a succinct way to
define them, i.e. without taking up three lines to define an execution. For Events the format would be:

    Event,<Event Name>,<Single Parameter>

and for Reports it would be

    Report,<Report Name>,<Single Parameter>,!!

The value of this parameter will be provided to the block implementation using a default name defined in the API.
The Reflective APIs will need a way to bind this parameter to one particular field. (Annotation?)


Allow processing at the end of a multiple-execution Event
---------------------------------------------------------
Some Events will require what is essentially a table of input before they perform any changes to the System Under Test.
One example would be an Event that writes a multi-line file with an auto-calculated tralier record.
This essentially requires knowledge of when the last execution in an Event block has occurred, so a good way to model
it may be to allow Listeners on the Fixture that are notified of the beginning and end of Event blocks.


JUnit integration & example
---------------------------
As there has been a lot of investment in JUnit integration elsewhere, e.g. in build tools and CI servers, it might
be good to leverage these capabilities by allowing SodaTests to be executed by JUnit somewhow.


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
