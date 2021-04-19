import sbtcrossproject.CrossPlugin.autoImport.{ crossProject, CrossType }

name := "Context-Aware-Resize"

version := "1.0"

scalaVersion := "2.13.4"

lazy val resize = 
  crossProject(JVMPlatform, NativePlatform)
  .in(file("."))
  .settings(Seq(
    scalaVersion := "2.13.4",
    libraryDependencies ++= List(
      "eu.joaocosta"      %%% "minart-core"    % "0.2.2",
      "eu.joaocosta"      %%% "minart-pure"    % "0.2.2"
    ),
    scalafmtOnCompile := true
  ))
  .nativeSettings(Seq(
    nativeLinkStubs := true,
    nativeMode := "release",
    nativeLTO := "thin",
    nativeGC := "immix"
  ))
  .settings(name := "resize")
