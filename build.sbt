name := """Sykkelapp"""
	
version := "1.0-SNAPSHOT"


lazy val root = (project in file(".")).enablePlugins(PlayScala)


scalaVersion := "2.12.3"


libraryDependencies ++= Seq(
  ws,
  guice,
  "org.mockito" % "mockito-core" % "2.21.0" % "test",
  "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.0" % "test"
)

EclipseKeys.withSource := true
EclipseKeys.withJavadoc := true