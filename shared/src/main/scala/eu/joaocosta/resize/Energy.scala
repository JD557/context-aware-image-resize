package eu.joaocosta.resize

import eu.joaocosta.minart.graphics._

case class Energy(energies: Vector[Vector[Int]]) {
  val width  = energies.headOption.map(_.size).getOrElse(0)
  val height = energies.size
  def toImage: RamSurface = new RamSurface(energies.map(_.map { e =>
    val intensity = (255 * e) / Energy.maxValue
    Color.grayscale(intensity)
  }))
}

object Energy {
  final val maxValue = math.ceil(math.sqrt(Math.fastSquare(255) * 6)).toInt
  def fromImage(image: Surface): Energy = {
    val xRange = (0 until image.width)
    Energy(image.getPixels().map { case line =>
      xRange.map { x =>
        val middle = line(x)
        val left   = if (x > 0) line(x - 1) else middle
        val right  = if (x < image.width - 2) line(x + 1) else middle
        val r      = Math.fastSquare(left.r - middle.r) + Math.fastSquare(right.r - middle.r)
        val g      = Math.fastSquare(left.g - middle.g) + Math.fastSquare(right.g - middle.g)
        val b      = Math.fastSquare(left.b - middle.b) + Math.fastSquare(right.b - middle.b)
        math.sqrt(r + g + b).toInt
      }.toVector
    })
  }
}
