// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait default_4bde67a8 extends js.Object:
  var camera: Camera | Null = js.native
  var deltaTime: Double = js.native
  var frameBeforeMap: js.Object = js.native
  var frameId: Double = js.native
  var frameMap: js.Object = js.native
  var material: Material[MaterialEventMap] | Null = js.native
  var `object`: Object3D[Object3DEventMap] | Null = js.native
  var renderBeforeMap: js.Object = js.native
  var renderer: Renderer | Null = js.native
  var renderId: Double = js.native
  var renderMap: js.Object = js.native
  var scene: Scene[Object3DEventMap] | Null = js.native
  var startTime: Double | Null = js.native
  var time: Double = js.native
  def update(): Unit = js.native
  def updateBeforeNode(node: NodeClass[NodeClassEventMap] & NodeElements & js.Any): Unit = js.native
  def updateNode(node: NodeClass[NodeClassEventMap] & NodeElements & js.Any): Unit = js.native

object default_4bde67a8:
  def of(
    camera: Camera | Null,
    deltaTime: Double,
    frameBeforeMap: js.Object,
    frameId: Double,
    frameMap: js.Object,
    material: Material[MaterialEventMap] | Null,
    `object`: Object3D[Object3DEventMap] | Null,
    renderBeforeMap: js.Object,
    renderer: Renderer | Null,
    renderId: Double,
    renderMap: js.Object,
    scene: Scene[Object3DEventMap] | Null,
    startTime: Double | Null,
    time: Double,
    update: js.Function0[Unit],
    updateBeforeNode: js.Function1[NodeClass[NodeClassEventMap] & NodeElements & js.Any, Unit],
    updateNode: js.Function1[NodeClass[NodeClassEventMap] & NodeElements & js.Any, Unit]
  ): default_4bde67a8 =
    // #COMPLETION_DRIVE: sanctioned construction boundary for native facade trait
    // #SUGGEST_VERIFY: runtime shape test verifies dictionary keys and receiver round-trip
    js.Dynamic.literal(
      "camera" -> camera.asInstanceOf[js.Any],
      "deltaTime" -> deltaTime.asInstanceOf[js.Any],
      "frameBeforeMap" -> frameBeforeMap.asInstanceOf[js.Any],
      "frameId" -> frameId.asInstanceOf[js.Any],
      "frameMap" -> frameMap.asInstanceOf[js.Any],
      "material" -> material.asInstanceOf[js.Any],
      "object" -> `object`.asInstanceOf[js.Any],
      "renderBeforeMap" -> renderBeforeMap.asInstanceOf[js.Any],
      "renderer" -> renderer.asInstanceOf[js.Any],
      "renderId" -> renderId.asInstanceOf[js.Any],
      "renderMap" -> renderMap.asInstanceOf[js.Any],
      "scene" -> scene.asInstanceOf[js.Any],
      "startTime" -> startTime.asInstanceOf[js.Any],
      "time" -> time.asInstanceOf[js.Any],
      "update" -> update.asInstanceOf[js.Any],
      "updateBeforeNode" -> updateBeforeNode.asInstanceOf[js.Any],
      "updateNode" -> updateNode.asInstanceOf[js.Any]
    ).asInstanceOf[default_4bde67a8]
