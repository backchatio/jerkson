package com.codahale.jerkson

import com.fasterxml.jackson.core.{JsonGenerator, JsonParser => JacksonParser}
import com.fasterxml.jackson.databind.{DeserializationFeature, MappingJsonFactory, ObjectMapper}

object Json extends Json

trait Json extends Parser with Generator {
  protected val classLoader = Thread.currentThread().getContextClassLoader

  protected val mapper = new ObjectMapper
  mapper.registerModule(new ScalaModule(classLoader))


  protected val factory = new MappingJsonFactory(mapper)
  factory.enable(JsonGenerator.Feature.AUTO_CLOSE_JSON_CONTENT)
  factory.enable(JsonGenerator.Feature.AUTO_CLOSE_TARGET)
  factory.enable(JsonGenerator.Feature.QUOTE_FIELD_NAMES)
  factory.enable(JacksonParser.Feature.ALLOW_COMMENTS)
  factory.enable(JacksonParser.Feature.AUTO_CLOSE_SOURCE)


  type JValue     = AST.JValue
  val  JValue     = AST.JValue
  val  JUndefined = AST.JUndefined
  val  JNull      = AST.JNull
  type JString    = AST.JString
  val  JString    = AST.JString
  type JFloat     = AST.JFloat
  val  JFloat     = AST.JFloat
  type JDecimal   = AST.JDecimal
  val  JDecimal   = AST.JDecimal
  type JInt       = AST.JInt
  val  JInt       = AST.JInt
  type JBoolean   = AST.JBoolean
  val  JBoolean   = AST.JBoolean
  type JField     = AST.JField
  val  JField     = AST.JField
  type JObject    = AST.JObject
  val  JObject    = AST.JObject
  type JArray     = AST.JArray
  val  JArray     = AST.JArray
  type JNumber    = AST.JNumber

}
