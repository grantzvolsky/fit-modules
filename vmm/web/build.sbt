name := """peptidentifier-web"""

version := "1.0-SNAPSHOT"

lazy val web = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

// local
resolvers += (
  "Local ivy2 Repository" at "file:///"+Path.userHome.absolutePath+"/.ivy2/local"
)

libraryDependencies ++= Seq(
  "org.zvolsky" % "peptidentifier_2.11" % "0.1-SNAPSHOT"
)

libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws,
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test
)

