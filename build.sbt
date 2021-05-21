ThisBuild / organization := "com.example"
ThisBuild / scalaVersion := "3.0.0"

lazy val root = (project in file("."))
  .settings(
    name := "scala3-live",
    scalacOptions ++= Seq("-source", "future"),
    scalacOptions -= "-Xfatal-warnings",
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-effect" % "3.1.1"
    ),
  )
  .enablePlugins(JavaAppPackaging)
