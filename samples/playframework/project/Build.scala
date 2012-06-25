import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

  val appName = "play_sample"
  val appVersion = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    "com.typesafe" % "play-plugins-guice" % "2.0.2",
    "org.lushlife.guicexml" % "guice-xml" % "1.40",
    "org.slf4j" % "slf4j-api" % "1.6.6",
    "net.debasishg" % "redisclient_2.9.1" % "2.5",
    "com.novus" %% "salat-core" % "0.0.8-SNAPSHOT")

  val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA).settings(
    resolvers ++= Seq(
      "Scala repository" at "https://oss.sonatype.org/content/groups/scala-tools/",
      "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/",
      "My Git repository" at "https://github.com/takeshi/maven-repo/raw/master/release/",
      "My Svn repository" at "http://lushlife.googlecode.com/svn/maven2/",
      "Maven repository" at "http://morphia.googlecode.com/svn/mavenrepo/",
      "MongoDb Java Driver Repository" at "http://repo1.maven.org/maven2/org/mongodb/mongo-java-driver/",
      "repo.novus rels" at "http://repo.novus.com/releases/",
      "repo.novus snaps" at "http://repo.novus.com/snapshots/"))

}
