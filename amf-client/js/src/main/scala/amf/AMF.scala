package amf

import amf.core.client.{Generator, Parser, Resolver}
import amf.core.plugins.AMFPlugin
import amf.model.document.BaseUnit
import amf.plugins.document.Vocabularies
import amf.plugins.document.vocabularies.spec.Dialect
import amf.validation.AMFValidationReport

import scala.scalajs.js.Promise
import scala.scalajs.js.annotation.{JSExportAll, JSExportTopLevel}

@JSExportAll
@JSExportTopLevel("AMF")
object AMF {

  def init(): Promise[Any] = {
    amf.plugins.document.WebApi.register()
    amf.plugins.document.Vocabularies.register()
    amf.plugins.features.AMFValidation.register()
    amf.Core.init()
  }

  def raml10Parser(): Raml10Parser = new Raml10Parser()

  def ramlParser(): RamlParser = new RamlParser()

  def raml10Generator(): Raml10Generator = new Raml10Generator()

  def raml08Parser(): Raml08Parser = new Raml08Parser()

  def raml08Generator(): Raml08Generator = new Raml08Generator()

  def oas20Parser(): Oas20Parser = new Oas20Parser()

  def oas20Generator(): Oas20Generator = new Oas20Generator()

  def amfGraphParser(): AmfGraphParser = new AmfGraphParser()

  def amfGraphGenerator(): AmfGraphGenerator = new AmfGraphGenerator()

  def validate(model: BaseUnit, profileName: String, messageStyle: String = "AMF"): Promise[AMFValidationReport] =
    amf.Core.validate(model, profileName, messageStyle)

  def loadValidationProfile(url: String): Promise[String] = amf.Core.loadValidationProfile(url)

  def registerNamespace(alias: String, prefix: String): Boolean = amf.Core.registerNamespace(alias, prefix)

  def registerDialect(url: String): Promise[Dialect] = Vocabularies.registerDialect(url)

  def resolveRaml10(unit: BaseUnit): BaseUnit = new Raml10Resolver().resolve(unit)

  def resolveRaml08(unit: BaseUnit): BaseUnit = new Raml08Resolver().resolve(unit)

  def resolveOas20(unit: BaseUnit): BaseUnit = new Oas20Resolver().resolve(unit)

  def resolveAmfGraph(unit: BaseUnit): BaseUnit = new AmfGraphResolver().resolve(unit)
}

@JSExportAll
@JSExportTopLevel("Core")
object CoreWrapper {
  def init(): Promise[Unit] = Core.init()

  def parser(vendor: String, mediaType: String): Parser = amf.Core.parser(vendor, mediaType)

  def generator(vendor: String, mediaType: String): Generator = amf.Core.generator(vendor, mediaType)

  def resolver(vendor: String): Resolver = amf.Core.resolver(vendor)

  def validate(model: BaseUnit, profileName: String, messageStyle: String = "AMF"): Promise[AMFValidationReport] =
    amf.Core.validate(model, profileName, messageStyle)

  def loadValidationProfile(url: String): Promise[String] = amf.Core.loadValidationProfile(url)

  def registerNamespace(alias: String, prefix: String): Boolean = amf.Core.registerNamespace(alias, prefix)

  def registerPlugin(plugin: AMFPlugin): Unit = amf.Core.registerPlugin(plugin)
}

@JSExportAll
@JSExportTopLevel("plugins")
object PluginsWrapper {
  val document = DocumentPluginsWrapper
  val features = FeaturesPluginsWrapper
}

@JSExportAll
object DocumentPluginsWrapper {
  val WebApi       = amf.plugins.document.WebApi
  val Vocabularies = amf.plugins.document.Vocabularies
}

@JSExportAll
object FeaturesPluginsWrapper {
  val AMFValidation = amf.plugins.features.AMFValidation
}
