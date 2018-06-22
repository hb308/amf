package amf

import java.io.{InputStream, PrintStream}
import java.util.Scanner

import amf.client.convert.CoreClientConverters
import amf.client.handler.Handler
import amf.client.model.document.{BaseUnit, Document}
import amf.client.parse._
import amf.client.render._
import amf.core.model.document
import amf.core.remote._

class Repl(val in: InputStream, val out: PrintStream) {

  init()

  private def init(): Unit = {
    val scanner                = new Scanner(in)
    var unit: Option[Document] = None

    while (scanner.hasNextLine) {
      scanner.nextLine() match {
        case Exit()               => return
        case Parse((vendor, url)) => remote(vendor, url, unit = _)
        case Generate(syntax)     => unit.foreach(doc => generate(doc, syntax))
        case line                 => out.println(s"... $line")
      }
    }
  }

  private def generate(unit: BaseUnit, syntax: String): Unit = {
    val generator: Option[Renderer] = syntax match {
      case "raml"   => Some(new Raml10Renderer)
      case "raml08" => Some(new Raml08Renderer)
      case "oas"    => Some(new Oas20Renderer)
      case "amf"    => Some(new AmfGraphRenderer)
      case _ =>
        out.println(s"Unsupported generation for: $syntax")
        None
    }

    val handler = new Handler[String] {
      override def error(exception: Throwable): Unit = println(s"An error occurred: $exception")
      override def success(generation: String): Unit = out.print(generation)
    }

    generator.foreach(g => {
      g.generateString(
        unit,
        handler.asInstanceOf[CoreClientConverters.ClientResultHandler[String]]
      )
    })
  }

  private def remote(vendor: Vendor, url: String, callback: (Option[Document]) => Unit): Unit = {
    parser(vendor).parseFile(
      url,
      unitHandler(callback).asInstanceOf[CoreClientConverters.ClientResultHandler[BaseUnit]]
    )
  }

  private def unitHandler(callback: Option[Document] => Unit) = {
    new Handler[BaseUnit] {
      override def success(unit: BaseUnit): Unit = {
        out.println("Successfully parsed. Type `:generate raml` or `:generate oas` or `:generate amf`")
        callback(Some(new Document(unit.asInstanceOf[document.Document])))
      }

      override def error(exception: Throwable): Unit = {
        callback(None)
        out.println(exception)
      }
    }
  }

  private object Parse {
    def unapply(line: String): Option[(Vendor, String)] = {
      line match {
        case s if s.startsWith(":raml ") => Some((Raml, s.stripPrefix(":raml ")))
        case s if s.startsWith(":oas ")  => Some((Oas, s.stripPrefix(":oas ")))
        case s if s.startsWith(":amf ")  => Some((Amf, s.stripPrefix(":amf ")))
        case _                           => None
      }
    }
  }

  private def parser(vendor: Vendor) = vendor match {
    case Raml10  => new Raml10Parser
    case Raml08  => new Raml08Parser
    case Raml    => new RamlParser
    case Oas     => new Oas20Parser
    case Amf     => new AmfGraphParser
    case Payload => throw new Exception("Cannot find a parser for Payload vendor")
    case _       => throw new Exception("Cannot find a parser for Unknown vendor")
  }

  private object Generate {
    def unapply(line: String): Option[String] = {
      line match {
        case s if s.startsWith(":generate ") => Some(s.stripPrefix(":generate "))
        case _                               => None
      }
    }
  }

  private object Exit {
    def unapply(line: String): Boolean = {
      line match {
        case ":exit" | ":quit" | ":q" => true
        case _                        => false
      }
    }
  }

}

object Repl {
  def apply(in: InputStream, out: PrintStream): Repl = new Repl(in, out)
}