package parser

import org.scalatestplus.play.PlaySpec

class StationToJsonSpec extends PlaySpec {

  val generator = new StationToJsonImpl;
  "StationToJsonSpec  " must {
    "No stations empty list " in {
      generator.formatStationsToJson(List()).toString mustBe """{"stations":[]}"""
    }

    "One station " in {
      val stations = List(Station(99, "ABF", 3, 4))
      val expectedResult = """{"stations":[{"id":99,"title":"ABF","bikes":3,"locks":4}]}"""
      generator.formatStationsToJson(stations).toString mustBe expectedResult
    }

    "One station no title" in {    
      a[java.lang.IllegalArgumentException] must be thrownBy {
         val stations = List(Station(99, "", 3, 4))
      }
    }

    "Tree stations " in {
      val stations = List(
        Station(99, "ABF", 1, 6),
        Station(22, "one", 5, 3),
        Station(33, "tree", 9, 2))
      val expectedResult = """{"stations":[""" +
        """{"id":99,"title":"ABF","bikes":1,"locks":6},""" +
        """{"id":22,"title":"one","bikes":5,"locks":3},""" +
        """{"id":33,"title":"tree","bikes":9,"locks":2}""" +
        """]}"""
      generator.formatStationsToJson(stations).toString mustBe expectedResult
    }

  }
}