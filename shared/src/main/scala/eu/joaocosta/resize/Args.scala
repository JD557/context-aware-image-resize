package eu.joaocosta.resize

import scala.util._

case class Args(
    width: Double,
    height: Double,
    filename: String
)

object Args {
  def parse(args: Array[String]): Either[String, Args] = {
    val initialArgs = Args(1.0, 1.0, "")
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
      case filename :: xs => aux(acc.copy(filename = filename), xs)
    }
    aux(initialArgs, args.toList)
  }
}
