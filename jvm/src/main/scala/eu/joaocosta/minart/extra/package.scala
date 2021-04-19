package eu.joaocosta.minart

import java.io.{FileInputStream, InputStream}
import scala.io.{Codec, Source}
import scala.util.Try

import eu.joaocosta.minart.backend.defaults.DefaultBackend

package object extra {
  implicit val jvmResourceLoader: DefaultBackend[Any, ResourceLoader] = (_) =>
    (name: String) =>
      new Resource {
        def path = "./" + name
        def asSource() =
          Try(Source.fromResource(name)(Codec.ISO8859))
            .getOrElse(Source.fromFile(name))
        def asInputStream(): InputStream =
          Try(Option(this.getClass().getResourceAsStream("/" + name)).get)
            .getOrElse(new FileInputStream(name))
      }
}
