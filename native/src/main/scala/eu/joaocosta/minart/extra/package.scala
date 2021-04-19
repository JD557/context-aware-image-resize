package eu.joaocosta.minart

import java.io.{ FileInputStream, InputStream }
import scala.io.Source

import eu.joaocosta.minart.backend.defaults.DefaultBackend

package object extra {
  implicit val nativeResourceLoader: DefaultBackend[Any, ResourceLoader] = DefaultBackend.fromConstant(new ResourceLoader {
    def loadResource(name: String): Resource = new Resource {
      def path = "./" + name
      def asSource(): Source = Source.fromFile("./" + name)
      def asInputStream(): InputStream = new FileInputStream("./" + name)
    }
  })
}
