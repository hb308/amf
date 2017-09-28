package amf.model

import scala.scalajs.js
import scala.scalajs.js.JSConverters._
import scala.scalajs.js.annotation.JSExportAll

/**
  * JVM AbstractDeclaration model class.
  */
abstract class AbstractDeclaration private[model] (private val declaration: amf.domain.`abstract`.AbstractDeclaration)
    extends DomainElement {
  override private[amf] def element: amf.domain.`abstract`.AbstractDeclaration

  val name: String                     = declaration.name
  val dataNode: DataNode               = DataNode(declaration.dataNode)
  val variables: js.Iterable[Variable] = declaration.variables.map(Variable).toJSArray

  /** Set name property of this [[AbstractDeclaration]]. */
  def withName(name: String): this.type = {
    declaration.withName(name)
    this
  }

  /** Set the dataNode property of this [[AbstractDeclaration]]. */
  def withDataNode(dataNode: DataNode): this.type = {
    declaration.withDataNode(dataNode.dataNode)
    this
  }

  /** Set variables property of this [[AbstractDeclaration]]. */
  def withVariables(variables: js.Iterable[Variable]): this.type = {
    declaration.withVariables(variables.map(_.element).toSeq)
    this
  }

  def withVariable(name: String): Variable = Variable(declaration.withVariable(name))
}

@JSExportAll
case class ResourceType private[model] (private val resourceType: amf.domain.`abstract`.ResourceType)
    extends AbstractDeclaration(resourceType) {
  def this() = this(amf.domain.`abstract`.ResourceType())

  override private[amf] def element: amf.domain.`abstract`.ResourceType = resourceType
}

@JSExportAll
case class Trait private[model] (private val tr: amf.domain.`abstract`.Trait) extends AbstractDeclaration(tr) {
  def this() = this(amf.domain.`abstract`.Trait())

  override private[amf] def element: amf.domain.`abstract`.Trait = tr
}
