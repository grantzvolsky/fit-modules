import sbt.Project.projectToRef

lazy val scalaV = "2.11.8"

lazy val web = (project in file("web")).settings(
  scalaJSProjects := Seq(client),
  pipelineStages in Assets := Seq(scalaJSPipeline),
  // triggers scalaJSPipeline when using compile or continuous compilation
  compile in Compile <<= (compile in Compile) dependsOn scalaJSPipeline,

  name := """peptidentifier-web""",
  version := "1.0-SNAPSHOT",
  scalaVersion := scalaV,
  // local
  resolvers += (
    "Local ivy2 Repository" at "file:///"+Path.userHome.absolutePath+"/.ivy2/local"
  ),
  libraryDependencies ++= Seq(
    "org.zvolsky" % "peptidentifier_2.11" % "0.1-SNAPSHOT",
    "com.vmunier" %% "scalajs-scripts" % "1.0.0",
    jdbc,
    cache,
    ws,
    "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test,
    "com.lihaoyi" %%% "upickle" % "0.4.3"
  )
).enablePlugins(PlayScala)
 .dependsOn(sharedJvm)

lazy val client = (project in file("web-client")).settings(
  scalaVersion := scalaV,
  name := "ScalaJSClient",
  version := "0.1-SNAPSHOT",
  addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full),
  libraryDependencies ++= Seq(
    "org.scala-js" %%% "scalajs-dom" % "0.9.1",
    "com.lihaoyi" %%% "upickle" % "0.4.3",
    "com.lihaoyi" %%% "scalatags" % "0.6.1",
    "org.scala-lang.modules" % "scala-xml_2.11" % "1.0.5"
  ),
  persistLauncher := false,
  persistLauncher in Test := false,
  persistLauncher in Compile := true
).enablePlugins(ScalaJSPlugin, ScalaJSWeb)
 .dependsOn(sharedJs)


lazy val shared = (crossProject.crossType(CrossType.Pure) in file("shared")).
  settings(scalaVersion := scalaV).
  jsConfigure(_ enablePlugins ScalaJSWeb)

lazy val sharedJvm = shared.jvm
lazy val sharedJs = shared.js

onLoad in Global := (Command.process("project web", _: State)) compose (onLoad in Global).value

fork in run := true