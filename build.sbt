name := "play2memcached"

version := "0.1"

organization := "eu.inn"

resolvers ++= Seq("Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/")

libraryDependencies += "net.spy" % "spymemcached" % "2.10.3"

libraryDependencies += "com.typesafe.play" %% "play" % "2.2.2-RC1"

libraryDependencies += "com.typesafe.play" %% "play-cache" % "2.2.2-RC1"

libraryDependencies += "org.scalatest" % "scalatest_2.10" % "2.0.RC1" % "test"

libraryDependencies += "org.mockito" % "mockito-all" % "1.9.5" % "test"

libraryDependencies += "com.typesafe.play" %% "play-test" % "2.2.2-RC1" % "test"
