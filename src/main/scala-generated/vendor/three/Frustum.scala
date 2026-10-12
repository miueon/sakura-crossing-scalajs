// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js
import scala.scalajs.js.annotation.*

@js.native
@JSImport("three", "Frustum")
class Frustum(p0: js.UndefOr[Plane] = js.undefined, p1: js.UndefOr[Plane] = js.undefined, p2: js.UndefOr[Plane] = js.undefined, p3: js.UndefOr[Plane] = js.undefined, p4: js.UndefOr[Plane] = js.undefined, p5: js.UndefOr[Plane] = js.undefined) extends js.Object:
  var planes: js.Array[Plane] = js.native
  @JSName("clone")
  protected def clone$raw(): Frustum = js.native
  def containsPoint(point: Vector3): Boolean = js.native
  @JSName("copy")
  protected def copy$raw(frustum: Frustum): Frustum = js.native
  def intersectsBox(box: Box3): Boolean = js.native
  def intersectsObject(`object`: Object3D[Object3DEventMap]): Boolean = js.native
  def intersectsSphere(sphere: Sphere): Boolean = js.native
  def intersectsSprite(sprite: Sprite[Object3DEventMap]): Boolean = js.native
  def set(p0: Plane, p1: Plane, p2: Plane, p3: Plane, p4: Plane, p5: Plane): Frustum = js.native
  @JSName("setFromProjectionMatrix")
  protected def setFromProjectionMatrix$raw(m: Matrix4, coordinateSystem: js.UndefOr[2000 | 2001] = js.undefined, reversedDepth: js.UndefOr[Boolean] = js.undefined): Frustum = js.native

object Frustum:
  extension [Self <: Frustum](self: Self)
    inline def clone(): Self = self.clone$raw().asInstanceOf[Self]
    inline def copy(frustum: Frustum): Self = self.copy$raw(frustum).asInstanceOf[Self]
    inline def setFromProjectionMatrix(m: Matrix4, coordinateSystem: js.UndefOr[2000 | 2001] = js.undefined, reversedDepth: js.UndefOr[Boolean] = js.undefined): Self = self.setFromProjectionMatrix$raw(m, coordinateSystem, reversedDepth).asInstanceOf[Self]
