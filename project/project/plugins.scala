
import sbt._
import Keys._

object PluginsBuild extends Build {

  val root = (Project("plugins", file("."), settings = Defaults.defaultSettings)
               dependsOn(uri("git://github.com/casualjim/junit-interface")))
}
