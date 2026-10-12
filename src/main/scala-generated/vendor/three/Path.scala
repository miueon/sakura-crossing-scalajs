// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js
import scala.scalajs.js.annotation.*

@js.native
@JSImport("three", "Path")
class Path(points: js.UndefOr[js.Array[Vector2]] = js.undefined) extends CurvePath[Vector2]:
  var currentPoint: Vector2 = js.native
  override def `type`: String = js.native
  @JSName("absarc")
  protected def absarc$raw(aX: Double, aY: Double, aRadius: Double, aStartAngle: Double, aEndAngle: Double, aClockwise: js.UndefOr[Boolean] = js.undefined): Path = js.native
  @JSName("absellipse")
  protected def absellipse$raw(aX: Double, aY: Double, xRadius: Double, yRadius: Double, aStartAngle: Double, aEndAngle: Double, aClockwise: js.UndefOr[Boolean] = js.undefined, aRotation: js.UndefOr[Double] = js.undefined): Path = js.native
  @JSName("arc")
  protected def arc$raw(aX: Double, aY: Double, aRadius: Double, aStartAngle: Double, aEndAngle: Double, aClockwise: js.UndefOr[Boolean] = js.undefined): Path = js.native
  @JSName("bezierCurveTo")
  protected def bezierCurveTo$raw(aCP1x: Double, aCP1y: Double, aCP2x: Double, aCP2y: Double, aX: Double, aY: Double): Path = js.native
  @JSName("ellipse")
  protected def ellipse$raw(aX: Double, aY: Double, xRadius: Double, yRadius: Double, aStartAngle: Double, aEndAngle: Double, aClockwise: js.UndefOr[Boolean] = js.undefined, aRotation: js.UndefOr[Double] = js.undefined): Path = js.native
  @JSName("fromJSON")
  protected override def fromJSON$raw(json: CurveJSON): Path = js.native
  @JSName("lineTo")
  protected def lineTo$raw(x: Double, y: Double): Path = js.native
  @JSName("moveTo")
  protected def moveTo$raw(x: Double, y: Double): Path = js.native
  @JSName("quadraticCurveTo")
  protected def quadraticCurveTo$raw(aCPx: Double, aCPy: Double, aX: Double, aY: Double): Path = js.native
  @JSName("setFromPoints")
  protected def setFromPoints$raw(vectors: js.Array[Vector2]): Path = js.native
  @JSName("splineThru")
  protected def splineThru$raw(pts: js.Array[Vector2]): Path = js.native

object Path:
  extension [Self <: Path](self: Self)
    inline def absarc(aX: Double, aY: Double, aRadius: Double, aStartAngle: Double, aEndAngle: Double, aClockwise: js.UndefOr[Boolean] = js.undefined): Self = self.absarc$raw(aX, aY, aRadius, aStartAngle, aEndAngle, aClockwise).asInstanceOf[Self]
    inline def absellipse(aX: Double, aY: Double, xRadius: Double, yRadius: Double, aStartAngle: Double, aEndAngle: Double, aClockwise: js.UndefOr[Boolean] = js.undefined, aRotation: js.UndefOr[Double] = js.undefined): Self = self.absellipse$raw(aX, aY, xRadius, yRadius, aStartAngle, aEndAngle, aClockwise, aRotation).asInstanceOf[Self]
    inline def arc(aX: Double, aY: Double, aRadius: Double, aStartAngle: Double, aEndAngle: Double, aClockwise: js.UndefOr[Boolean] = js.undefined): Self = self.arc$raw(aX, aY, aRadius, aStartAngle, aEndAngle, aClockwise).asInstanceOf[Self]
    inline def bezierCurveTo(aCP1x: Double, aCP1y: Double, aCP2x: Double, aCP2y: Double, aX: Double, aY: Double): Self = self.bezierCurveTo$raw(aCP1x, aCP1y, aCP2x, aCP2y, aX, aY).asInstanceOf[Self]
    inline def ellipse(aX: Double, aY: Double, xRadius: Double, yRadius: Double, aStartAngle: Double, aEndAngle: Double, aClockwise: js.UndefOr[Boolean] = js.undefined, aRotation: js.UndefOr[Double] = js.undefined): Self = self.ellipse$raw(aX, aY, xRadius, yRadius, aStartAngle, aEndAngle, aClockwise, aRotation).asInstanceOf[Self]
    inline def lineTo(x: Double, y: Double): Self = self.lineTo$raw(x, y).asInstanceOf[Self]
    inline def moveTo(x: Double, y: Double): Self = self.moveTo$raw(x, y).asInstanceOf[Self]
    inline def quadraticCurveTo(aCPx: Double, aCPy: Double, aX: Double, aY: Double): Self = self.quadraticCurveTo$raw(aCPx, aCPy, aX, aY).asInstanceOf[Self]
    inline def setFromPoints(vectors: js.Array[Vector2]): Self = self.setFromPoints$raw(vectors).asInstanceOf[Self]
    inline def splineThru(pts: js.Array[Vector2]): Self = self.splineThru$raw(pts).asInstanceOf[Self]
    inline def fromJSON(json: PathJSON): Self = self.fromJSON$raw(json).asInstanceOf[Self]
