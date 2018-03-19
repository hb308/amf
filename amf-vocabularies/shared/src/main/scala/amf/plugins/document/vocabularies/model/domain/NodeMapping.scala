package amf.plugins.document.vocabularies.model.domain

import amf.client.model.StrField
import amf.core.metamodel.Obj
import amf.core.model.domain.{DomainElement, Linkable}
import amf.core.parser.{Annotations, Fields}
import amf.core.utils._
import amf.plugins.document.vocabularies.metamodel.domain.NodeMappingModel
import amf.plugins.document.vocabularies.metamodel.domain.NodeMappingModel._
import org.yaml.model.YMap

case class NodeMapping(fields: Fields, annotations: Annotations) extends DomainElement with Linkable {

  override def meta: Obj = NodeMappingModel
  override def adopted(parent: String): NodeMapping.this.type = withId(parent + "/" + name.value().urlEncoded)

  def name: StrField                                     = fields.field(Name)
  def withName(name: String)                             = set(Name, name)
  def nodetypeMapping: StrField                          = fields.field(NodeTypeMapping)
  def withNodeTypeMapping(nodeType: String)              = set(NodeTypeMapping, nodeType)
  def propertiesMapping(): Seq[PropertyMapping]          = fields.field(PropertiesMapping)
  def withPropertiesMapping(props: Seq[PropertyMapping]) = setArrayWithoutId(PropertiesMapping, props)

  override def linkCopy(): Linkable = NodeMapping().withId(id)

  override def resolveUnreferencedLink[T](label: String, annotations: Annotations, unresolved: T): T = {
    val unresolvedNodeMapping = unresolved.asInstanceOf[NodeMapping]
    unresolvedNodeMapping.link(label, annotations).asInstanceOf[NodeMapping].withId(unresolvedNodeMapping.id).withName(unresolvedNodeMapping.name.value()).asInstanceOf[T]
  }
}

object NodeMapping {
  def apply(): NodeMapping = apply(Annotations())

  def apply(ast: YMap): NodeMapping = apply(Annotations(ast))

  def apply(annotations: Annotations): NodeMapping = NodeMapping(Fields(), annotations)
}