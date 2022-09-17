package eu.joaocosta.resize

import eu.joaocosta.minart.graphics._

object Resizer {

  private def removeColumn(image: Surface): RamSurface = {
    val energy     = Energy.fromImage(image)
    val seamEnergy = SeamEnergy.fromEnergy(energy)
    val seam       = seamEnergy.minSeam
    val newPixels = image.getPixels().zip(seam).map { case (line, pixelToRemove) =>
      line.take(pixelToRemove) ++ line.drop(pixelToRemove + 1)
    }
    new RamSurface(newPixels)
  }

  private def computeColumnSeam(energy: Energy): (List[Int], Energy) = {
    val seamEnergy = SeamEnergy.fromEnergy(energy)
    val seam       = seamEnergy.minSeam
    val newEnergies = energy.energies.zip(seam).map { case (line, pixel) =>
      line.updated(pixel, Energy.maxValue)
    }
    seam -> Energy(newEnergies)
  }

  private def addColumn(image: Surface, seam: List[Int]): RamSurface = {
    val newPixels = image.getPixels().zip(seam).map { case (line, pixelToAdd) =>
      val left       = line.take(pixelToAdd)
      val right      = line.drop(pixelToAdd)
      val leftPixel  = left.lastOption
      val rightPixel = right.headOption
      val middlePixel = (leftPixel, rightPixel) match {
        case (Some(c1), Some(c2)) =>
          (c1 + c2) * Color.grayscale(127)
        case (Some(l), None) => l
        case (None, Some(r)) => r
        case _               => Color.grayscale(0)
      }
      left ++ Array(middlePixel) ++ right
    }
    new RamSurface(newPixels)
  }

  private def removeNColumns(image: Surface, n: Int): Surface = {
    if (n <= 0) image
    else removeNColumns(removeColumn(image), n - 1)
  }

  private def removeNLines(image: Surface, n: Int): Surface = {
    removeNColumns(image.view.transpose, n).view.transpose
  }

  private def addNColumns(image: Surface, n: Int): Surface = {
    val seams: List[List[Int]] = Iterator
      .unfold(Energy.fromImage(image)) { case energy =>
        Some(computeColumnSeam(energy))
      }
      .drop(1)
      .take(n)
      .toList
      .sortBy(_.head)
      .zipWithIndex
      .map { case (seam, delta) => seam.map(_ + delta) }
    seams.foldLeft(image) { case (image, seam) => addColumn(image, seam) }
  }

  private def addNLines(image: Surface, n: Int): Surface = {
    addNColumns(image.view.transpose, n).view.transpose
  }

  def resizeColumns(image: Surface, n: Int): Surface =
    if (n > 0) addNColumns(image, n)
    else if (n < 0) removeNColumns(image, -n)
    else image

  def resizeLines(image: Surface, n: Int): Surface =
    if (n > 0) addNLines(image, n)
    else if (n < 0) removeNLines(image, -n)
    else image

}
