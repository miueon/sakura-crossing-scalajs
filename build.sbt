import org.scalajs.linker.interface.ModuleKind
import org.scalajs.linker.interface.ModuleSplitStyle
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport.*

ThisBuild / scalaVersion := "3.8.1"

ThisBuild / scalacOptions ++= Seq(
  "-Wunused:all",
  "-Wconf:src=src/main/scala-generated/.*:silent",
  "-experimental"
)

resolvers +=
  "Sonatype S01 OSS Snapshots".at(
    "https://s01.oss.sonatype.org/content/repositories/snapshots"
  )

lazy val `sakura-crossing` = project
  .in(file("."))
  .enablePlugins(ScalaJSPlugin)
  .settings(
    name                            := "sakura-crossing-scalajs",
    scalaJSUseMainModuleInitializer := true,
    scalaJSLinkerConfig ~= {
      _.withModuleKind(ModuleKind.ESModule)
        .withModuleSplitStyle(ModuleSplitStyle.SmallModulesFor(List("sakura")))
    },
    Compile / unmanagedSourceDirectories += baseDirectory.value / "src" / "main" / "scala-generated",
    Compile / fastLinkJS / scalaJSLinkerOutputDirectory := baseDirectory.value / "build" / "dev",
    Compile / fullLinkJS / scalaJSLinkerOutputDirectory := baseDirectory.value / "build" / "prod",
    Test / fastLinkJS / scalaJSLinkerOutputDirectory    := baseDirectory.value / "build" / "test-dev",
    libraryDependencies ++= Seq(
      "com.raquo"          % "laminar_sjs1_3"     % "17.2.1",
      "org.scala-js"       % "scalajs-dom_sjs1_3" % "2.8.0",
      "io.github.iltotore" % "iron_sjs1_3"        % "3.3.1",
      "io.indigoengine"    % "ultraviolet_sjs1_3" % "0.30.0-M6",
      "org.scalameta"      % "munit_sjs1_3"       % "1.0.2" % Test
    ),
    Compile / fullOptJS / scalaJSLinkerConfig ~= { _.withCheckIR(false) },
    Test / fullOptJS / scalaJSLinkerConfig ~= { _.withCheckIR(false) }
  )
