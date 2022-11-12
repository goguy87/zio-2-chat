ThisBuild / scalaVersion     := "3.2.0"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "guygo.chat"
ThisBuild / organizationName := "example"

lazy val zioVersion = "2.0.3"

Compile / PB.targets := Seq(
  scalapb.gen(grpc = true) -> (Compile / sourceManaged).value,
  scalapb.zio_grpc.ZioCodeGenerator -> (Compile / sourceManaged).value
)

lazy val root = (project in file("."))
  .settings(
    name := "zio-2-chat",
    libraryDependencies ++= Seq(
      "dev.zio" %% "zio" % zioVersion,
      "dev.zio" %% "zio-macros" % zioVersion,
      "io.grpc" % "grpc-netty" % "1.50.2",
      "com.thesamet.scalapb" %% "scalapb-runtime-grpc" % scalapb.compiler.Version.scalapbVersion,
//      "com.thesamet.scalapb" %% "scalapb-runtime" % scalapb.compiler.Version.scalapbVersion % "protobuf",


      // --- Test ---
      "dev.zio" %% "zio-test" % zioVersion % "test",
      "dev.zio" %% "zio-test-sbt" % zioVersion % "test",
//      "org.specs2" %% "specs2-core" % "5.0.0" % "test",
    ),
    testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")
  )
