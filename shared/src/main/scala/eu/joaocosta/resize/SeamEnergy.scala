package eu.joaocosta.resize

import eu.joaocosta.minart.core.Color
import eu.joaocosta.minart.extra.Image

case class SeamEnergy(seamEnergies: Array[Array[Int]]) {
  val width         = seamEnergies.headOption.map(_.size).getOrElse(0)
  val height        = seamEnergies.size
  lazy val maxValue = seamEnergies.map(_.max).max
  def toImage: Image = Image(seamEnergies.toVector.map(_.map { e =>
    val intensity = (255 * e / maxValue).toInt
    Color(intensity, intensity, intensity).argb
  }))

  def minSeam: List[Int] = {
    def minSeamAux(x: Int, y: Int, acc: List[Int] = Nil): List[Int] = {
      if (y >= height) acc.reverse
      else {
        val nextX = Iterator(x - 1, x, x + 1)
          .filter(xx => xx >= 0 && xx < width)
          .minBy(xx => seamEnergies(y)(xx))
        minSeamAux(nextX, y + 1, nextX :: acc)
      }
    }
    val bestStartX = (0 until width).minBy(x => seamEnergies(0)(x))
    minSeamAux(bestStartX, 1, List(bestStartX))
  }
}

object SeamEnergy {
  def fromEnergy(energy: Energy): SeamEnergy = {
    val xRange  = (0 until energy.width)
    val scratch = Array.fill(energy.height)(Array.fill(energy.width)(-1))
    xRange.foreach(x => scratch(energy.height - 1)(x) = energy.energies(energy.height - 1)(x))
    (0 until energy.height - 1).reverse.foreach { y =>
      xRange.foreach { x =>
        val middle = scratch(y + 1)(x)
        val left   = if (x > 0) scratch(y + 1)(x - 1) else Int.MaxValue
        val right  = if (x < energy.width - 2) scratch(y + 1)(x + 1) else Int.MaxValue
        scratch(y)(x) = energy.energies(y)(x) + math.min(middle, math.min(left, right))
      }
    }
    SeamEnergy(scratch)
  }
}
