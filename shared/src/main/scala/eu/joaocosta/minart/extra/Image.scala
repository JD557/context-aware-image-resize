package eu.joaocosta.minart.extra

import scala.io.{BufferedSource, Source}
import scala.util.Try

import eu.joaocosta.minart.core._
import eu.joaocosta.minart.pure._

case class Image(pixels: Vector[Array[Int]]) {

  val width  = pixels.headOption.map(_.size).getOrElse(0)
  val height = pixels.size

  private[this] val lines   = (0 until height)
  private[this] val columns = (0 until width)

  def getPixel(x: Int, y: Int): Option[Color] = {
    if (x < 0 || y < 0 || x >= width || y >= height) None
    else Some(Color.fromRGB(pixels(y)(x)))
  }

  def renderUnsafe(canvas: Canvas, x: Int, y: Int, mask: Option[Color]): Unit =
    for {
      iy <- lines
      ix <- columns
    } {
      val pixel = Color.fromRGB(pixels(iy)(ix))
      if (!mask.contains(pixel)) canvas.putPixel(x + ix, y + iy, pixel)
    }

  def renderUnsafe(canvas: Canvas, x: Int, y: Int): Unit = renderUnsafe(canvas, x, y, None)

  def renderUnsafe(canvas: Canvas, x: Int, y: Int, cx: Int, cy: Int, cw: Int, ch: Int, mask: Option[Color]): Unit =
    for {
      iy <- lines
      if iy >= cy && iy < cy + ch
      ix <- columns
      if ix >= cx && ix < cx + cw
    } {
      val pixel = Color.fromRGB(pixels(iy)(ix))
      if (!mask.contains(pixel)) canvas.putPixel(x + ix - cx, y + iy - cy, pixel)
    }

  def renderUnsafe(canvas: Canvas, x: Int, y: Int, cx: Int, cy: Int, cw: Int, ch: Int): Unit =
    renderUnsafe(canvas, x, y, cx, cy, cw, ch, None)

  def render(x: Int, y: Int, mask: Option[Color]): CanvasIO[Unit] = {
    val ops = (for {
      iy <- lines
      ix <- columns
      pixel = Color.fromRGB(pixels(iy)(ix))
      if !mask.contains(pixel)
    } yield CanvasIO.putPixel(x + ix, y + iy, pixel))
    CanvasIO.sequence_(ops)
  }

  def render(x: Int, y: Int): CanvasIO[Unit] = render(x, y, None)

  def render(x: Int, y: Int, cx: Int, cy: Int, cw: Int, ch: Int, mask: Option[Color]): CanvasIO[Unit] = {
    val ops = (for {
      iy <- lines
      if iy >= cy && iy < cy + ch
      ix <- columns
      if ix >= cx && ix < cx + cw
      pixel = Color.fromRGB(pixels(iy)(ix))
      if !mask.contains(pixel)
    } yield CanvasIO.putPixel(x + ix - cx, y + iy - cy, pixel))
    CanvasIO.sequence_(ops)
  }

  def render(x: Int, y: Int, cx: Int, cy: Int, cw: Int, ch: Int): CanvasIO[Unit] =
    render(x, y, cx, cy, cw, ch, None)

  lazy val invert =
    Image(pixels.map(_.map { rc =>
      val c = Color.fromRGB(rc)
      Color(255 - c.r, 255 - c.g, 255 - c.b).argb
    }))

  lazy val flipH = Image(pixels.map(_.reverse))

  lazy val flipV = Image(pixels.reverse)

  lazy val transpose = Image(pixels.transpose.map(_.toArray))
}

object Image {
  val empty: Image = Image(Vector.empty)

  def loadPpmImage(resource: Resource): Try[Image] = Try {
    println("Loading resource")
    val inputStream                  = resource.asInputStream()
    val byteIterator: Iterator[Int]  = Iterator.continually(inputStream.read()).takeWhile(_ != -1)
    val charIterator: Iterator[Char] = byteIterator.map(_.toChar)
    def nextLine(): String           = charIterator.takeWhile(_ != '\n').mkString("")
    val lineIt                       = Iterator.continually(nextLine())
    val stringIt                     = lineIt.filterNot(_.startsWith("#")).flatMap(_.split(" "))
    val builder                      = Array.newBuilder[Int]
    val format                       = stringIt.next()
    val width                        = stringIt.next().toInt
    val height                       = stringIt.next().toInt
    require(stringIt.next() == "255", "Invalid color range")
    format match {
      case "P3" =>
        val intIterator = stringIt.map(_.toInt)
        println("Reading pixels...")
        (0 until (width * height)).foreach { _ =>
          val color = Color(intIterator.next(), intIterator.next(), intIterator.next()).argb
          builder += color
        }
        inputStream.close()
      case "P6" =>
        val intIterator = byteIterator
        println("Reading pixels...")
        (0 until (width * height)).foreach { _ =>
          val color = Color(intIterator.next(), intIterator.next(), intIterator.next()).argb
          builder += color
        }
        inputStream.close()
      case fmt =>
        inputStream.close()
        throw new Exception("Invalid pixel format: " + fmt)
    }
    println("Formatting")
    val pixels = builder.result().sliding(width, width).map(_.toArray).toVector
    println("Done")
    Image(pixels)
  }
}
