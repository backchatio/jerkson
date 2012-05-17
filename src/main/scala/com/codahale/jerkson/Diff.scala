package com.codahale.jerkson

import AST._
/** A difference between two JSONs (j1 diff j2).
 * @param changed what has changed from j1 to j2
 * @param added what has been added to j2
 * @param deleted what has been deleted from j1
 */
case class Diff(changed: JValue, added: JValue, deleted: JValue) {
  def map(f: JValue => JValue): Diff = {
    def applyTo(x: JValue) = x match {
      case JUndefined => JUndefined
      case _ => f(x)
    }
    Diff(applyTo(changed), applyTo(added), applyTo(deleted))
  }
}

/** Computes a diff between two JSONs.
 */
object Diff {
  /** Return a diff.
   * <p>
   * Example:<pre>
   * val Diff(c, a, d) = ("name", "joe") ~ ("age", 10) diff ("fname", "joe") ~ ("age", 11)
   * c = JObject(JField("age",JInt(11)) :: Nil)
   * a = JObject(JField("fname",JString("joe")) :: Nil)
   * d = JObject(JField("name",JString("joe")) :: Nil)
   * </pre>
   */
  def diff(val1: JValue, val2: JValue): Diff = (val1, val2) match {
    case (x, y) if x == y => Diff(JUndefined, JUndefined, JUndefined)
    case (JObject(xs), JObject(ys)) => diffFields(xs, ys)
    case (JArray(xs), JArray(ys)) => diffVals(xs, ys)
    case (JField(xn, xv), JField(yn, yv)) if (xn == yn) => diff(xv, yv) map (JField(xn, _))
    case (x @ JField(xn, xv), y @ JField(yn, yv)) if (xn != yn) => Diff(JUndefined, y, x)
    case (JInt(x), JInt(y)) if (x != y) => Diff(JInt(y), JUndefined, JUndefined)
    case (JFloat(x), JFloat(y)) if (x != y) => Diff(JFloat(y), JUndefined, JUndefined)
    case (JDecimal(x), JDecimal(y)) if (x != y) => Diff(JDecimal(y), JUndefined, JUndefined)
    case (JString(x), JString(y)) if (x != y) => Diff(JString(y), JUndefined, JUndefined)
    case (JBoolean(x), JBoolean(y)) if (x != y) => Diff(JBoolean(y), JUndefined, JUndefined)
    case (x, y) => Diff(JUndefined, y, x)
  }

  private def diffFields(vs1: List[JField], vs2: List[JField]) = {
    def diffRec(xleft: List[JField], yleft: List[JField]): Diff = xleft match {
      case Nil => Diff(JUndefined, if (yleft.isEmpty) JUndefined else JObject(yleft), JUndefined)
      case x :: xs => yleft find (_.name == x.name) match {
        case Some(y) =>
          val Diff(c1, a1, d1) = diff(x, y)
          val Diff(c2, a2, d2) = diffRec(xs, yleft filterNot (_ == y))
          Diff(c1 ++ c2, a1 ++ a2, d1 ++ d2) map {
            case f: JField => JObject(f :: Nil)
            case x => x
          }
        case None =>
          val Diff(c, a, d) = diffRec(xs, yleft)
          Diff(c, a, JObject(x :: Nil) merge d)
      }
    }

    diffRec(vs1, vs2)
  }

  private def diffVals(vs1: List[JValue], vs2: List[JValue]) = {
    def diffRec(xleft: List[JValue], yleft: List[JValue]): Diff = (xleft, yleft) match {
      case (xs, Nil) => Diff(JUndefined, JUndefined, if (xs.isEmpty) JUndefined else JArray(xs))
      case (Nil, ys) => Diff(JUndefined, if (ys.isEmpty) JUndefined else JArray(ys), JUndefined)
      case (x :: xs, y :: ys) =>
        val Diff(c1, a1, d1) = diff(x, y)
        val Diff(c2, a2, d2) = diffRec(xs, ys)
        Diff(c1 ++ c2, a1 ++ a2, d1 ++ d2)
    }

    diffRec(vs1, vs2)
  }

  private[jerkson] trait Diffable { this: JValue =>
    /** Return a diff.
     * @see com.codahale.jerkson.Diff#diff
     */
    def diff(other: JValue) = Diff.diff(this, other)
  }
}