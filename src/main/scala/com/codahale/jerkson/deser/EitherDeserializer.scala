package com.codahale.jerkson.deser

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.node.TreeTraversingParser
import com.fasterxml.jackson.databind._

class EitherDeserializer(config: DeserializationConfig,
                         javaType: JavaType) extends JsonDeserializer[Object] {
  def deserialize(jp: JsonParser, ctxt: DeserializationContext) = {
    val node = jp.readValueAsTree[JsonNode]
    val tp = new TreeTraversingParser(node, jp.getCodec)

    try {
      Left(tp.getCodec.readValue[Object](tp, javaType.containedType(0)))
    } catch {
      case _ => {
        // We don't want to reuse the same parser that was used in the
        // try-block, as the read there may have used nextToken() and advanced
        // us past the point where we expect to be.
        val tpRight = new TreeTraversingParser(node, jp.getCodec)
        Right(tpRight.getCodec.readValue[Object](tpRight, javaType.containedType(1)))
      }
    }
  }

  override def isCachable = true
}
