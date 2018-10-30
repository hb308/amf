package amf.client.convert

import amf.client.convert.WebApiClientConverters._
import amf.client.environment.{Environment => ClientEnvironment}
import amf.client.plugins._
import amf.core.model.document.{PayloadFragment => InternalPayloadFragment}
import amf.core.model.domain.Shape
import amf.core.validation.{AMFPayloadValidationPlugin, AMFValidationReport, PayloadValidator}
import amf.internal.environment.Environment

import scala.concurrent.Future
object ClientPayloadPluginConverter {

  implicit object AMFPluginConverter extends ClientInternalMatcher[ClientAMFPlugin, AMFPlugin] {

    override def asInternal(from: ClientAMFPlugin): AMFPlugin = new AMFPlugin {
      override def init(): Future[AMFPlugin] = new ClientFutureOps(from.init())(AMFPluginConverter).asInternal

      override def dependencies(): Seq[AMFPlugin] =
        new ClientListOps(from.dependencies())(AMFPluginConverter).asInternal

      override val ID: String = from.ID
    }
  }

  def convert(clientPlugin: ClientAMFPayloadValidationPlugin): AMFPayloadValidationPlugin =
    new AMFPayloadValidationPlugin {

      override val payloadMediaType: Seq[String] = clientPlugin.payloadMediaType.asInternal

      override def canValidate(shape: Shape, env: Environment): Boolean =
        clientPlugin.canValidate(ShapeMatcher.asClient(shape), ClientEnvironment(env))

      override val ID: String = clientPlugin.ID

      override def dependencies(): Seq[AMFPlugin] =
        new ClientListOps(clientPlugin.dependencies())(AMFPluginConverter).asInternal

      override def init(): Future[AMFPlugin] = new ClientFutureOps(clientPlugin.init())(AMFPluginConverter).asInternal

      override def validator(s: Shape, env: Environment, validationMode: ValidationMode): PayloadValidator = {
        val validator = clientPlugin.validator(s, ClientEnvironment(env), validationMode)
        new PayloadValidator {
          override val shape: Shape                   = validator.shape
          override val defaultSeverity: String        = validator.defaultSeverity
          override val validationMode: ValidationMode = validator.validationMode
          override val env: Environment               = validator.env._internal
          override def validate(payload: String, mediaType: String): AMFValidationReport =
            validator.validate(payload, mediaType)._internal
          override def validate(payloadFragment: InternalPayloadFragment): AMFValidationReport =
            validator.validate(payloadFragment)._internal

          override def fastValidation(payload: String, mediaType: String): Boolean =
            validator.fastValidation(payload, mediaType)
        }
      }
    }
}
