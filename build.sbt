name := """Sykkelapp"""
	
version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).settings(
	name:= "Hello"
)


scalaVersion := "2.12.3"
libraryDependencies +=  "org.scalaj" %% "scalaj-http" % "2.4.1"
libraryDependencies += "net.liftweb" %% "lift-json" % "3.3.0"

EclipseKeys.withSource := true
EclipseKeys.withJavadoc := true