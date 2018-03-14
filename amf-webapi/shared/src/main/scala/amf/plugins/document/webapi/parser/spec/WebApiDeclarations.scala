package amf.plugins.document.webapi.parser.spec

import amf.core.model.domain.extensions.CustomDomainProperty
import amf.core.model.domain.{DomainElement, Shape}
import amf.core.parser.{
  Annotations,
  Declarations,
  EmptyFutureDeclarations,
  ErrorHandler,
  Fields,
  FutureDeclarations,
  SearchScope
}
import amf.plugins.document.webapi.parser.spec.WebApiDeclarations._
import amf.plugins.domain.shapes.models.{AnyShape, CreativeWork, Example}
import amf.plugins.domain.webapi.models.security.SecurityScheme
import amf.plugins.domain.webapi.models.templates.{ResourceType, Trait}
import amf.plugins.domain.webapi.models.{Parameter, Payload, Response}
import org.yaml.model.YPart

/**
  * Declarations object.
  */
class WebApiDeclarations(libs: Map[String, WebApiDeclarations] = Map(),
                         frags: Map[String, DomainElement] = Map(),
                         var shapes: Map[String, Shape] = Map(),
                         anns: Map[String, CustomDomainProperty] = Map(),
                         var resourceTypes: Map[String, ResourceType] = Map(),
                         var parameters: Map[String, Parameter] = Map(),
                         var payloads: Map[String, Payload] = Map(),
                         var traits: Map[String, Trait] = Map(),
                         var securitySchemes: Map[String, SecurityScheme] = Map(),
                         var responses: Map[String, Response] = Map(),
                         errorHandler: Option[ErrorHandler],
                         futureDeclarations: FutureDeclarations)
    extends Declarations(libs, frags, anns, errorHandler, futureDeclarations = futureDeclarations) {

  override def +=(element: DomainElement): WebApiDeclarations = {
    element match {
      case r: ResourceType =>
        futureDeclarations.resolveRef(r.name, r)
        resourceTypes = resourceTypes + (r.name -> r)
      case t: Trait =>
        futureDeclarations.resolveRef(t.name, t)
        traits = traits + (t.name -> t)
      case s: Shape =>
        futureDeclarations.resolveRef(s.name, s)
        shapes = shapes + (s.name -> s)
      case p: Parameter =>
        futureDeclarations.resolveRef(p.name, p)
        parameters = parameters + (p.name -> p)
      case ss: SecurityScheme =>
        futureDeclarations.resolveRef(ss.name, ss)
        securitySchemes = securitySchemes + (ss.name -> ss)
      case re: Response =>
        futureDeclarations.resolveRef(re.name, re)
        responses = responses + (re.name -> re)
      case _ => super.+=(element)
    }
    this
  }

  /** Find domain element with the same name. */
  override def findEquivalent(element: DomainElement): Option[DomainElement] = element match {
    case r: ResourceType    => findResourceType(r.name, SearchScope.All)
    case t: Trait           => findTrait(t.name, SearchScope.All)
    case s: Shape           => findType(s.name, SearchScope.All)
    case p: Parameter       => findParameter(p.name, SearchScope.All)
    case ss: SecurityScheme => findSecurityScheme(ss.name, SearchScope.All)
    case re: Response       => findResponse(re.name, SearchScope.All)
    case _                  => super.findEquivalent(element)
  }

  def registerParameter(parameter: Parameter, payload: Payload): Unit = {
    parameters = parameters + (parameter.name -> parameter)
    payloads = payloads + (parameter.name     -> payload)
  }

  def parameterPayload(parameter: Parameter): Payload = payloads(parameter.name)

  /** Get or create specified library. */
  override def getOrCreateLibrary(alias: String): WebApiDeclarations = {
    libraries.get(alias) match {
      case Some(lib: WebApiDeclarations) => lib
      case _ =>
        val result =
          new WebApiDeclarations(errorHandler = errorHandler, futureDeclarations = EmptyFutureDeclarations())
        libraries = libraries + (alias -> result)
        result
    }
  }

  override def declarables(): Seq[DomainElement] =
    super
      .declarables()
      .toList ++ (shapes.values ++ resourceTypes.values ++ traits.values ++ parameters.values ++ securitySchemes.values ++ responses.values).toList

  def findParameterOrError(ast: YPart)(key: String, scope: SearchScope.Scope): Parameter =
    findParameter(key, scope) match {
      case Some(result) => result
      case _ =>
        error(s"Parameter '$key' not found", ast)
        ErrorParameter
    }

  def findParameter(key: String, scope: SearchScope.Scope): Option[Parameter] =
    findForType(key, _.asInstanceOf[WebApiDeclarations].parameters, scope) collect {
      case p: Parameter => p
    }

  def findResourceTypeOrError(ast: YPart)(key: String, scope: SearchScope.Scope): ResourceType =
    findResourceType(key, scope) match {
      case Some(result) => result
      case _ =>
        error(s"ResourceType $key not found", ast)
        ErrorResourceType
    }

  def findResourceType(key: String, scope: SearchScope.Scope): Option[ResourceType] =
    findForType(key, _.asInstanceOf[WebApiDeclarations].resourceTypes, scope) collect {
      case r: ResourceType => r
    }

  def findDocumentations(key: String, scope: SearchScope.Scope): Option[CreativeWork] =
    findForType(key, Map(), scope) collect {
      case u: CreativeWork => u
    }

  def findTraitOrError(ast: YPart)(key: String, scope: SearchScope.Scope): Trait = findTrait(key, scope) match {
    case Some(result) => result
    case _ =>
      error(s"Trait $key not found", ast)
      ErrorTrait
  }

  private def findTrait(key: String, scope: SearchScope.Scope): Option[Trait] =
    findForType(key, _.asInstanceOf[WebApiDeclarations].traits, scope) collect {
      case t: Trait => t
    }

  def findType(key: String, scope: SearchScope.Scope): Option[AnyShape] =
    findForType(key, _.asInstanceOf[WebApiDeclarations].shapes, scope) collect {
      case s: AnyShape => s
    }

  def findSecuritySchemeOrError(ast: YPart)(key: String, scope: SearchScope.Scope): SecurityScheme =
    findSecurityScheme(key, scope) match {
      case Some(result) => result
      case _ =>
        error(s"SecurityScheme '$key' not found", ast)
        ErrorSecurityScheme
    }

  def findSecurityScheme(key: String, scope: SearchScope.Scope): Option[SecurityScheme] =
    findForType(key, _.asInstanceOf[WebApiDeclarations].securitySchemes, scope) collect {
      case ss: SecurityScheme => ss
    }

  def findResponse(key: String, scope: SearchScope.Scope): Option[Response] =
    findForType(key, _.asInstanceOf[WebApiDeclarations].responses, scope) collect {
      case re: Response => re
    }

  def findResponseOrError(ast: YPart)(key: String, searchScope: SearchScope.Scope): Response =
    findResponse(key, searchScope) match {
      case Some(result) => result
      case _ =>
        error(s"Response '$key' not found", ast)
        ErrorResponse
    }

  def findNamedExampleOrError(ast: YPart)(key: String): Example = findNamedExample(key) match {
    case Some(result) => result
    case _ =>
      error(s"NamedExample '$key' not found", ast)
      ErrorNamedExample
  }

  def findNamedExample(key: String): Option[Example] = fragments.get(key) collect { case e: Example => e }

}

object WebApiDeclarations {

  def apply(declarations: Seq[DomainElement],
            errorHandler: Option[ErrorHandler],
            futureDeclarations: FutureDeclarations): WebApiDeclarations = {
    val result = new WebApiDeclarations(errorHandler = errorHandler, futureDeclarations = futureDeclarations)
    declarations.foreach(result += _)
    result
  }

  trait ErrorDeclaration

  object ErrorTrait          extends Trait(Fields(), Annotations()) with ErrorDeclaration
  object ErrorResourceType   extends ResourceType(Fields(), Annotations()) with ErrorDeclaration
  object ErrorSecurityScheme extends SecurityScheme(Fields(), Annotations()) with ErrorDeclaration
  object ErrorNamedExample   extends Example(Fields(), Annotations()) with ErrorDeclaration
  object ErrorCreativeWork   extends CreativeWork(Fields(), Annotations()) with ErrorDeclaration
  object ErrorParameter      extends Parameter(Fields(), Annotations()) with ErrorDeclaration
  object ErrorResponse       extends Response(Fields(), Annotations()) with ErrorDeclaration
}
