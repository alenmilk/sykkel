package parser

import org.scalatestplus.play.PlaySpec
import play.api.libs.json.Json
import scala.util.Try
import play.api.libs.json.JsResultException

class BikeJsonParserSpec extends PlaySpec {

  "BikeJsonParserSpec  " must {
    val parser = new BikeJsonParserImpl;

    "Titles Empty json " in {
      a[JsResultException] must be thrownBy {
        val titles = parser.parseStationTitles(Json.parse("{}"))
      }
    }

    "Titles Empty stations " in {
      val titles = parser.parseStationTitles(Json.parse("""{"stations":[]}"""))
      titles.isEmpty mustBe true
    }

    "Titles one station " in {
      val titles = parser.parseStationTitles(
        Json.parse(
          """{
            "stations":[{
                          "id": 210,
                          "title": "Birkelunden"}
                        ]
              }"""))
      titles.isEmpty mustBe false
      titles.get(210).get mustBe "Birkelunden"
    }
    
    "Titles one station duplicate " in {
      val titles = parser.parseStationTitles(
        Json.parse(
          """{
            "stations":[{
                          "id": 210,
                          "title": "Birkelunden"
                        },
                        {
                          "id": 210,
                          "title": "Birkelunden2"
                        }
                        ]
              }"""))
      titles.size mustBe 1
      titles.get(210).get mustBe "Birkelunden2"
    }
    
    "Titles three stations" in {
      val titles = parser.parseStationTitles(
        Json.parse(
          """{
            "stations":[{
                          "id": 1,
                          "title": "A"
                        },
                        {
                          "id": 2,
                          "title": "B"
                        },
                        {
                          "id": 3,
                          "title": "C"
                        }
                        ]
              }"""))
      titles.size mustBe 3
      titles.get(1).get mustBe "A"
      titles.get(2).get mustBe "B"
      titles.get(3).get mustBe "C"
    }

    "Available Empty json " in {
      a[JsResultException] must be thrownBy {
        val available = parser.parseAvailableStations(Json.parse("{}"), Map())
      }
    }

    "Available Empty stations " in {
      val available = parser.parseAvailableStations(Json.parse("""{"stations":[]}"""), Map())
      available.isEmpty mustBe true
    }

    "Available one station but no title" in {
      val available = parser.parseAvailableStations(
        Json.parse(
          """{
            "stations":[{
                          "id": 210,
                          "availability": {
                             "bikes" : -1,
                             "locks" : -1 
                             }
                        }]
              }"""), Map())
      available.isEmpty mustBe true
    }
    
    "Available one station with title " in {
      val available = parser.parseAvailableStations(
        Json.parse(
          """{
            "stations":[{
                          "id": 210,
                          "availability": {
                             "bikes" : 3,
                             "locks" : 4 
                             }
                        }]
              }"""), Map(210.toLong -> "Title"))
      available(0).title mustBe "Title"
      available(0).bikes mustBe 3
      available(0).locks mustBe 4
    }

  }
}
