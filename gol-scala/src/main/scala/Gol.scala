/**
  * Created by johnmu on 2/3/2017.
  */

import java.nio.file.Paths

import com.typesafe.scalalogging.LazyLogging

object Gol extends App with LazyLogging {
  if (args.length != 3) {
    throw new RuntimeException("Must be 3 arguments, see ../README.md for details")
  }

  val numGenerations = args(0).toInt
  val inputGrid = Paths.get(args(1))
  val outputGif = Paths.get(args(2))

  logger.info("Start loading file")
  val cells = RleFile(inputGrid).cells
  logger.info("Done loading file")
  println(cells)

  logger.info("Start iterations")
  logger.info("Done iterations")

  logger.info("Start output")
  logger.info("Done output")
}
