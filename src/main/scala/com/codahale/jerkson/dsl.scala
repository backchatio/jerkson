package com.codahale.jerkson

import AST._

/** A DSL to produce valid JSON.
 * Example:<pre>
 * import com.codahale.jerkson.DSL.WithJFloat._
 * ("name", "joe") ~ ("age", 15) == JObject(JField("name",JString("joe")) :: JField("age",JInt(15)) :: Nil)
 *
 * import com.codahale.jerkson.DSL.WithJDecimal._
 * ("name", "joe") ~ ("age", 15) == JObject(JField("name",JString("joe")) :: JField("age",JInt(15)) :: Nil)
 * </pre>
 */
object DSL {

  /** Basic implicit conversions from primitive types into JSON.
   * Example:<pre>
   * import com.codahale.jerkson.DSL.JFloatImplicits._
   * JObject(JField("name", "joe") :: Nil) == JObject(JField("name", JString("joe")) :: Nil)
   *
   * import com.codahale.jerkson.DSL.JDecimalImplicits._
   * JObject(JField("name", "joe") :: Nil) == JObject(JField("name", JString("joe")) :: Nil)
   * </pre>
   */
  object JFloatImplicits extends Implicits with DoubleModeImplicits
  object JDecimalImplicits extends Implicits with BigDecimalModeImplicits

  trait DoubleModeImplicits {
    implicit def double2jvalue(x: Double) = JFloat(x)
    implicit def float2jvalue(x: Float) = JFloat(x)
    implicit def bigdecimal2jvalue(x: BigDecimal) = JFloat(x.doubleValue)

  }

  trait BigDecimalModeImplicits {
    implicit def double2jvalue(x: Double) = JDecimal(BigDecimal(x))
    implicit def float2jvalue(x: Float) = JDecimal(BigDecimal(x.toDouble))
    implicit def bigdecimal2jvalue(x: BigDecimal) = JDecimal(x)

  }
  trait Implicits {
    implicit def int2jvalue(x: Int) = JInt(x)
    implicit def long2jvalue(x: Long) = JInt(x)
    implicit def bigint2jvalue(x: BigInt) = JInt(x)
    implicit def boolean2jvalue(x: Boolean) = JBoolean(x)
    implicit def string2jvalue(x: String) = JString(x)
    implicit def double2jvalue(x: Double): JValue
      implicit def float2jvalue(x: Float): JValue
      implicit def bigdecimal2jvalue(x: BigDecimal): JValue
  }

  trait DSL extends Implicits {
    implicit def seq2jvalue[A <% JValue](s: Traversable[A]) =
      JArray(s.toList.map { a => val v: JValue = a; v })

    implicit def map2jvalue[A <% JValue](m: Map[String, A]) =
      JObject(m.toList.map { case (k, v) => JField(k, v) })

    implicit def option2jvalue[A <% JValue](opt: Option[A]): JValue = opt match {
      case Some(x) => x
      case None => JUndefined
    }

    implicit def symbol2jvalue(x: Symbol) = JString(x.name)
    implicit def pair2jvalue[A <% JValue](t: (String, A)) = JObject(List(JField(t._1, t._2)))
    implicit def list2jvalue(l: List[JField]) = JObject(l)
    implicit def jobject2assoc(o: JObject) = new JsonListAssoc(o.fields)
    implicit def pair2Assoc[A <% JValue](t: (String, A)) = new JsonAssoc(t)

    class JsonAssoc[A <% JValue](left: (String, A)) {
      def ~[B <% JValue](right: (String, B)) = {
        val l: JValue = left._2
        val r: JValue = right._2
        JObject(JField(left._1, l) :: JField(right._1, r) :: Nil)
      }

      def ~(right: JObject) = {
        val l: JValue = left._2
        JObject(JField(left._1, l) :: right.fields)
      }
    }

    class JsonListAssoc(left: List[JField]) {
      def ~(right: (String, JValue)) = JObject(left ::: List(JField(right._1, right._2)))
      def ~(right: JObject) = JObject(left ::: right.fields)
    }
  }

  trait WithJDecimal extends DSL with BigDecimalModeImplicits
  /** A DSL to produce valid JSON.
   * Example:<pre>
   * import com.codahale.jerkson.DSL.WithJDecimal._
   * ("name", "joe") ~ ("age", 15) == JObject(JField("name",JString("joe")) :: JField("age",JInt(15)) :: Nil)
   * </pre>
   */
  object WithJDecimal extends WithJDecimal
  trait WithJFloat extends DSL with DoubleModeImplicits
  /** A DSL to produce valid JSON.
   * Example:<pre>
   * import com.codahale.jerkson.DSL.WithJFloat._
   * ("name", "joe") ~ ("age", 15) == JObject(JField("name",JString("joe")) :: JField("age",JInt(15)) :: Nil)
   * </pre>
   */
  object WithJFloat extends WithJFloat
}
