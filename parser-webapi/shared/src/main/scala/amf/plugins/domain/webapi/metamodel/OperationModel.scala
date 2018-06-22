package amf.plugins.domain.webapi.metamodel

import amf.core.metamodel.Field
import amf.core.metamodel.Type._
import amf.core.metamodel.domain.DomainElementModel
import amf.core.metamodel.domain.templates.{KeyField, OptionalField}
import amf.core.vocabulary.Namespace.{Document, Http, Hydra, Schema}
import amf.core.vocabulary.{Namespace, ValueType}
import amf.plugins.domain.shapes.metamodel.CreativeWorkModel
import amf.plugins.domain.webapi.metamodel.security.ParametrizedSecuritySchemeModel
import amf.plugins.domain.webapi.models.Operation

/**
  * Operation meta model.
  */
object OperationModel extends DomainElementModel with KeyField with OptionalField {

  val Method = Field(Str, Hydra + "method")

  val Name = Field(Str, Schema + "name")

  val Description = Field(Str, Schema + "description")

  val Deprecated = Field(Bool, Document + "deprecated")

  val Summary = Field(Str, Http + "guiSummary")

  val Documentation = Field(CreativeWorkModel, Schema + "documentation")

  val Schemes = Field(Array(Str), Http + "scheme")

  val Accepts = Field(Array(Str), Http + "accepts")

  val ContentType = Field(Array(Str), Http + "contentType")

  val Request = Field(RequestModel, Hydra + "expects")

  val Responses = Field(Array(ResponseModel), Hydra + "returns")

  val Security = Field(Array(ParametrizedSecuritySchemeModel), Namespace.Security + "security")

  val Tags = Field(Array(Str), Http + "tag")

  val Callbacks = Field(Array(CallbackModel), Http + "callback")

  val Servers = Field(Array(ServerModel), Http + "server")

  override val key: Field = Method

  override val `type`: List[ValueType] = Hydra + "Operation" :: DomainElementModel.`type`

  override val fields: List[Field] = List(Method,
                                          Name,
                                          Description,
                                          Deprecated,
                                          Summary,
                                          Documentation,
                                          Schemes,
                                          Accepts,
                                          ContentType,
                                          Request,
                                          Responses,
                                          Security,
                                          Tags,
                                          Callbacks,
                                          Servers) ++ DomainElementModel.fields

  override def modelInstance = Operation()
}