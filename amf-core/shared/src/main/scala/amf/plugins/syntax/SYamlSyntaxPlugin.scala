package amf.plugins.syntax

import amf.core.parser.ParsedDocument
import amf.core.plugins.{AMFPlugin, AMFSyntaxPlugin}
import org.yaml.model.{YComment, YDocument, YMap, YNode}
import org.yaml.parser.YamlParser
import org.yaml.render.{JsonRender, YamlRender}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object SYamlSyntaxPlugin extends AMFSyntaxPlugin {

  override val ID = "SYaml"

  override def init(): Future[AMFPlugin] = Future { this }

  override def dependencies() = Nil

  override def supportedMediaTypes() = Seq(
    "application/yaml",
    "application/x-yaml",
    "text/yaml",
    "text/x-yaml",
    "application/json",
    "text/json",
    "application/raml",
    "text/vnd.yaml"
  )

  override def parse(mediaType: String, text: CharSequence) = {
    val parser = YamlParser(text)
    val parts = parser.parse(true)

    if (parts.exists(v => v.isInstanceOf[YDocument])) {
      parts collectFirst { case d: YDocument => d } map { document =>
        val comment = parts collectFirst { case c: YComment => c }
        ParsedDocument(comment, document)
      }
    } else {
      parts collectFirst { case d: YComment => d } map { comment =>
        ParsedDocument(Some(comment), YDocument(IndexedSeq(YNode(YMap()))))
      }
    }
  }

  override def unparse(mediaType: String, ast: YDocument) = {
    val format = mediaType match {
      case "application/yaml"   => "yaml"
      case "application/x-yaml" => "yaml"
      case "text/yaml"          => "yaml"
      case "text/x-yaml"        => "yaml"
      case "application/json"   => "json"
      case "text/json"          => "json"
      case _                    => if (mediaType.indexOf("json") > -1) {
        "json"
      } else {
        "yaml"
      }
    }

    if (format == "yaml") {
      Some(YamlRender.render(ast))
    } else {
      Some(JsonRender.render(ast))
    }
  }

}
