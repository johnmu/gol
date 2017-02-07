/**
  * Created by johnmu on 2/5/2017.
  */
case class Cell(val x: Int, val y: Int) {
  def translate(by: (Int, Int)): Cell = Cell(this.x + by._1, this.y + by._2)
}
