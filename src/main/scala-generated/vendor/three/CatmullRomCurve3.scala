// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js
import scala.scalajs.js.annotation.*

@js.native
@JSImport("three", "CatmullRomCurve3")
class CatmullRomCurve3(points_1: js.UndefOr[js.Array[Vector3]] = js.undefined, closed_1: js.UndefOr[Boolean] = js.undefined, curveType_1: js.UndefOr["catmullrom" | "centripetal" | "chordal"] = js.undefined, tension_1: js.UndefOr[Double] = js.undefined) extends Curve[Vector3]:
  var closed: Boolean = js.native
  var curveType: "catmullrom" | "centripetal" | "chordal" = js.native
  def isCatmullRomCurve3: true = js.native
  var points: js.Array[Vector3] = js.native
  var tension: Double = js.native
  override def `type`: String = js.native
