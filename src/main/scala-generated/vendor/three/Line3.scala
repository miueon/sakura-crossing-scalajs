// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js
import scala.scalajs.js.annotation.*

@js.native
@JSImport("three", "Line3")
class Line3(start_1: js.UndefOr[Vector3] = js.undefined, end_1: js.UndefOr[Vector3] = js.undefined) extends js.Object:
  var end: Vector3 = js.native
  var start: Vector3 = js.native
  def applyMatrix4(matrix: Matrix4): Line3 = js.native
  def at(t: Double, target: Vector3): Vector3 = js.native
  @JSName("clone")
  protected def clone$raw(): Line3 = js.native
  def closestPointToPoint(point: Vector3, clampToLine: Boolean, target: Vector3): Vector3 = js.native
  def closestPointToPointParameter(point: Vector3, clampToLine: js.UndefOr[Boolean] = js.undefined): Double = js.native
  @JSName("copy")
  protected def copy$raw(line: Line3): Line3 = js.native
  def delta(target: Vector3): Vector3 = js.native
  def distance(): Double = js.native
  def distanceSq(): Double = js.native
  def distanceSqToLine3(line: Line3, c1: js.UndefOr[Vector3] = js.undefined, c2: js.UndefOr[Vector3] = js.undefined): Double = js.native
  // #WARN: Line3.equals collides with the pinned platform member 'java.lang.Object#equals' only at the Scala source name; the JS wire key 'equals' is preserved with @JSName and the member is emitted under a derived collision-free name (the inherited member stays callable under Scala semantics)
  @JSName("equals")
  def equals_1(line: Line3): Boolean = js.native
  def getCenter(target: Vector3): Vector3 = js.native
  def set(start: js.UndefOr[Vector3] = js.undefined, end: js.UndefOr[Vector3] = js.undefined): Line3 = js.native

object Line3:
  extension [Self <: Line3](self: Self)
    inline def clone(): Self = self.clone$raw().asInstanceOf[Self]
    inline def copy(line: Line3): Self = self.copy$raw(line).asInstanceOf[Self]
