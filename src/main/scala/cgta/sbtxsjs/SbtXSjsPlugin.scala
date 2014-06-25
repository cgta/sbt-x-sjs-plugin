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

  def sourceSettings(suffix: String): Seq[Setting[_]] = Seq(
    resourceDirectories in Compile := Seq(
      baseDirectory.value / ".." / "resources"),
    unmanagedSourceDirectories in Compile := Seq(
      baseDirectory.value,
      baseDirectory.value / ".." / "scala"),
    resourceDirectories in Test := Seq(
      baseDirectory.value / ".." / ".." / "test" / "resources"),
    unmanagedSourceDirectories in Test := Seq(
      baseDirectory.value / ".." / ".." / "test" / ("scala-" + suffix),
      baseDirectory.value / ".." / ".." / "test" / "scala"),
    target := baseDirectory.value / ".." / ".." / ".." / "target" / suffix)

  def xSjsProjects(id: String, baseFile: File): XSjsProjects = {

    def sub(suffix: String): Project =
      Project(s"$id-$suffix", baseFile / "src" / "main" / "scala-sjs").settings(sourceSettings(suffix): _*)

    val base: Project = Project(id, baseFile).aggregate(LocalProject(s"$id-jvm"), LocalProject(s"$id-sjs"))
    val jvm: Project = sub("jvm")
    val sjs: Project = sub("sjs")

    new XSjsProjects(id, base = base, jvm = jvm, sjs = sjs)
  }

  class XSjsProjects private[SbtXSjsPlugin](
    val id: String,
    val base: Project,
    val jvm: Project,
    val sjs: Project) {

    def copy(
      base: Project = this.base,
      jvm: Project = this.jvm,
      sjs: Project = this.sjs
    ): XSjsProjects = {
      new XSjsProjects(id = id, base = base, jvm = jvm, sjs = sjs)
    }

    def dependsOn(deps: XSjsProjects*): XSjsProjects = copy(
      base = base.dependsOn(deps.map(x => x.base: sbt.ClasspathDep[sbt.ProjectReference]): _*),
      jvm = jvm.dependsOn(deps.map(x => x.jvm: sbt.ClasspathDep[sbt.ProjectReference]): _*),
      sjs = sjs.dependsOn(deps.map(x => x.sjs: sbt.ClasspathDep[sbt.ProjectReference]): _*))

    //Added to all projects, base / sjs / jvm
    def settingsAll(ss: Def.Setting[_]*): XSjsProjects = copy(
      base = base.settings(ss: _*),
      jvm = jvm.settings(ss: _*),
      sjs = sjs.settings(ss: _*))

    //Added only to base project
    def settingsBase(ss: Def.Setting[_]*): XSjsProjects = copy(base = base.settings(ss: _*))

    //Added only to jvm project
    def settingsJvm(ss: Def.Setting[_]*): XSjsProjects = copy(jvm = jvm.settings(ss: _*))

    //Added only to sjs project
    def settingsSjs(ss: Def.Setting[_]*): XSjsProjects = copy(sjs = sjs.settings(ss: _*))
  }
}