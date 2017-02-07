/**
  * Created by johnmu on 2/3/2017.
  */

import java.nio.file.Paths

import com.typesafe.scalalogging.LazyLogging

import scala.collection.generic.CanBuildFrom
import scala.collection.mutable
import scala.collection.parallel.ParSet

object Gol extends App with LazyLogging {
  if (args.length != 3) {
    throw new RuntimeException("Must be 3 arguments, see ../README.md for details")
  }

  val numGenerations = args(0).toInt
  val inputGrid = Paths.get(args(1))
  val outputGif = Paths.get(args(2))

  logger.info("Start loading file")
  val cells = RleFile(inputGrid).cells.toSet
  logger.info("Done loading file")

  logger.info("Start iterations")

  def b3s23Rule(alive: Boolean, neighborsAlive: Int): Boolean = {
    if (alive) {
      if (neighborsAlive < 2 || neighborsAlive > 3) false else true
    } else {
      if (neighborsAlive == 3) true else false
    }
  }

  val neighborIndices = (for (x <- -1 to 1;y <- -1 to 1) yield (x,y))
  def countNeighbors(currCell:Cell, cells:ParSet[Cell]):Int = neighborIndices.filter(_ != (0,0)).map(delta => cells.contains(currCell translate delta)).count(identity)

  val generator: Stream[ParSet[Cell]] = cells.par #:: generator.map(currCells => {
    currCells.par.flatMap(cell => neighborIndices.map(delta => cell translate delta).filter(cell=> b3s23Rule(currCells contains cell, countNeighbors(cell, currCells))))
  })

  val generations = generator.take(numGenerations).toList
  println(generations)

  logger.info("Done iterations")

  logger.info("Start output")
  logger.info("Done output")
}
