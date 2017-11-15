package amf.spec.domain

import amf.domain.Annotation.{SingleValueArray, SynthesizedField}
import amf.domain.{Annotations, Example}
import amf.metadata.domain.ExampleModel
import amf.model.AmfScalar
import amf.parser.{YMapOps, YScalarYRead}
import amf.spec.ParserContext
import amf.spec.common.{AnnotationParser, ValueNode}
import org.yaml.model._
import org.yaml.render.YamlRender

import scala.collection.mutable.ListBuffer

/**
  *
  */
case class OasResponseExamplesParser(key: String, map: YMap)(implicit ctx: ParserContext) {
  def parse(): Seq[Example] = {
    val results = ListBuffer[Example]()
    map
      .key(key)
      .foreach(entry => {
        entry.value
          .as[YMap]
          .regex(".*/.*")
          .map(e => results += OasResponseExampleParser(e).parse())
      })

    results
  }
}

case class OasResponseExampleParser(yMapEntry: YMapEntry)(implicit ctx: ParserContext) {
  def parse(): Example = {
    val example = Example(yMapEntry)
      .set(ExampleModel.MediaType, yMapEntry.key.as[YScalar].text)
      .withStrict(false)
    RamlExampleValueAsString(yMapEntry.value, example, strict = false).populate()
  }
}

case class RamlExamplesParser(map: YMap, singleExampleKey: String, multipleExamplesKey: String)(
    implicit ctx: ParserContext) {
  def parse(): Seq[Example] =
    RamlMultipleExampleParser(multipleExamplesKey, map).parse() ++
      RamlSingleExampleParser(singleExampleKey, map).parse()

}

case class RamlMultipleExampleParser(key: String, map: YMap)(implicit ctx: ParserContext) {
  def parse(): Seq[Example] = {
    val examples = ListBuffer[Example]()

    map.key(key).foreach { entry =>
      ctx.link(entry.value) match {
        case Left(s) => examples ++= ctx.declarations.findNamedExample(s).map(e => e.link(s).asInstanceOf[Example])
        case Right(node) =>
          node.tagType match {
            case YType.Map =>
              examples ++= node.as[YMap].entries.map(RamlNamedExampleParser(_).parse())
            case YType.Seq =>
            case _         => RamlExampleValueAsString(node, Example(node.as[YScalar]), strict = true).populate()
          }
      }
    }
    examples
  }
}

case class RamlNamedExampleParser(entry: YMapEntry)(implicit ctx: ParserContext) {
  def parse(): Example = {
    val name             = ValueNode(entry.key)
    val example: Example = RamlSingleExampleValueParser(entry.value.as[YMap]).parse()
    example.set(ExampleModel.Name, name.string(), Annotations(entry))
  }
}

case class RamlSingleExampleParser(key: String, map: YMap)(implicit ctx: ParserContext) {
  def parse(): Option[Example] = {
    map.key(key).flatMap { entry =>
      entry.value.tagType match {
        case YType.Map => Option(RamlSingleExampleValueParser(entry.value.as[YMap]).parse().add(SingleValueArray()))
        case YType.Seq =>
          ctx.violation("", "Not supported part type for example", entry.value)
          None
        case _ => // example can be any type or scalar value, like string int datetime etc. We will handle all like strings in this stage
          val scalar = entry.value.as[YScalar]
          Option(
            RamlExampleValueAsString(entry.value, Example(scalar), strict = true).populate().add(SingleValueArray()))
      }
    }
  }
}

case class RamlSingleExampleValueParser(node: YMap)(implicit ctx: ParserContext) {
  def parse(): Example = {
    val isExpanded = node.regex("""displayName|description|strict|value|\(.+\)""").nonEmpty

    val example = Example(node)
    if (isExpanded) {
      node
        .key("displayName")
        .foreach(entry => {
          val value = ValueNode(entry.value)
          example.set(ExampleModel.DisplayName, value.string(), Annotations(entry))
        })
      node
        .key("description")
        .foreach(entry => {
          val value = ValueNode(entry.value)
          example.set(ExampleModel.Description, value.string(), Annotations(entry))
        })
      node
        .key("strict")
        .foreach(entry => {
          val value = ValueNode(entry.value)
          example.set(ExampleModel.Strict, value.boolean(), Annotations(entry))
        })

      node
        .key("value")
        .foreach(entry => {
          RamlExampleValueAsString(entry.value, example, Option(example.strict).getOrElse(true)).populate()
        })
      AnnotationParser(() => example, node).parse()
    } else {
      RamlExampleValueAsString(node, example, strict = true).populate()
    }

    example
  }
}

case class RamlExampleValueAsString(node: YNode, example: Example, strict: Boolean)(implicit ctx: ParserContext) {
  def populate(): Example = {
    if (example.fields.entry(ExampleModel.Strict).isEmpty) {
      example.set(ExampleModel.Strict, AmfScalar(strict), Annotations() += SynthesizedField())
    }
    node.tagType match {
      case YType.Map =>
        example.set(ExampleModel.Value,
                    AmfScalar(YamlRender.render(node.value), Annotations(node.value)),
                    Annotations(node.value))
      case YType.Seq =>
        ctx.violation("", "Not supported part type for example", node)
      case _ =>
        val scalar = node.as[YScalar]
        example.set(ExampleModel.Value, AmfScalar(scalar.text, Annotations(node.value)), Annotations(node.value))
    }

    example
  }
}
