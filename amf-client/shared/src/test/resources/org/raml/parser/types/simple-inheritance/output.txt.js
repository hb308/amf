Model: file://amf-client/shared/src/test/resources/org/raml/parser/types/simple-inheritance/input.raml
Profile: RAML
Conforms? false
Number of results: 1

Level: Violation

- Source: file://amf-client/shared/src/test/resources/org/raml/parser/types/simple-inheritance/input.raml#/declarations/types/Person_validation
  Message: Object at / must be valid
Data at //age must be greater than or equal to 0

  Level: Violation
  Target: file://amf-client/shared/src/test/resources/org/raml/parser/types/simple-inheritance/input.raml#/declarations/types/Office/example/default-example
  Property: http://a.ml/vocabularies/data#age
  Position: Some(LexicalInformation([(15,13)-(21,0)]))
  Location: file://amf-client/shared/src/test/resources/org/raml/parser/types/simple-inheritance/input.raml