package eu.joaocosta.resize

import scala.util._

case class Args(
    width: Double,
    height: Double,
    filename: String,
    output: Option[String]
)

object Args {
  val supportedInputs  = Set("pgm", "ppm", "bmp", "qoi")
  val supportedOutputs = Set("ppm", "bmp", "qoi")

  def getExtension(file: String): String =
    file.reverse.takeWhile(_ != '.').reverse.toLowerCase

  def parse(args: Array[String]): Either[String, Args] = {
    val initialArgs = Args(1.0, 1.0, "", None)
    def aux(acc: Args, args: List[String]): Either[String, Args] = args match {
      case Nil => Right(acc)
      case "-w" :: width :: xs =>
        width.toDoubleOption match {
          case Some(w) if w >= 0.0 && w <= 2.0 => aux(acc.copy(width = w), xs)
          case _                               => Left("Invalid width: " + width)
        }
      case "-h" :: height :: xs =>
        height.toDoubleOption match {
          case Some(h) if h >= 0.0 && h <= 2.0 => aux(acc.copy(height = h), xs)
          case _                               => Left("Invalid height: " + height)
        }
      case "-o" :: output :: xs =>
        val extension = getExtension(output)
        if (supportedOutputs.contains(extension)) aux(acc.copy(output = Some(output)), xs)
        else Left("Unsupported output image format: " + extension)
      case filename :: xs =>
        val extension = getExtension(filename)
        if (supportedInputs.contains(extension)) aux(acc.copy(filename = filename), xs)
        else Left("Unsupported input image format: " + extension)
    }
    aux(initialArgs, args.toList)
  }
}
