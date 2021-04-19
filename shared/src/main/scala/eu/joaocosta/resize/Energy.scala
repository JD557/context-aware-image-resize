package eu.joaocosta.resize

import eu.joaocosta.minart.core.Color
import eu.joaocosta.minart.extra.Image

case class Energy(energies: Vector[Vector[Int]]) {
  val width         = energies.headOption.map(_.size).getOrElse(0)
  val height        = energies.size
  lazy val maxValue = math.ceil(math.sqrt(Math.fastSquare(255) * 6))
  def toImage: Image = Image(energies.map(_.map { e =>
    val intensity = (255 * e / maxValue).toInt
    Color(intensity, intensity, intensity).argb
  }.toArray))
}

object Energy {
  def fromImage(image: Image): Energy = {
    val xRange = (0 until image.width)
    Energy(image.pixels.map { case line =>
      xRange.map { x =>
        val middle = Color.fromRGB(line(x))
        val left   = if (x > 0) Color.fromRGB(line(x - 1)) else middle
        val right  = if (x < image.width - 2) Color.fromRGB(line(x + 1)) else middle
        val r      = Math.fastSquare(left.r - middle.r) + Math.fastSquare(right.r - middle.r)
        val g      = Math.fastSquare(left.g - middle.g) + Math.fastSquare(right.g - middle.g)
        val b      = Math.fastSquare(left.b - middle.b) + Math.fastSquare(right.b - middle.b)
        math.sqrt(r + g + b).toInt
      }.toVector
    })
  }
}
