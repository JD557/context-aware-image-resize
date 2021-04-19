package eu.joaocosta.minart.extra

import scala.io.Source

import eu.joaocosta.minart.backend.defaults.DefaultBackend

trait ResourceLoader {
  def loadResource(name: String): Resource
}

object ResourceLoader {
  def default()(implicit d: DefaultBackend[Any, ResourceLoader]): ResourceLoader =
    d.defaultValue(())
}
