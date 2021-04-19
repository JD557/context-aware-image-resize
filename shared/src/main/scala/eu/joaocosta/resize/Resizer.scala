package eu.joaocosta.resize

import eu.joaocosta.minart.core.Color
import eu.joaocosta.minart.extra.Image

object Resizer {

  // For some reason, Image.transpose is not working in scala native
  private def transposeImage(image: Image): Image = {
    val pixels = image.pixels.transpose.map(_.toArray)
    Image(pixels)
  }

  private def removeColumn(image: Image): Image = {
    val energy     = Energy.fromImage(image)
    val seamEnergy = SeamEnergy.fromEnergy(energy)
    val seam       = seamEnergy.minSeam
    val newPixels = image.pixels.zip(seam).map { case (line, pixelToRemove) =>
      line.take(pixelToRemove) ++ line.drop(pixelToRemove + 1)
    }
    Image(newPixels)
  }

  private def computeColumnSeam(energy: Energy): (List[Int], Energy) = {
    val seamEnergy = SeamEnergy.fromEnergy(energy)
    val seam       = seamEnergy.minSeam
    val newEnergies = energy.energies.zip(seam).map { case (line, pixel) =>
      line.updated(pixel, Energy.maxValue)
    }
    seam -> Energy(newEnergies)
  }

  private def addColumn(image: Image, seam: List[Int]): Image = {
    val newPixels = image.pixels.zip(seam).map { case (line, pixelToAdd) =>
      val left       = line.take(pixelToAdd)
      val right      = line.drop(pixelToAdd)
      val leftPixel  = left.lastOption
      val rightPixel = right.headOption
      val middlePixel = (leftPixel, rightPixel) match {
        case (Some(l), Some(r)) =>
          val Color(r1, g1, b1) = Color.fromRGB(l)
          val Color(r2, g2, b2) = Color.fromRGB(r)
          Color((r1 + r2) / 2, (g1 + g2) / 2, (b1 + b2) / 2).argb
        case (Some(l), None) => l
        case (None, Some(r)) => r
        case _               => 0
      }
      left ++ Array(middlePixel) ++ right
    }
    Image(newPixels)
  }

  private def removeNColumns(image: Image, n: Int): Image = {
    if (n <= 0) image
    else removeNColumns(removeColumn(image), n - 1)
  }

  private def removeNLines(image: Image, n: Int): Image = {
    transposeImage(removeNColumns(transposeImage(image), n))
  }

  private def addNColumns(image: Image, n: Int): Image = {
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

  private def addNLines(image: Image, n: Int): Image = {
    transposeImage(addNColumns(transposeImage(image), n))
  }

  def resizeColumns(image: Image, n: Int): Image =
    if (n > 0) addNColumns(image, n)
    else if (n < 0) removeNColumns(image, -n)
    else image

  def resizeLines(image: Image, n: Int): Image =
    if (n > 0) addNLines(image, n)
    else if (n < 0) removeNLines(image, -n)
    else image

}
