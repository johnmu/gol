import java.nio.file.Path
import java.util.stream.IntStream

import scala.annotation.switch
import scala.collection.mutable.ListBuffer
import scala.io.Source
import scala.util.control.Breaks._

/**
  * Created by johnmu on 2/5/2017.
  */
case class RleFile(path: Path) {
  private val rleLine = Source.fromFile(path.toFile)
    .getLines()
    .map(s => s.trim)
    .filterNot(s => s.isEmpty)
    .filter(s => {
      val first = Character.toLowerCase(s.charAt(0))
      Character.isDigit(first) || first == 'b' || first == 'o' || first == '$' || first == '!'
    })
    .reduce((s1, s2) => s1 + s2)

  def parseLength = (s: String) => if (s.isEmpty) 1 else s.toInt

  private var currY = 0
  private var currX = 0
  private val currCount = new StringBuilder
  private val _cells = new ListBuffer[Cell]
  breakable {
    for (c <- rleLine.toCharArray) {
      val count = parseLength(currCount toString)
      (c: @switch) match {
        case 'b' => {
          currX += count
          currCount clear
        }
        case 'o' => {
          IntStream.range(0, count)
            .forEachOrdered(_ => {
              _cells.append(Cell(currX, currY))
              currX += 1
            })
          currCount clear
        }
        case '$' => {
          currX = 0
          currY += count
          currCount clear
        }
        case '!' => break
        case _ => {
          if (Character.isDigit(c)) currCount.append(c)
          else throw new RuntimeException("Bad RLE string in file: " + rleLine);
        }
      }
    }
  }

  def cells: List[Cell] = _cells.toList
}
