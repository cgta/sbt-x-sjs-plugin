# sbt-x-sjs-plugin 0.1.3


Really simple scaffolding for compiling projects that target both ScalaJs and ScalaJvm.


This README is longer than the [source](https://github.com/cgta/sbt-x-sjs-plugin/blob/v0.1.3/src/main/scala/cgta/sbtxsjs/SbtXSjsPlugin.scala), So it's probably just easier to read that, but for
those that prefer a bit more english in the programming comprehension, here goes nothing:


This plugin will create a container, that holds 3 separate projects. It doesn't include 
any other libraries or plugins. Note that it does not even include ScalaJSPlugin / Settings.
This prevents having to update this plugin when ScalaJs updates.


The `base` project
------------------
The `base` project should have all the sources that are shared between ScalaJs and ScalaJvm. The majority of the source should end up in this project.  All the code will be put under the normal `src/main/scala` and `src/test/scala` folders. All the code from under these folders will be included in each of the jvm and sjs builds.

The `jvm` project
-----------------
The `jvm` project should contain all the sources that are specific to the jvm implementation. All the ScalaJs code will be put under `src/main/scala-jvm` and `src/test/scala-jvm`

The `sjs` project
-----------------
The `sjs` project should contain all the sources that are specific to the sjs implementation. All the ScalaJs code will be put under `src/main/scala-sjs` and `src/test/scala-sjs`


Usage
=====

Include the plugin
------------------
Create `project/sbtXSjsPlugin.sbt` containing:
```scala
addSbtPlugin("biz.cgta" % "sbt-x-sjs-plugin" % "0.1.3")
```

Use the plugin
--------------
In `project/Build.scala` 

```scala
import cgta.sbtxsjs.SbtXSjsPlugin


//This isn't a project, just a container that holds 3 projects
lazy val projectaX = SbtXSjsPlugin.xSjsProjects("projecta", file("projecta"))
    //Applies to all Projects
    .settingsAll(organization := "biz.cgta")
    //Applies only to the ScalaJs Project
    .settingsSjs(ScalaJSPlugin.scalaJSSettings: _*)
    //Applies only to the Jvm Project
    .settingsJvm(foo := bar)
    //Applies only to the Base Project
    .settingsBase(foo : = bip)
    
lazy val projecta = projectaX.base
lazy val projectaJvm = projectaX.jvm
lazy val projectaSjs = projectaX.sjs

//Use depends on to make xSjsProjects depend on each other
lazy val projectbX = SbtXSjsPlugin.xSjsProjects("projectb", file("projectb"))
    .dependsOn(projectaX)
    
lazy val projectb = projectbX.base
lazy val projectbJvm = projectbX.jvm
lazy val projectbSjs = projectbX.sjs
```


Compile
-------
The base project, whatever you happen to name it, acts as an aggregate of the other other two projects so tasks can be run from there. Or the code can be run in each of the two projects individually.

Gotchas
-------
The SbtXSjsProjects container is immutable! This in keeping in the spirit of sbt, where projects are also immutable.

Sbt's project discovery is to find all the lazy vals of : Project in your Build and then treate them as projects, so
make sure that you assign the 3 seperate projects to lazy vals.


See it in action
----------------
The otest framework uses the plugin.
You can see the 3 directories [here](https://github.com/cgta/otest/tree/master/otest/src/main)

In the base package [this file](https://github.com/cgta/otest/blob/v0.1.9/otest/src/main/scala/cgta/otest/FunSuite.scala#L17) defines a FunSuite, if that suite is targeting ScalaJS it needs to have an annotation `@JSExportDescendentObjects` provided to aid with test discovery.

In base:
```scala
class FunSuite extends FunSuitePlatformImpl { ...
```

To accomplish this `FunSuitePlatformImpl` is given different defintions in the sjs and jvm projects.
In jvm: [File](https://github.com/cgta/otest/blob/v0.1.9/otest/src/main/scala-jvm/cgta/otest/FunSuitePlatformImpl.scala#L13)
```scala
trait FunSuitePlatformImpl
```

In sjs: [File](https://github.com/cgta/otest/blob/v0.1.9/otest/src/main/scala-sjs/cgta/otest/FunSuitePlatformImpl.scala)
```
@JSExportDescendentObjects
trait FunSuitePlatformImpl
```







