name := "play2memcached"

version := "0.0-SNAPSHOT"

organization := "eu.inn"

resolvers ++= Seq("Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/")

libraryDependencies += "org.scalatest" % "scalatest_2.10" % "2.0.RC1" % "test"

libraryDependencies += "org.mockito" % "mockito-all" % "1.9.5" % "test"

libraryDependencies += "com.dongxiguo" % "memcontinuationed_2.10" % "0.3.1"

libraryDependencies <+= scalaVersion { v =>
  compilerPlugin("org.scala-lang.plugins" % "continuations" % v)
}

libraryDependencies += "com.typesafe.play" %% "play" % "2.2.2-RC1"

libraryDependencies += "com.typesafe.play" %% "play-test" % "2.2.2-RC1" % "test"

scalacOptions += "-P:continuations:enable"
