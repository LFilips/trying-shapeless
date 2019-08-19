name := "trying-shapeless"

version := "0.1"

scalaVersion := "2.13.0"

resolvers ++= Seq(
  Resolver.sonatypeRepo("releases"),
  Resolver.sonatypeRepo("snapshots")
)

libraryDependencies ++= Seq(
  "com.chuusai" %% "shapeless" % "2.3.3",
  "io.spray" %  "spray-json_2.12" % "1.3.4",
  "org.scalatest" %% "scalatest" % "3.0.8" % Test
)


