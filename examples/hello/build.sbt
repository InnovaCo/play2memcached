name := "hello"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  "eu.inn" %% "play2memcached" % "0.1.1"
)     

play.Project.playScalaSettings
