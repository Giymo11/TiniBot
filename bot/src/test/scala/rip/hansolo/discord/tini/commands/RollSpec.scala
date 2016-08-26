package rip.hansolo.discord.tini.commands


import org.scalatest.{FlatSpec, WordSpec}


/**
  * Created by Giymo11 on 11.08.2016.
  */
class RollSpec extends WordSpec {

  "unapply" should {
    "return None" when {
      "the command is not a roll" in {
        assert(Roll.unapply("not a roll command").isEmpty)}
    }
    "return Some" when {
      "the command is a Dnd roll" in {
        assert(Roll.unapply("roll 2d6").isDefined)}
      "the command is a single argument roll" in {
        assert(Roll.unapply("roll 4").isDefined)}
      "the command is a roll with a lower and upper bound" in {
        assert(Roll.unapply("roll 4 10").isDefined)}
    }
  }
  "rollTheDice" when {
    "the parameter is a DnD roll" should {
      "return the correct number of results" in {
        assert(Roll.rollTheDice(List("3d6")).size == 3)}
      "return only results between 1 and <sides>" is pending
      "return no results for incorrect parameters" is pending
    }
    "the parameter is a roll with only upper bound" should {
      "return the correct number of results" in {
        assert(Roll.rollTheDice(List("1")).size == 1)}
      "return only results between 1 and <upper>" is pending
      "return empty results for incorrect parameters" is pending
    }
    "the parameter is a roll with upper and lower bounds" should {
      "return the correct number of results" in {
        assert(Roll.rollTheDice(List("1", "10")).size == 1)}
      "return only results between <lower> and <upper>" is pending
      "return empty results for incorrect parameters" is pending
    }
  }

}
