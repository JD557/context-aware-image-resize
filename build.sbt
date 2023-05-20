import sbtcrossproject.CrossPlugin.autoImport.{crossProject, CrossType}

name := "Context-Aware-Resize"

version := "1.2"

scalaVersion := "3.2.2"

lazy val resize =
  crossProject(JVMPlatform, NativePlatform)
    .in(file("."))
    .settings(
      Seq(
        scalaVersion := "3.2.2",
        libraryDependencies ++= List(
          "eu.joaocosta" %%% "minart" % "0.5.2"
        ),
        scalafmtOnCompile := true
      )
    )
    .nativeSettings(
      Seq(
        nativeLinkStubs := true,
        nativeMode := "release",
        nativeLTO := "thin",
        nativeGC := "immix"
      )
    )
    .settings(name := "resize")
