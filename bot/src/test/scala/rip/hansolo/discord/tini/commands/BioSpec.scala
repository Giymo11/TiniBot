package rip.hansolo.discord.tini.commands

import org.scalatest.WordSpec

/**
  * Created by Giymo11 on 11.08.2016.
  */
class BioSpec extends WordSpec {

  "Bio.unapply" should {
    "return Some" when {
      "the argument starts with !bio" in {
        assert(Bio.unapply("bio").isDefined)
        assert(Bio.unapply("bio help").isDefined)
        assert(Bio.unapply("bio set bla bla").isDefined)
        assert(Bio.unapply("bio \nset bla \nbla").isDefined)
        assert(Bio.unapply("bio\nset\nbla\nbla").isDefined)
        assert(Bio.unapply("bio @mention").isDefined)
      }
    }
    "should stip the !bio and whitespacefrom reply" in {
      assert(Bio.unapply("bio").contains(""))
      assert(Bio.unapply("bio help").contains("help"))
      assert(Bio.unapply("bio set bla bla").contains("set bla bla"))
      assert(Bio.unapply("bio       set bla bla      ").contains("set bla bla"))
      assert(Bio.unapply("bio \nset bla bla").contains("set bla bla"))
      assert(Bio.unapply("bio \n   set bla bla").contains("set bla bla"))
      assert(Bio.unapply("bio @mention").contains("@mention"))
    }
    "return None" when {
      "the argument is invalid" in {
        assert(Bio.unapply("hellO?").isEmpty)
        assert(Bio.unapply("not !bio").isEmpty)
      }
    }
  }

  "Bio.Set.unapply" should {
    "return Some" when {
      "the argument starts with set" in {
        assert(Bio.Set.unapply("set bla bla").isDefined)
        assert(Bio.Set.unapply("set \nbla\nbla").isDefined)
        assert(Bio.Set.unapply("set\nbla\nbla").isDefined)
      }
    }
    "return None" when {
      "the argument is invalid" in {
        assert(Bio.Set.unapply("").isEmpty)
        assert(Bio.Set.unapply("not set").isEmpty)
      }
    }
  }

  "Bio.Get.unapply" should {
    "return Some" when {
      "the argument starts with @mention" in {
        assert(Bio.Get.unapply("@mention").isDefined)
      }
    }
    "return None" when {
      "the argument is invalid" in {
        assert(Bio.Get.unapply("").isEmpty)
        assert(Bio.Get.unapply("not @mention").isEmpty)
      }
    }
  }


}
