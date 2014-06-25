import sbtrelease._
import ReleaseStateTransformations._
import ReleasePlugin._
import ReleaseKeys._
import Utilities._
import com.typesafe.sbt.SbtPgp.PgpKeys._

Seq[Setting[_]](shellPrompt <<= (thisProjectRef, version) { (id, v) => _ => "%s:%s> ".format(id.project, v)})

sbtPlugin := true

name := "sbt-x-sjs-plugin"

organization := "biz.cgta"

scalacOptions ++= Seq("-deprecation", "-unchecked", "-feature")

releaseSettings

releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runClean,
  runTest,
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  publishArtifacts.copy(action = publishSignedAction),
  setNextVersion,
  commitNextVersion,
  pushChanges
)

lazy val publishSignedAction = { st: State =>
  val extracted = st.extract
  val ref = extracted.get(thisProjectRef)
  extracted.runAggregated(publishSigned in Global in ref, st)
}

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}

publishMavenStyle := true

publishArtifact in Test := false

pomIncludeRepository := { _ => false }

pomExtra := (
  <url>https://github.com/cgta/sbt-sjs-and-jvm-projects-plugin</url>
    <licenses>
      <license>
        <name>MIT license</name>
        <url>http://www.opensource.org/licenses/mit-license.php</url>
      </license>
    </licenses>
    <scm>
      <url>git://github.com/cgta/sbt-sjs-and-jvm-projects-plugin.git</url>
      <connection>scm:git://github.com/cgta/sbt-sjs-and-jvm-projects-plugin.git</connection>
    </scm>
    <developers>
      <developer>
        <id>benjaminjackman</id>
        <name>Benjamin Jackman</name>
        <url>https://github.com/benjaminjackman</url>
      </developer>
    </developers>
  )


//~/.sbt/0.13/sonatype.sbt file with the following:
//credentials += Credentials("Sonatype Nexus Repository Manager",
//                           "oss.sonatype.org",
//                           "<your username>",
//                           "<your password>")