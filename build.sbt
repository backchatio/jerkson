import scala.xml.Group

scalaVersion := "2.9.2"

name := "jerkson"

version := "0.7.0"

organization := "io.backchat.jerkson"

javacOptions ++= Seq("-Xlint:unchecked", "-source", "1.6", "-target", "1.6", "-Xlint:deprecation")

scalacOptions ++= Seq("-optimise", "-unchecked", "-deprecation", "-Xcheckinit", "-encoding", "utf8")

autoCompilerPlugins := true

libraryDependencies ++= Seq(
  "com.fasterxml.jackson.core" % "jackson-core" % "2.0.5",
  "com.fasterxml.jackson.core" % "jackson-databind" % "2.0.5",
  compilerPlugin("com.nativelibs4java" % "scalacl-compiler-plugin" % "0.2"),
  "com.codahale" % "simplespec_2.9.1" % "0.5.2" % "test"
)

resolvers += "NativeLibs4Java Repository" at "http://nativelibs4java.sourceforge.net/maven/"

resolvers += "Coda Hale Repository" at "http://repo.codahale.com"

homepage := Some(url("https://github.com/backchatio/jerkson"))

startYear := Some(2010)

licenses := Seq(("MIT", url("http://codahale.com/mit.txt")))

pomExtra <<= (pomExtra, name, description) {(pom, name, desc) => pom ++ Group(
  <scm>
    <connection>scm:git:git://github.com/backchatio/jerkson.git</connection>
    <developerConnection>scm:git:git@github.com:backchatio/jerkson.git</developerConnection>
    <url>https://github.com/backchatio/jerkson</url>
  </scm>
  <developers>
    <developer>
        <name>Coda Hale</name>
        <email>coda.hale@gmail.com</email>
        <timezone>-8</timezone>
    </developer>
    <developer>
      <id>casualjim</id>
      <name>Ivan Porto Carrero</name>
      <url>http://flanders.co.nz/</url>
    </developer>
    <developer>
      <id>sdb</id>
      <name>Stefan De Boey</name>
      <url>http://ellefant.be/</url>
    </developer>
  </developers>  
)}

packageOptions <+= (name, version, organization) map {
    (title, version, vendor) =>
      Package.ManifestAttributes(
        "Created-By" -> "Simple Build Tool",
        "Built-By" -> System.getProperty("user.name"),
        "Build-Jdk" -> System.getProperty("java.version"),
        "Specification-Title" -> title,
        "Specification-Version" -> version,
        "Specification-Vendor" -> vendor,
        "Implementation-Title" -> title,
        "Implementation-Version" -> version,
        "Implementation-Vendor-Id" -> vendor,
        "Implementation-Vendor" -> vendor
      )
  }

publishMavenStyle := true

publishTo <<= version { (v: String) =>
  val nexus = "https://oss.sonatype.org/"
  if (v.trim.endsWith("SNAPSHOT"))
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}

publishArtifact in Test := false

pomIncludeRepository := { x => false }

// seq(scalariformSettings: _*)

// ScalariformKeys.preferences :=
//   (FormattingPreferences()
//         setPreference(IndentSpaces, 2)
//         setPreference(AlignParameters, false)
//         setPreference(AlignSingleLineCaseStatements, true)
//         setPreference(DoubleIndentClassDeclaration, true)
//         setPreference(RewriteArrowSymbols, true)
//         setPreference(PreserveSpaceBeforeArguments, true)
//         setPreference(IndentWithTabs, false))

// (excludeFilter in ScalariformKeys.format) <<= excludeFilter(_ || "*Spec.scala")

// testOptions in Test += Tests.Setup( () => System.setProperty("akka.mode", "test") )

// testOptions in Test += Tests.Argument(TestFrameworks.Specs2, "console", "junitxml")

// testOptions in Test <+= (crossTarget map { ct =>
//  Tests.Setup { () => System.setProperty("specs2.junit.outDir", new File(ct, "specs-reports").getAbsolutePath) }
// })
