// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js
import scala.scalajs.js.annotation.*

@js.native
@JSImport("three", "Euler")
class Euler(x_1: js.UndefOr[Double] = js.undefined, y_1: js.UndefOr[Double] = js.undefined, z_1: js.UndefOr[Double] = js.undefined, order_1: js.UndefOr["XYZ" | "XZY" | "YXZ" | "YZX" | "ZXY" | "ZYX"] = js.undefined) extends js.Object:
  def isEuler: true = js.native
  var order: "XYZ" | "XZY" | "YXZ" | "YZX" | "ZXY" | "ZYX" = js.native
  var x: Double = js.native
  var y: Double = js.native
  var z: Double = js.native
  @JSName("_onChange")
  protected def _onChange$raw(callback: js.Function0[Unit]): Euler = js.native
  def _onChangeCallback(): Unit = js.native
  @JSName("clone")
  protected def clone$raw(): Euler = js.native
  @JSName("copy")
  protected def copy$raw(euler: Euler): Euler = js.native
  // #WARN: Euler.equals collides with the pinned platform member 'java.lang.Object#equals' only at the Scala source name; the JS wire key 'equals' is preserved with @JSName and the member is emitted under a derived collision-free name (the inherited member stays callable under Scala semantics)
  @JSName("equals")
  def equals_1(euler: Euler): Boolean = js.native
  def fromArray(array: js.Array[Double | js.UndefOr["XYZ" | "XZY" | "YXZ" | "YZX" | "ZXY" | "ZYX"]]): Euler = js.native
  @JSName(js.Symbol.iterator)
  def jsIterator(): js.Object = js.native
  def reorder(newOrder: "XYZ" | "XZY" | "YXZ" | "YZX" | "ZXY" | "ZYX"): Euler = js.native
  def set(x: Double, y: Double, z: Double, order: js.UndefOr["XYZ" | "XZY" | "YXZ" | "YZX" | "ZXY" | "ZYX"] = js.undefined): Euler = js.native
  def setFromQuaternion(q: Quaternion, order: js.UndefOr["XYZ" | "XZY" | "YXZ" | "YZX" | "ZXY" | "ZYX"] = js.undefined, update: js.UndefOr[Boolean] = js.undefined): Euler = js.native
  def setFromRotationMatrix(m: Matrix4, order: js.UndefOr["XYZ" | "XZY" | "YXZ" | "YZX" | "ZXY" | "ZYX"] = js.undefined, update: js.UndefOr[Boolean] = js.undefined): Euler = js.native
  def setFromVector3(v: Vector3, order: js.UndefOr["XYZ" | "XZY" | "YXZ" | "YZX" | "ZXY" | "ZYX"] = js.undefined): Euler = js.native
  def toArray(array: js.UndefOr[js.Array[js.UndefOr[Double] | js.UndefOr["XYZ" | "XZY" | "YXZ" | "YZX" | "ZXY" | "ZYX"]]] = js.undefined, offset: js.UndefOr[Double] = js.undefined): js.Array[Double | js.UndefOr["XYZ" | "XZY" | "YXZ" | "YZX" | "ZXY" | "ZYX"]] = js.native


object Euler:
  extension [Self <: Euler](self: Self)
    inline def clone(): Self = self.clone$raw().asInstanceOf[Self]
    inline def copy(euler: Euler): Self = self.copy$raw(euler).asInstanceOf[Self]
    inline def _onChange(callback: js.Function0[Unit]): Self = self._onChange$raw(callback).asInstanceOf[Self]

  // #WARN: static 'DEFAULT_ORDER' is declared read-write upstream; the member-level native publication cannot express a JS setter, so its write direction is withheld
  @js.native
  @JSImport("three", "Euler.DEFAULT_ORDER")
  def DEFAULT_ORDER: "XYZ" = js.native
