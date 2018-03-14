package amf.plugins.domain.shapes.models

import amf.core.metamodel.Obj
import amf.core.model.domain.Shape
import amf.core.parser.{Annotations, Fields}
import amf.plugins.domain.shapes.metamodel.AnyShapeModel
import amf.plugins.domain.shapes.metamodel.AnyShapeModel._
import org.yaml.model.YPart

class AnyShape(val fields: Fields, val annotations: Annotations) extends Shape with ShapeHelpers {

  override def adopted(parent: String): this.type = withId(parent + "/any/" + name)

  def documentation: CreativeWork     = fields(Documentation)
  def xmlSerialization: XMLSerializer = fields(XMLSerialization)
  def examples: Seq[Example]          = fields(Examples)

  def withDocumentation(documentation: CreativeWork): this.type        = set(Documentation, documentation)
  def withXMLSerialization(xmlSerialization: XMLSerializer): this.type = set(XMLSerialization, xmlSerialization)
  def withExamples(examples: Seq[Example]): this.type                  = setArray(Examples, examples)

  def withExample(name: Option[String]): Example = {
    val example = Example()
    name.foreach { example.withName }
    add(Examples, example)
    example
  }

  override def linkCopy(): AnyShape = AnyShape().withId(id)

  override def meta: Obj = AnyShapeModel

  def copyAnyShape(fields: Fields = fields, annotations: Annotations = annotations) =
    AnyShape(fields, annotations).withId(id)
}

object AnyShape {
  def apply(): AnyShape = apply(Annotations())

  def apply(ast: YPart): AnyShape = apply(Annotations(ast))

  def apply(annotations: Annotations): AnyShape = AnyShape(Fields(), annotations)

  def apply(fields: Fields, annotations: Annotations): AnyShape = new AnyShape(fields, annotations)
}
