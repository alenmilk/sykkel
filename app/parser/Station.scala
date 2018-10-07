package parser

case class Station(id: Long, title: String, bikes: Long, locks: Long) {
  require(title!=null && title!="")
}


