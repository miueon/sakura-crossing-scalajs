// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js
import scala.scalajs.js.annotation.*

@js.native
@JSImport("three", "Curve")
abstract class Curve[TVector <: Vector2 | Vector3]() extends js.Object:
  var arcLengthDivisions: Double = js.native
  def `type`: String = js.native
  @JSName("clone")
  protected def clone$raw(): Curve[TVector] = js.native
  def computeFrenetFrames(segments: Double, closed: js.UndefOr[Boolean] = js.undefined): ArcCurveComputeFrenetFramesReturn = js.native
  @JSName("copy")
  protected def copy$raw(source: Curve[TVector]): Curve[TVector] = js.native
  @JSName("fromJSON")
  protected def fromJSON$raw(json: CurveJSON): Curve[TVector] = js.native
  def getLength(): Double = js.native
  def getLengths(divisions: js.UndefOr[Double] = js.undefined): js.Array[Double] = js.native
  def getPoint(t: Double, optionalTarget: js.UndefOr[TVector] = js.undefined): TVector = js.native
  def getPointAt(u: Double, optionalTarget: js.UndefOr[TVector] = js.undefined): TVector = js.native
  def getPoints(divisions: js.UndefOr[Double] = js.undefined): js.Array[TVector] = js.native
  def getSpacedPoints(divisions: js.UndefOr[Double] = js.undefined): js.Array[TVector] = js.native
  def getTangent(t: Double, optionalTarget: js.UndefOr[TVector] = js.undefined): TVector = js.native
  def getTangentAt(u: Double, optionalTarget: js.UndefOr[TVector] = js.undefined): TVector = js.native
  def getUtoTmapping(u: Double, distance: Double): Double = js.native
  def toJSON(): CurveJSON = js.native
  def updateArcLengths(): Unit = js.native

object Curve:
  extension [TVector <: Vector2 | Vector3, Self <: Curve[TVector]](self: Self)
    inline def clone(): Self = self.clone$raw().asInstanceOf[Self]
    inline def copy(source: Curve[TVector]): Self = self.copy$raw(source).asInstanceOf[Self]
    inline def fromJSON(json: CurveJSON): Self = self.fromJSON$raw(json).asInstanceOf[Self]
