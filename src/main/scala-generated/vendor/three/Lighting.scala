// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait Lighting extends js.Object:
  var enabled: Boolean = js.native
  def beginRender(scene: Scene[Object3DEventMap]): Unit = js.native
  def createNode(lights: js.UndefOr[js.Array[Light]] = js.undefined): LightsNode = js.native
  def finishRender(scene: Scene[Object3DEventMap]): Unit = js.native
  def getNode(scene: Scene[Object3DEventMap]): LightsNode = js.native

object Lighting:
  def of(
    beginRender: js.Function1[Scene[Object3DEventMap], Unit],
    createNode: js.Function1[js.UndefOr[js.Array[Light]], LightsNode],
    enabled: Boolean,
    finishRender: js.Function1[Scene[Object3DEventMap], Unit],
    getNode: js.Function1[Scene[Object3DEventMap], LightsNode]
  ): Lighting =
    // #COMPLETION_DRIVE: sanctioned construction boundary for native facade trait
    // #SUGGEST_VERIFY: runtime shape test verifies dictionary keys and receiver round-trip
    js.Dynamic.literal(
      "beginRender" -> beginRender.asInstanceOf[js.Any],
      "createNode" -> createNode.asInstanceOf[js.Any],
      "enabled" -> enabled.asInstanceOf[js.Any],
      "finishRender" -> finishRender.asInstanceOf[js.Any],
      "getNode" -> getNode.asInstanceOf[js.Any]
    ).asInstanceOf[Lighting]
