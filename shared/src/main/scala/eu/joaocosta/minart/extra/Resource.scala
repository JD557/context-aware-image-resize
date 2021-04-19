package eu.joaocosta.minart.extra

import java.io.InputStream
import scala.io.Source

trait Resource {
  def path: String

  def asSource(): Source

  def asInputStream(): InputStream
}
