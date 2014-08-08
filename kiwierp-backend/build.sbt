name := """kiwierp"""

version := "0.1-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.10.4"

libraryDependencies ++= Seq(
  "org.scalikejdbc" %% "scalikejdbc" % "2.0.2",
  "org.scalikejdbc" %% "scalikejdbc-interpolation" % "2.0.2",
  "org.scalikejdbc" %% "scalikejdbc-async" % "0.4.1",
  "org.scalikejdbc" %% "scalikejdbc-async-play-plugin" % "0.4.1",
  "com.github.mauricio" %% "postgresql-async" % "0.2.13",
  "com.lambdaworks" % "scrypt" % "1.4.0"
)
