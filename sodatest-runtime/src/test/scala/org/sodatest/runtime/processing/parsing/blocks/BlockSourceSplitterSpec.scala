package org.sodatest.runtime
package processing.parsing.blocks

import data.blocks.BlockSource
import data.blocks.Line
import org.specs.{Specification, SpecificationWithJUnit}

class BlockSourceSplitterSpec extends SpecificationWithJUnit {

  implicit val log = NoOpLog
  val blockParser = new BlockSourceSplitter

  def C(c: String, v: String): String = v

  "BlockSourceSplitter" should {

    "parse blocks" in {
      val blocks = blockParser.parseBlocks(List(
        List("Fixture", "TestFixtureName"),
        List(""),
        List("Note", "Some Text"),
        List("", "Some more text"),
        List(""),
        List("Junk", "What's this?"),
        List("", "Who knows - still a blocks!"),
        List(""),
        List("Event", "Basic Event"),
        List(""),
        List("Report", "Basic Report", "!!"),
        List("", "Text"),
        List(""),
        List("Event", "Parameterised Events"),
        List("", "Parameter 1", "Parameter 2"),
        List("", "argument one", "argument two"),
        List("", "argument one2", "argument two2"),
        List(""),
        List("Report", "Parameterised Inline Report"),
        List("", "Parameter 1", "Parameter 2", "!!"),
        List("", "argument one", "argument two", "some result"),
        List("", "argument one2", "argument two2", "some other result"),
        List(" "),
        List("Report", "Parameterised Report"),
        List("", "Parameter 1", "Parameter 2", ""),
        List("", "argument one", "argument two", " "),
        List("", "some result", " ", "  "),
        List("!!", "argument one2", "argument two2"),
        List("", "some other result", "with a second column"),
        List("", "and a second row")
       ))

      blocks(0) must_== (
           BlockSource(List(
              Line(1, List(C("A", "Fixture"), C("B", "TestFixtureName")))
           )))

      blocks(1) must_== (
           BlockSource(List(
              Line(3, List(C("A", "Note"), C("B", "Some Text"))),
              Line(4, List(C("A", ""), C("B", "Some more text")))
           )))

      blocks(2) must_== (
           BlockSource(List(
              Line(6, List(C("A", "Junk"), C("B", "What's this?"))),
              Line(7, List(C("A", ""), C("B", "Who knows - still a blocks!")))
           )))

      blocks(3) must_== (
           BlockSource(List(
              Line(9, List(C("A", "Event"), C("B", "Basic Event")))
           )))

      blocks(4) must_== (
           BlockSource(List(
              Line(11, List(C("A", "Report"), C("B", "Basic Report"), C("C", "!!"))),
              Line(12, List(C("A", ""), C("B", "Text")))
           )))

      blocks(5) must_== (
           BlockSource(List(
              Line(14, List(C("A", "Event"), C("B", "Parameterised Events"))),
              Line(15, List(C("A", ""), C("B", "Parameter 1"), C("C", "Parameter 2"))),
              Line(16, List(C("A", ""), C("B", "argument one"), C("C", "argument two"))),
              Line(17, List(C("A", ""), C("B", "argument one2"), C("C", "argument two2")))
           )))

      blocks(6) must_== (
           BlockSource(List(
              Line(19, List(C("A", "Report"), C("B", "Parameterised Inline Report"))),
              Line(20, List(C("A", ""), C("B", "Parameter 1"), C("C", "Parameter 2"), C("D", "!!"))),
              Line(21, List(C("A", ""), C("B", "argument one"), C("C", "argument two"), C("D", "some result"))),
              Line(22, List(C("A", ""), C("B", "argument one2"), C("C", "argument two2"), C("D", "some other result")))
           )))

      blocks(7) must_== (
           BlockSource(List(
              Line(24, List(C("A", "Report"), C("B", "Parameterised Report"))),
              Line(25, List(C("A", ""), C("B", "Parameter 1"), C("C", "Parameter 2"))),
              Line(26, List(C("A", ""), C("B", "argument one"), C("C", "argument two"))),
              Line(27, List(C("A", ""), C("B", "some result"))),
              Line(28, List(C("A", "!!"), C("B", "argument one2"), C("C", "argument two2"))),
              Line(29, List(C("A", ""), C("B", "some other result"), C("C", "with a second column"))),
              Line(30, List(C("A", ""), C("B", "and a second row")))
           )))

      blocks.length must_== 8
    }
  }

}