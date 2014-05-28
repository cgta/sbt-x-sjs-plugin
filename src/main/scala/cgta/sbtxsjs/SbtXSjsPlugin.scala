package cgta.sbtxsjs

import sbt._
import sbt.Keys._
import scala.scalajs.sbtplugin.ScalaJSPlugin

//////////////////////////////////////////////////////////////
// Copyright (c) 2014 Ben Jackman, Jeff Gomberg
// All Rights Reserved
// please contact ben@jackman.biz or jeff@cgtanalytics.com
// for licensing inquiries
// Created by bjackman @ 5/28/14 3:10 PM
//////////////////////////////////////////////////////////////

object SbtXSjsPlugin extends Plugin {
  def xprojects(name: String) : XSjsProjects = {

    val sharedSourceSettings = Seq(
      unmanagedSourceDirectories in Compile += baseDirectory(_ / ".." / name / "src" / "main" / "scala").value,
      unmanagedSourceDirectories in Test += baseDirectory(_ / ".." / name / "src" / "test" / "scala").value)

    def p(name: String) = Project(name, file(name))

    val shared: Project = p(name)
    val jvm: Project = p(name + "-jvm").settings(sharedSourceSettings: _*)
    val sjs: Project = p(name + "-sjs").settings(sharedSourceSettings ++ ScalaJSPlugin.scalaJSSettings: _*)

    new XSjsProjects(name, shared = shared, jvm = jvm, sjs = sjs)

  }


  class XSjsProjects private[SbtXSjsPlugin](
    val name: String,
    val shared: Project,
    val jvm: Project,
    val sjs: Project) {

    def copy(
      shared: Project = this.shared,
      jvm: Project = this.jvm,
      sjs: Project = this.sjs
    ): XSjsProjects = {
      new XSjsProjects(name = name, shared = shared, jvm = jvm, sjs = sjs)
    }

    def dependsOn(deps: XSjsProjects*): XSjsProjects = {
      copy(
        shared = shared.dependsOn(deps.map(x => x.shared: sbt.ClasspathDep[sbt.ProjectReference]): _*),
        jvm = jvm.dependsOn(deps.map(x => x.jvm: sbt.ClasspathDep[sbt.ProjectReference]): _*),
        sjs = sjs.dependsOn(deps.map(x => x.sjs: sbt.ClasspathDep[sbt.ProjectReference]): _*)
      )
    }

    def settingsShared(ss: Def.Setting[_]*): XSjsProjects = {
      copy(
        shared = shared.settings(ss: _*),
        jvm = jvm.settings(ss: _*),
        sjs = sjs.settings(ss: _*)
      )
    }

    def settingsJvm(ss: Def.Setting[_]*): XSjsProjects = {
      copy(jvm = jvm.settings(ss: _*))
    }

    def settingsSjs(ss: Def.Setting[_]*): XSjsProjects = {
      copy(sjs = sjs.settings(ss: _*))
    }
  }
}