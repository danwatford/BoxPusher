name := """box"""

version := "1.0-SNAPSHOT"

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  "org.apache.commons" % "commons-lang3" % "3.1",
  "org.jmockit" % "jmockit" % "1.20" % "test",
  "junit" % "junit" % "4.12" % "test",
  "org.hamcrest" % "hamcrest-library" % "1.3" % "test",
  "com.google.guava" % "guava" % "18.0" % "test",
  "org.scalatest" %% "scalatest" % "2.2.4" % "test",
  "org.scalacheck" %% "scalacheck" % "1.12.5" % "test"
)

initialCommands in Test in console := "import org.scalacheck.Prop._; import org.scalacheck._"

fork in run := true

testOptions in Test += Tests.Argument(TestFrameworks.ScalaCheck, "-minSuccessfulTests", "500", "-workers", "4")
