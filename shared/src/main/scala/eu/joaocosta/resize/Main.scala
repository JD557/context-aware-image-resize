package eu.joaocosta.resize

import eu.joaocosta.minart.backend.defaults._
import eu.joaocosta.minart.core._
import eu.joaocosta.minart.extra._

object Main {

  def removeColumn(image: Image): Image = {
    val energy     = Energy.fromImage(image)
    val seamEnergy = SeamEnergy.fromEnergy(energy)
    val seam       = seamEnergy.minSeam
    val newPixels = image.pixels.zip(seam).map { case (line, pixelToRemove) =>
      line.take(pixelToRemove) ++ line.drop(pixelToRemove + 1)
    }
    Image(newPixels)
  }

  def removeNColumns(image: Image, n: Int): Image = {
    if (n <= 0) image
    else removeNColumns(removeColumn(image), n - 1)
  }

  def transposeImage(image: Image): Image = {
    val pixels = image.pixels.transpose.map(_.toArray)
    Image(pixels)
  }

  def removeNLines(image: Image, n: Int): Image = {
    transposeImage(removeNColumns(transposeImage(image), n))
  }

  def main(args: Array[String]): Unit = {
    Args.parse(args) match {
      case Left(error) =>
        println("Invalid arguments: " + error)
      case Right(arguments) =>
        println(arguments)
        val image        = Image.loadPpmImage(ResourceLoader.default().loadResource(arguments.filename)).get
        val targetWidth  = (image.width * arguments.width).toInt
        val targetHeight = (image.height * arguments.height).toInt
        println("Generating a " + targetWidth + " x " + targetHeight + " image")
        val widthDelta  = targetWidth - image.width
        val heightDelta = targetHeight - image.height
        println("Resizing Columns...")
        val newImage1 = Resizer.resizeColumns(image, widthDelta)
        println("Done")
        println("Resizing Lines...")
        val newImage2 = Resizer.resizeLines(newImage1, heightDelta)
        println("Done")

        RenderLoop
          .default()
          .singleFrame(
            CanvasManager.default(),
            Canvas.Settings(targetWidth, targetHeight),
            c => {
              newImage2.render(0, 0).run(c)
              c.redraw()
            }
          )
    }
  }
}
