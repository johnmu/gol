/**
  * Created by johnmu on 2/3/2017.
  */

import java.awt.{Color, Graphics2D, Rectangle}
import java.awt.image.BufferedImage
import java.nio.file.Paths
import scala.concurrent.duration._

import com.sksamuel.scrimage.Image
import com.sksamuel.scrimage.nio.GifSequenceWriter
import com.typesafe.scalalogging.LazyLogging

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

  val neighborIndices = (for (x <- -1 to 1; y <- -1 to 1) yield (x, y)).toArray
  val neighborIndicesWithoutSelf = neighborIndices.filter(_ != (0, 0))

  def countNeighbors(currCell: Cell, cells: ParSet[Cell]): Int = neighborIndicesWithoutSelf.map(delta => cells.contains(currCell translate delta)).count(identity)

  val generator: Stream[ParSet[Cell]] = cells.par #:: generator.map(currCells => {
    currCells.par.flatMap(cell => neighborIndices.map(delta => cell translate delta).filter(cell => b3s23Rule(currCells contains cell, countNeighbors(cell, currCells))))
  })

  val startTime = System.nanoTime()
  val generations = generator.take(numGenerations).toList
  val endTime = System.nanoTime()
  println((endTime - startTime) / 1000000.0 + " ms")

  logger.info("Done iterations")

  logger.info("Start output")

  def findBounds[U <: Cell](cells: Seq[U]): Rectangle = cells.foldLeft(new Rectangle())((rect, cell) => {
    rect.add(cell.x, cell.y);
    rect
  })

  val bounds = generations.foldLeft(new Rectangle())((rect, cells) => {
    rect.add(findBounds(cells.toArray.toSeq));
    rect
  })

  def createImage[U <: Cell](cells: Seq[U], rect: Rectangle, zoom: Int): BufferedImage = {
    val image = new BufferedImage(bounds.width * zoom, bounds.height * zoom, BufferedImage.TYPE_INT_RGB)
    val graphics = image.createGraphics()
    graphics.setPaint(Color.WHITE)
    graphics.fillRect(0, 0, bounds.width * zoom, bounds.height * zoom)
    graphics.setPaint(Color.BLACK)
    for (cell <- cells) graphics.fillRect((cell.x - bounds.x) * zoom, (cell.y - bounds.y) * zoom, zoom, zoom)
    image
  }

  implicit val writer = GifSequenceWriter().withInfiniteLoop(true).withFrameDelay(20 milliseconds)
  writer.output(generations.map(cells => Image.fromAwt(createImage(cells.toArray.toSeq, bounds, 2))), outputGif)

  logger.info("Done output")
}
