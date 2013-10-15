import java.io.PrintWriter
import sbt._
import sbt.Keys._
import play.Project._
import scala.io.Source

object ApplicationBuild extends Build {

  val appName         = "closure"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    // Add your project dependencies here,
    jdbc,
    anorm
  )

  def orderJsFiles(extDirectory: File, files: Seq[File]) = files

  def buildJs(managedResourceDirectory: File) {
    println("Running Build Js")
    val extDirectory = managedResourceDirectory / "public" / "javascripts" / "ext"
    val outputFile = new File(extDirectory,  "concat.js")
    println(outputFile)

    val extPathFinder  = extDirectory ** "*.min.js"
    val inputFiles = extPathFinder.get.filter(_ != outputFile)

    val isOuputFileUpToDate = outputFile.exists && inputFiles.forall(_.olderThan(outputFile))

    if (isOuputFileUpToDate) {
      println("nothing to do. concat.js is up to date!")
    } else {
      val sortedUrls = orderJsFiles(extDirectory, inputFiles)

      // Generate concat file
      val writer = new PrintWriter(outputFile)
      for (file <- sortedUrls; line <- Source.fromFile(file).getLines()) writer.println(line)
      writer.close()
    }


//    jsFiles.getPaths.map {
//      file => {
//        println("Reading file: " + file)
//        Source.fromFile(file).foreach( line => writer.write(line))
//      }
//    }
//    writer.close()
//    
//    
//
//    // -- Get list of files for lookup
//    jsFiles.getPaths.map {
//      file => {
//        new File(file).getName.split(".min.js")(0)
//      }
//    }
//
//    val test = "a" :: List()
//    test "b" :: test
//    println(test)
//
//    // -- Find dependencies
//    jsFiles.getPaths.map {
//      file => {
//        val searchFile = new File(file).getName.split(".min.js")(0)
//        println(searchFile)
//        for (
//          line <- Source.fromFile(file).getLines;
//          if line.matches(searchFile)
//        ) println("dependency"+ line)
//      }
//    }
//
//    // Generate concat file
//    val writer = new PrintWriter(new File(jsFiles.get(0).getParentFile , "concat.js" ))
//    jsFiles.getPaths.map {
//      file => {
//        println("Reading file: " + file)
//        Source.fromFile(file).foreach( line => writer.write(line))
//      }
//    }
//    writer.close()
//
//    println("building javascript files: : " + jsFiles.getPaths)
  }


  val hello = TaskKey[Unit]("hello", "Prints 'Hello World'")

  val helloTask = hello <<= (managedResourceDirectories in Compile) map { (baseDir: Seq[File]) => buildJs(baseDir(0))} dependsOn (managedResources in Compile)


  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here
    helloTask,
    //sbt.Keys.`package` in Compile <<= sbt.Keys.`package` in Compile dependsOn hello
    packageBin in Compile <<= packageBin in Compile dependsOn hello
    //dist <<= distTask dependsOn hello


  )

}
