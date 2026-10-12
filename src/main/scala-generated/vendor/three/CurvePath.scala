// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js
import scala.scalajs.js.annotation.*

@js.native
@JSImport("three", "CurvePath")
class CurvePath[TVector <: Vector2 | Vector3]() extends Curve[TVector]:
  var autoClose: Boolean = js.native
  var curves: js.Array[Curve[TVector]] = js.native
  override def `type`: String = js.native
  def add(curve: Curve[TVector]): Unit = js.native
  @JSName("closePath")
  protected def closePath$raw(): CurvePath[TVector] = js.native
  @JSName("fromJSON")
  protected override def fromJSON$raw(json: CurveJSON): CurvePath[TVector] = js.native
  def getCurveLengths(): js.Array[Double] = js.native
  override def getPoint(t: Double, optionalTarget: js.UndefOr[TVector] = js.undefined): TVector = js.native
  override def getPoints(divisions: js.UndefOr[Double] = js.undefined): js.Array[TVector] = js.native
  override def getSpacedPoints(divisions: js.UndefOr[Double] = js.undefined): js.Array[TVector] = js.native
  override def toJSON(): CurvePathJSON = js.native

object CurvePath:
  extension [TVector <: Vector2 | Vector3, Self <: CurvePath[TVector]](self: Self)
    inline def closePath(): Self = self.closePath$raw().asInstanceOf[Self]
    inline def fromJSON(json: CurvePathJSON): Self = self.fromJSON$raw(json).asInstanceOf[Self]
