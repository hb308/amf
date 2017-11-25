package amf.framework.parser

import org.yaml.model.{YComment, YDocument}

case class ParsedDocument(comment: Option[YComment], document: YDocument)
