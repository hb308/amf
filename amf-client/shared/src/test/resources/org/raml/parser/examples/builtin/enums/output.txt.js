Model: file://amf-client/shared/src/test/resources/org/raml/parser/examples/builtin/enums/input.raml
Profile: RAML 1.0
Conforms? false
Number of results: 2

Level: Violation

- Source: http://a.ml/vocabularies/amf/validation#example-validation-error
  Message: should be equal to one of the allowed values
  Level: Violation
  Target: file://amf-client/shared/src/test/resources/org/raml/parser/examples/builtin/enums/input.raml#/declarations/types/scalar/countryBad/example/default-example
  Property: file://amf-client/shared/src/test/resources/org/raml/parser/examples/builtin/enums/input.raml#/declarations/types/scalar/countryBad/example/default-example
  Position: Some(LexicalInformation([(11,13)-(11,16)]))
  Location: file://amf-client/shared/src/test/resources/org/raml/parser/examples/builtin/enums/input.raml

- Source: http://a.ml/vocabularies/amf/validation#example-validation-error
  Message: should be equal to one of the allowed values
  Level: Violation
  Target: file://amf-client/shared/src/test/resources/org/raml/parser/examples/builtin/enums/input.raml#/declarations/types/scalar/sizesBad/example/default-example
  Property: file://amf-client/shared/src/test/resources/org/raml/parser/examples/builtin/enums/input.raml#/declarations/types/scalar/sizesBad/example/default-example
  Position: Some(LexicalInformation([(19,13)-(19,14)]))
  Location: file://amf-client/shared/src/test/resources/org/raml/parser/examples/builtin/enums/input.raml
