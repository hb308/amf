package amf.client.model.domain

import amf.client.convert.WebApiClientConverters._
import amf.client.model.StrField

import scala.scalajs.js.annotation.{JSExportTopLevel, JSExportAll}
import amf.plugins.domain.webapi.models.bindings.kafka.{KafkaOperationBinding => InternalKafkaOperationBinding}

@JSExportAll
class KafkaOperationBinding(override private[amf] val _internal: InternalKafkaOperationBinding)
    extends OperationBinding
    with BindingVersion {
  @JSExportTopLevel("model.domain.KafkaOperationBinding")
  def this() = this(InternalKafkaOperationBinding())
  override protected def bindingVersion: StrField = _internal.bindingVersion
  override def withBindingVersion(bindingVersion: String): this.type = {
    _internal.withBindingVersion(bindingVersion)
    this
  }

  def groupId: StrField  = _internal.groupId
  def clientId: StrField = _internal.clientId

  def withGroupId(groupId: String): this.type = {
    _internal.withGroupId(groupId)
    this
  }

  def withClientId(clientId: String): this.type = {
    _internal.withClientId(clientId)
    this
  }

  override def linkCopy(): KafkaOperationBinding = _internal.linkCopy()

}
