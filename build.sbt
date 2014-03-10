name := "play2memcached"

version := "0.1"

organization := "eu.inn"

resolvers ++= Seq("Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/")

libraryDependencies += "net.spy" % "spymemcached" % "2.10.3"

libraryDependencies += "com.typesafe.play" %% "play" % "2.2.2"

libraryDependencies += "com.typesafe.play" %% "play-cache" % "2.2.2"
