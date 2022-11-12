addSbtPlugin("com.thesamet" % "sbt-protoc" % "1.0.2")

val zioGrpcVersion = "0.6.0-test4"

libraryDependencies ++= Seq(
  "com.thesamet.scalapb.zio-grpc" %% "zio-grpc-codegen" % zioGrpcVersion,
  "com.thesamet.scalapb" %% "compilerplugin" % "0.11.12"
)