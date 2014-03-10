name := "play2memcached"

version := "0.1.1"

organization := "eu.inn"

resolvers ++= Seq("Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/")

libraryDependencies += "net.spy" % "spymemcached" % "2.10.3"

libraryDependencies += "com.typesafe.play" %% "play" % "2.2.2"

libraryDependencies += "com.typesafe.play" %% "play-cache" % "2.2.2"


// Sonatype repositary publish options
publishMavenStyle := true

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}

publishArtifact in Test := false

pomIncludeRepository := { _ => false }

pomExtra := (
  <url>https://github.com/InnovaCo/play2memcached</url>
  <licenses>
    <license>
      <name>BSD-style</name>
      <url>http://opensource.org/licenses/BSD-3-Clause</url>
      <distribution>repo</distribution>
    </license>
  </licenses>
  <scm>
    <url>git@github.com:InnovaCo/play2memcached.git</url>
    <connection>scm:git:git@github.com:InnovaCo/play2memcached.git</connection>
  </scm>
  <developers>
  	<developer>
      <id>InnovaCo</id>
      <name>Innova Co S.a r.l</name>
      <url>https://github.com/InnovaCo</url>
    </developer>
    <developer>
      <id>maqdev</id>
      <name>Maga Abdurakhmanov</name>
      <url>https://github.com/maqdev</url>
    </developer>
  </developers>
)