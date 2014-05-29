import bintray.Keys._

sbtPlugin := true

name := "sbt-x-sjs-plugin"

organization := "biz.cgta"

publishMavenStyle := false

scalacOptions ++= Seq("-deprecation", "-unchecked", "-feature")

bintrayPublishSettings

releaseSettings

repository in bintray := "sbt-plugins"

// This is an example.  bintray-sbt requires licenses to be specified
// (using a canonical name).
licenses += ("MIT", url("http://opensource.org/licenses/MIT"))

bintrayOrganization in bintray := Some("cgta")