package cgta.sbtxsjs

import sbt._
import sbt.Keys._


//////////////////////////////////////////////////////////////
// Copyright (c) 2014 Ben Jackman, Jeff Gomberg
// All Rights Reserved
// please contact ben@jackman.biz or jeff@cgtanalytics.com
// for licensing inquiries
// Created by bjackman @ 5/28/14 3:10 PM
//////////////////////////////////////////////////////////////

object SbtXSjsPlugin extends Plugin {

  object XSjsProjects {

    def nopSettings: Seq[Setting[_]] = Seq(
      publish := {},
      test := {},
      testQuick := {},
      testOnly := {},
      compile := {sbt.inc.Analysis.Empty}
    )

    def sourceSettings(suffix: String): Seq[Setting[_]] = Seq(
      unmanagedSourceDirectories in Compile := Seq(
        baseDirectory.value,
        baseDirectory.value / ".." / "scala"),
      unmanagedSourceDirectories in Test := Seq(
        baseDirectory.value / ".." / ".." / "test" / ("scala-" + suffix),
        baseDirectory.value / ".." / ".." / "test" / "scala"),
      unmanagedResourceDirectories in Compile := Seq(
        baseDirectory.value / ".." / "resources"),
      unmanagedResourceDirectories in Test := Seq(
        baseDirectory.value / ".." / ".." / "test" / "resources"
      ),
      target := baseDirectory.value / ".." / ".." / ".." / "target" / suffix)

    def testSourceSettings(suffix: String): Seq[Setting[_]] = Seq(
      resourceDirectories in Compile := Nil,
      unmanagedSourceDirectories in Compile := Nil,
      resourceDirectories in Test := Seq(baseDirectory.value / ".." / "resources"),
      unmanagedSourceDirectories in Test := Seq(baseDirectory.value),
      target := baseDirectory.value / ".." / ".." / ".." / "target" / (suffix + "-test")) ++
      nopSettings

    def base(id: String, baseFile: File): Project =
      Project(id, baseFile)
        .aggregate(LocalProject(s"$id-jvm"), LocalProject(s"$id-sjs"))
        .settings(nopSettings: _*)

    def sub(id: String, suffix: String, baseFile: File): Project =
      Project(s"$id-$suffix", baseFile / "src" / "main" / s"scala-$suffix")
        .settings(sourceSettings(suffix): _*)

    //This is made as an intellij workaround
    def subTest(id: String, suffix: String, baseFile: File): Project =
      Project(s"$id-$suffix-test", baseFile / "src" / "test" / s"scala-$suffix")
        .settings(testSourceSettings(suffix): _*)

    def apply(id: String, baseFile: File): XSjsProjects = {
      val baseP: Project = base(id, baseFile)
      val jvm: Project = sub(id, "jvm", baseFile)
      val sjs: Project = sub(id, "sjs", baseFile)
      val jvmTest: Project = subTest(id, "jvm", baseFile).dependsOn(LocalProject(s"$id-jvm"))
      val sjsTest: Project = subTest(id, "sjs", baseFile).dependsOn(LocalProject(s"$id-sjs"))

      new XSjsProjects(id, base = baseP, jvm = jvm, sjs = sjs, jvmTest = jvmTest, sjsTest = sjsTest)
    }
  }

  class XSjsProjects(
    val id: String,
    val base: Project,
    val jvm: Project,
    val sjs: Project,
    val jvmTest: Project,
    val sjsTest: Project) {

    def copy(
      base: Project = this.base,
      jvm: Project = this.jvm,
      sjs: Project = this.sjs,
      jvmTest: Project = this.jvmTest,
      sjsTest: Project = this.sjsTest): XSjsProjects = {
      new XSjsProjects(id = id, base = base, jvm = jvm, sjs = sjs, jvmTest = jvmTest, sjsTest = sjsTest)
    }

    def mapSelf(f: XSjsProjects => XSjsProjects): XSjsProjects = f(this)

    def dependsOn(deps: XSjsProjects*): XSjsProjects = copy(
      base = base.dependsOn(deps.map(x => x.base: sbt.ClasspathDep[sbt.ProjectReference]): _*),
      jvm = jvm.dependsOn(deps.map(x => x.jvm: sbt.ClasspathDep[sbt.ProjectReference]): _*),
      sjs = sjs.dependsOn(deps.map(x => x.sjs: sbt.ClasspathDep[sbt.ProjectReference]): _*))

    //Added to all projects, base / sjs / jvm
    def settingsAll(ss: Def.Setting[_]*): XSjsProjects = copy(
      base = base.settings(ss: _*),
      jvm = jvm.settings(ss: _*),
      sjs = sjs.settings(ss: _*),
      jvmTest = jvmTest.settings(ss: _*),
      sjsTest = sjsTest.settings(ss: _*))

    //Added only to base project
    def settingsBase(ss: Def.Setting[_]*): XSjsProjects = copy(base = base.settings(ss: _*))
    def mapBase(f: Project => Project): XSjsProjects = copy(base = f(base))

    //Added only to jvm project
    def settingsJvm(ss: Def.Setting[_]*): XSjsProjects = copy(jvm = jvm.settings(ss: _*))
    def mapJvm(f: Project => Project): XSjsProjects = copy(jvm = f(jvm))

    //Added only to sjs project
    def settingsSjs(ss: Def.Setting[_]*): XSjsProjects = copy(sjs = sjs.settings(ss: _*))
    def mapSjs(f: Project => Project): XSjsProjects = copy(sjs = f(sjs))

    //Added only to jvm test project
    def settingsJvmTest(ss: Def.Setting[_]*): XSjsProjects = copy(jvmTest = jvmTest.settings(ss: _*))
    def mapJvmTest(f: Project => Project): XSjsProjects = copy(jvmTest = f(jvmTest))

    //Added only to sjs test project
    def settingsSjsTest(ss: Def.Setting[_]*): XSjsProjects = copy(sjsTest = sjsTest.settings(ss: _*))
    def mapSjsTest(f: Project => Project): XSjsProjects = copy(sjsTest = f(sjsTest))

    //Added only to jvm projects
    def settingsJvmAll(ss: Def.Setting[_]*): XSjsProjects = copy(jvm = jvm.settings(ss: _*), jvmTest = jvmTest.settings(ss: _*))
    def mapJvmAll(f: Project => Project): XSjsProjects = copy(jvm = f(jvm), jvmTest = f(jvmTest))

    //Added only to sjs projects
    def settingsSjsAll(ss: Def.Setting[_]*): XSjsProjects = copy(sjs = sjs.settings(ss: _*), sjsTest = sjsTest.settings(ss: _*))
    def mapSjsAll(f: Project => Project): XSjsProjects = copy(sjs = f(sjs), sjsTest = f(sjsTest))

    //Added only to the jvm and sjs projects
    def settingsSubs(ss: Def.Setting[_]*): XSjsProjects =
      settingsJvm(ss: _*).settingsSjs(ss: _*)

    //Added only to the jvm and sjs test projects
    def settingsSubsTest(ss: Def.Setting[_]*): XSjsProjects =
      settingsJvmTest(ss: _*).settingsSjsTest(ss: _*)

    //Added only to the jvm and sjs main + test projects
    def settingsSubsAll(ss: Def.Setting[_]*): XSjsProjects =
      settingsJvm(ss: _*).settingsSjs(ss: _*).settingsJvmTest(ss: _*).settingsSjsTest(ss: _*)


    def tupled = (this, base, jvm, sjs)

    def tupledWithTests = (this, base, jvm, sjs, jvmTest, sjsTest)
  }
}