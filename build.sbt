ThisBuild / scalaVersion     := "3.2.0"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "guygo.chat"
ThisBuild / organizationName := "example"

lazy val zioVersion = "2.0.3"

lazy val root = (project in file("."))
  .settings(
    name := "zio-2-chat",
    libraryDependencies ++= Seq(
      "dev.zio" %% "zio" % zioVersion,
      "dev.zio" %% "zio-macros" % zioVersion,


      // --- Test ---
      "dev.zio" %% "zio-test" % zioVersion % "test",
      "dev.zio" %% "zio-test-sbt" % zioVersion % "test",
//      "org.specs2" %% "specs2-core" % "5.0.0" % "test",
    ),
    testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")
  )
