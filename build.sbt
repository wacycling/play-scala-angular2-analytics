import sbt.Keys.libraryDependencies

name := """play-scala-angular2-analytics"""
organization := "com.example"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.3"

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test

libraryDependencies += "com.google.apis" % "google-api-services-analytics" % "v3-rev142-1.22.0"
libraryDependencies += "com.google.apis" % "google-api-services-analyticsreporting" % "v4-rev116-1.22.0"
libraryDependencies += "com.google.api-client" % "google-api-client" % "1.22.0"
libraryDependencies += "com.google.oauth-client" % "google-oauth-client" % "1.22.0"
libraryDependencies += "com.google.http-client" % "google-http-client-gson" % "1.22.0"

//libraryDependencies += ehcache
//libraryDependencies += cacheApi

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.example.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.example.binders._"
