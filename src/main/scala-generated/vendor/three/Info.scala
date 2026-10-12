// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait Info extends js.Object:
  var autoReset: Boolean = js.native
  def calls: Double = js.native
  def compute: InfoCompute = js.native
  def frame: Double = js.native
  def memory: InfoMemory = js.native
  def render: InfoRender = js.native
  def createAttribute(attribute: BufferAttribute[BufferAttributeEventMap]): Unit = js.native
  def createIndexAttribute(attribute: BufferAttribute[BufferAttributeEventMap]): Unit = js.native
  def createIndirectStorageAttribute(attribute: BufferAttribute[BufferAttributeEventMap]): Unit = js.native
  def createReadbackBuffer(readbackBuffer: ReadbackBuffer[ReadbackBufferEventMap]): Unit = js.native
  def createStorageAttribute(attribute: BufferAttribute[BufferAttributeEventMap]): Unit = js.native
  def createTexture(texture: Texture[js.Any, TextureEventMap]): Unit = js.native
  def destroyAttribute(attribute: BufferAttribute[BufferAttributeEventMap]): Unit = js.native
  def destroyReadbackBuffer(readbackBuffer: ReadbackBuffer[ReadbackBufferEventMap]): Unit = js.native
  def destroyTexture(texture: Texture[js.Any, TextureEventMap]): Unit = js.native
  def dispose(): Unit = js.native
  def reset(): Unit = js.native
  def update(`object`: Object3D[Object3DEventMap], count: Double, instanceCount: Double): Unit = js.native

object Info:
  def of(
    autoReset: Boolean,
    calls: Double,
    compute: InfoCompute,
    createAttribute: js.Function1[BufferAttribute[BufferAttributeEventMap], Unit],
    createIndexAttribute: js.Function1[BufferAttribute[BufferAttributeEventMap], Unit],
    createIndirectStorageAttribute: js.Function1[BufferAttribute[BufferAttributeEventMap], Unit],
    createReadbackBuffer: js.Function1[ReadbackBuffer[ReadbackBufferEventMap], Unit],
    createStorageAttribute: js.Function1[BufferAttribute[BufferAttributeEventMap], Unit],
    createTexture: js.Function1[Texture[js.Any, TextureEventMap], Unit],
    destroyAttribute: js.Function1[BufferAttribute[BufferAttributeEventMap], Unit],
    destroyReadbackBuffer: js.Function1[ReadbackBuffer[ReadbackBufferEventMap], Unit],
    destroyTexture: js.Function1[Texture[js.Any, TextureEventMap], Unit],
    dispose: js.Function0[Unit],
    frame: Double,
    memory: InfoMemory,
    render: InfoRender,
    reset: js.Function0[Unit],
    update: js.Function3[Object3D[Object3DEventMap], Double, Double, Unit]
  ): Info =
    // #COMPLETION_DRIVE: sanctioned construction boundary for native facade trait
    // #SUGGEST_VERIFY: runtime shape test verifies dictionary keys and receiver round-trip
    js.Dynamic.literal(
      "autoReset" -> autoReset.asInstanceOf[js.Any],
      "calls" -> calls.asInstanceOf[js.Any],
      "compute" -> compute.asInstanceOf[js.Any],
      "createAttribute" -> createAttribute.asInstanceOf[js.Any],
      "createIndexAttribute" -> createIndexAttribute.asInstanceOf[js.Any],
      "createIndirectStorageAttribute" -> createIndirectStorageAttribute.asInstanceOf[js.Any],
      "createReadbackBuffer" -> createReadbackBuffer.asInstanceOf[js.Any],
      "createStorageAttribute" -> createStorageAttribute.asInstanceOf[js.Any],
      "createTexture" -> createTexture.asInstanceOf[js.Any],
      "destroyAttribute" -> destroyAttribute.asInstanceOf[js.Any],
      "destroyReadbackBuffer" -> destroyReadbackBuffer.asInstanceOf[js.Any],
      "destroyTexture" -> destroyTexture.asInstanceOf[js.Any],
      "dispose" -> dispose.asInstanceOf[js.Any],
      "frame" -> frame.asInstanceOf[js.Any],
      "memory" -> memory.asInstanceOf[js.Any],
      "render" -> render.asInstanceOf[js.Any],
      "reset" -> reset.asInstanceOf[js.Any],
      "update" -> update.asInstanceOf[js.Any]
    ).asInstanceOf[Info]
