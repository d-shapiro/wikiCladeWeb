name := "wikiCladeWeb"
 
version := "1.1.4"
      
lazy val `wikicladeweb` = (project in file(".")).enablePlugins(PlayScala)

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"
      
resolvers += "Akka Snapshot Repository" at "http://repo.akka.io/snapshots/"

resolvers += "jitpack" at "https://jitpack.io"
      
scalaVersion := "2.12.8"

libraryDependencies ++= Seq( jdbc , ehcache , ws , specs2 % Test , guice )

libraryDependencies += "com.github.d-shapiro" % "wikiClade" % "1.1.4"

unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )  
