import sbtcrossproject.CrossPlugin.autoImport.{ crossProject, CrossType }

name := "Context-Aware-Resize"

version := "1.0"

scalaVersion := "2.13.4"

lazy val resize = 
  crossProject(JVMPlatform, JSPlatform, NativePlatform)
  .in(file("."))
  .settings(Seq(
    scalaVersion := "2.13.4",
    libraryDependencies ++= List(
      "eu.joaocosta"      %%% "minart-core"    % "0.2.2",
      "eu.joaocosta"      %%% "minart-pure"    % "0.2.2",
      "eu.joaocosta"      %%% "minart-extra"   % "0.2.2-SNAPSHOT"
    ),
    scalafmtOnCompile := true
  ))
  .jsSettings(Seq(
    scalaJSUseMainModuleInitializer := true
  ))
  .nativeSettings(Seq(
    nativeLinkStubs := true,
    nativeMode := "release",
    nativeLTO := "thin",
    nativeGC := "immix"
  ))
  .settings(name := "resize")
