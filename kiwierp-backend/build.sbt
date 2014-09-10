name := """kiwierp-backend"""

version := "0.1-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.2"

libraryDependencies ++= Seq(
  "org.scalikejdbc" %% "scalikejdbc" % "2.1.1",
  "org.scalikejdbc" %% "scalikejdbc-interpolation" % "2.1.1",
  "org.scalikejdbc" %% "scalikejdbc-async" % "0.5.1",
  "org.scalikejdbc" %% "scalikejdbc-async-play-plugin" % "0.5.1",
  "com.github.mauricio" %% "postgresql-async" % "0.2.14",
  "com.lambdaworks" % "scrypt" % "1.4.0"
)
