Model: file://amf-client/shared/src/test/resources/validations/jsonschema/not/api4.raml
Profile: RAML
Conforms? false
Number of results: 1

Level: Violation

- Source: http://a.ml/vocabularies/amf/parser#exampleError
  Message: {"keyword":"not","dataPath":".foo","schemaPath":"#/properties/foo/not","params":{},"message":"should NOT be valid"}
  Level: Violation
  Target: file://amf-client/shared/src/test/resources/validations/jsonschema/not/api4.raml#/web-api/end-points/%2Fep1/get/200/application%2Fjson/schema/example/default-example
  Property: 
  Position: Some(LexicalInformation([(22,0)-(25,0)]))
  Location: 