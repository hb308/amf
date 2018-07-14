package amf.plugins.document.webapi.validation.remote

import amf.core.annotations.LexicalInformation
import amf.core.model.document.PayloadFragment
import amf.core.validation.core.ValidationProfile
import amf.core.validation.{AMFValidationReport, AMFValidationResult, ValidationCandidate, _}
import amf.core.vocabulary.Namespace

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.scalajs.js

object JsJsonSchemaValidator extends PlatformJsonSchemaValidator {

  val validator = AjvValidator()

  override def validate(validationCandidates: Seq[ValidationCandidate], profile: ValidationProfile): Future[AMFValidationReport] = {
    val jsonSchemaCandidates: Seq[(PayloadFragment, String, String, Option[Throwable])] = computeJsonSchemaCandidates(validationCandidates)

    val results = jsonSchemaCandidates flatMap  { case (payload, jsonSchema, shapeId, maybeException) =>

      maybeException match {
        case Some(e: UnknownDiscriminator) => Seq( // error case for polymorphic shapes without matching discriminator
          AMFValidationResult(
            message = "Unknown discriminator value",
            level = SeverityLevels.VIOLATION,
            targetNode = payload.encodes.id,
            targetProperty = None,
            validationId = (Namespace.AmfParser + "exampleError").iri(),
            position = payload.encodes.annotations.find(classOf[LexicalInformation]),
            location = None,
            source = e
          )
        )
        case Some(_)                       => Nil // we ignore other exceptions
        case None                          => // standard ase, we have a payload and shape
          loadDataNodeString(payload) match {
            case Some(dataNode) =>
              var schemaNode = loadJson(jsonSchema).asInstanceOf[js.Dictionary[js.Dynamic]]
              schemaNode -= "x-amf-fragmentType"
              schemaNode -= "example"
              schemaNode -= "examples"

              /*
              println("\n\nValidating...")
              println("  - SCHEMA:")
              println(js.JSON.stringify(schemaNode))
              println("  - DATA:")
              println(js.JSON.stringify(dataNode))
              */

              val correct = validator.validate(schemaNode.asInstanceOf[js.Object], dataNode)

              /*
              println(s"  ====> RESULT: $correct")
              println(js.JSON.stringify(validator.errors))
              println("-----------------------\n\n")
              */

              if (!correct) {
                validator.errors.toOption.getOrElse(js.Array[ValidationResult]()).map { result =>
                  AMFValidationResult(
                    message = js.Dynamic.global.JSON.stringify(result).asInstanceOf[String],
                    level = SeverityLevels.VIOLATION,
                    targetNode = payload.encodes.id,
                    targetProperty = None,
                    validationId = (Namespace.AmfParser + "exampleError").iri(),
                    position = payload.encodes.annotations.find(classOf[LexicalInformation]),
                    location = None,
                    source = result
                  )
                }
              } else {
                Nil
              }
            case _ => Nil
          }
      }
    }

    Future {
      AMFValidationReport(
        conforms = results.isEmpty,
        model = "http://test.com/paylaod#validations",
        profile = profile.name, // profiles.headOption.map(_.name).getOrElse(ProfileNames.AMF)
        results = results
      )
    }
  }




  protected def loadDataNodeString(payload: PayloadFragment): Option[js.Dynamic] = {
    try {
      literalRepresentation(payload) map { payloadText =>
        loadJson(payloadText)
      }
    } catch {
      case _: ExampleUnknownException => None
    }
  }

  protected def loadJson(str: String): js.Dynamic = {
    js.Dynamic.global.JSON.parse(str)
  }

}