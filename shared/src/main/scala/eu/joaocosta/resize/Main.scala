package eu.joaocosta.resize

import eu.joaocosta.minart.backend.defaults._
import eu.joaocosta.minart.runtime._
import eu.joaocosta.minart.graphics._
import eu.joaocosta.minart.graphics.image.Image

object Main {

  def main(args: Array[String]): Unit = {
    Args.parse(args) match {
      case Left(error) =>
        println("Invalid arguments: " + error)
      case Right(arguments) =>
        println(arguments)
        val inputResource = Resource(arguments.filename)
        val image =
          Args.getExtension(arguments.filename) match {
            case "pgm" | "ppm" => Image.loadPpmImage(inputResource).get
            case "bmp"         => Image.loadBmpImage(inputResource).get
            case "qoi"         => Image.loadQoiImage(inputResource).get
            case format        => throw new Exception("Unsupported input format:" + format)
          }
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

        arguments.output match {
          case Some(output) =>
            val outputResource = Resource(output)
            Args.getExtension(arguments.filename) match {
              case "ppm"  => Image.storePpmImage(newImage2, outputResource).get
              case "bmp"  => Image.storeBmpImage(newImage2, outputResource).get
              case "qoi"  => Image.storeQoiImage(newImage2, outputResource).get
              case format => throw new Exception("Unsupported output format:" + format)
            }
          case None =>
            println("No output specified, rendering output in a window")
            AppLoop
              .statelessRenderLoop { (canvas: Canvas) =>
                canvas.blit(newImage2)(0, 0)
                canvas.redraw()
              }
              .configure(Canvas.Settings(targetWidth, targetHeight), LoopFrequency.Never)
              .run()
        }
    }
  }
}
