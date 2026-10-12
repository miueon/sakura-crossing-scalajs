// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js
import scala.scalajs.js.annotation.*

@js.native
trait NodeClass[TEventMap <: NodeClassEventMap] extends js.Object:
  var global: Boolean = js.native
  def id: Double = js.native
  def isNode: Boolean = js.native
  var name: String = js.native
  def needsUpdate_=(value: Boolean): Unit = js.native
  var nodeType: String | Null = js.native
  var parents: Boolean = js.native
  var stackTrace: String | Null = js.native
  def `type`: String = js.native
  var updateAfterType: "frame" | "none" | "object" | "render" = js.native
  var updateBeforeType: "frame" | "none" | "object" | "render" = js.native
  var updateType: "frame" | "none" | "object" | "render" = js.native
  def uuid: String = js.native
  def version: Double = js.native
  def addEventListener[T](`type`: T, listener: js.Function1[js.Object & Event[String, NodeClass[TEventMap]], Unit]): Unit = js.native
  def analyze(builder: default_b44adefb, output: js.UndefOr[NodeClass[NodeClassEventMap] & NodeElements & js.Any | Null] = js.undefined): Unit = js.native
  @JSName("before")
  protected def before$raw(node: NodeClass[NodeClassEventMap] & NodeElements & js.Any): NodeClass[TEventMap] = js.native
  def build(builder: default_b44adefb, output: js.UndefOr[String | NodeClass[NodeClassEventMap] & NodeElements & js.Any | Null] = js.undefined): String | NodeClass[NodeClassEventMap] & NodeElements & js.Any | Null = js.native
  def customCacheKey(): Double = js.native
  def deserialize(json: NodeJSONInputData): Unit = js.native
  def dispatchEvent[T](event: BaseEvent[String] & js.Any): Unit = js.native
  def dispose(): Unit = js.native
  def generate(builder: default_b44adefb, output: js.UndefOr[String | Null] = js.undefined): js.UndefOr[String | Null] = js.native
  def generateNodeType(builder: default_b44adefb, output: js.UndefOr[String] = js.undefined): String = js.native
  def getArrayCount(builder: default_b44adefb): Double | Null = js.native
  def getCacheKey(force: js.UndefOr[Boolean] = js.undefined, ignores: js.UndefOr[js.Object] = js.undefined): Double = js.native
  def getChildren(): js.Object = js.native
  def getElementType(builder: default_b44adefb): String = js.native
  def getHash(builder: default_b44adefb): String = js.native
  def getMemberType(builder: default_b44adefb, name: String): String = js.native
  def getNodeType(builder: default_b44adefb, output: js.UndefOr[String] = js.undefined): String = js.native
  @JSName("getScope")
  protected def getScope$raw(): NodeClass[TEventMap] = js.native
  def getSerializeChildren(): js.Array[NodeChild] = js.native
  def getShared(builder: default_b44adefb): NodeClass[NodeClassEventMap] & NodeElements & js.Any = js.native
  def getUpdateAfterType(): "frame" | "none" | "object" | "render" = js.native
  def getUpdateBeforeType(): "frame" | "none" | "object" | "render" = js.native
  def getUpdateType(): "frame" | "none" | "object" | "render" = js.native
  def hasEventListener[T](`type`: T, listener: js.Function1[js.Object & Event[String, NodeClass[TEventMap]], Unit]): Boolean = js.native
  def isGlobal(builder: default_b44adefb): Boolean = js.native
  @JSName("onFrameUpdate")
  protected def onFrameUpdate$raw(callback: js.ThisFunction1[this.type, default_4bde67a8, js.Any]): NodeClass[TEventMap] = js.native
  @JSName("onObjectUpdate")
  protected def onObjectUpdate$raw(callback: js.ThisFunction1[this.type, default_4bde67a8, js.Any]): NodeClass[TEventMap] = js.native
  @JSName("onReference")
  protected def onReference$raw(callback: js.ThisFunction1[this.type, default_4bde67a8 | default_b44adefb, js.Any]): NodeClass[TEventMap] = js.native
  @JSName("onRenderUpdate")
  protected def onRenderUpdate$raw(callback: js.ThisFunction1[this.type, default_4bde67a8, js.Any]): NodeClass[TEventMap] = js.native
  @JSName("onUpdate")
  protected def onUpdate$raw(callback: js.ThisFunction1[this.type, default_4bde67a8, js.Any], updateType: "frame" | "none" | "object" | "render"): NodeClass[TEventMap] = js.native
  def removeEventListener[T](`type`: T, listener: js.Function1[js.Object & Event[String, NodeClass[TEventMap]], Unit]): Unit = js.native
  def serialize(json: NodeJSONIntermediateOutputData): Unit = js.native
  def setup(builder: default_b44adefb): js.UndefOr[NodeClass[NodeClassEventMap] & NodeElements & js.Any | Null] = js.native
  def toJSON(meta: js.UndefOr[String | NodeJSONMeta] = js.undefined): NodeJSONOutputData = js.native
  def traverse(callback: js.Function1[NodeClass[NodeClassEventMap] & NodeElements & js.Any, Unit]): Unit = js.native
  def update(frame: default_4bde67a8): js.UndefOr[Boolean] = js.native
  def updateAfter(frame: default_4bde67a8): js.UndefOr[Boolean] = js.native
  def updateBefore(frame: default_4bde67a8): js.UndefOr[Boolean] = js.native
  def updateReference(state: default_4bde67a8 | default_b44adefb): facadecontract.std.JSUnknown = js.native
// #WARN: SHIM-065: Native facade trait 'NodeClass' cannot receive a builder companion: method 'addEventListener' declares method-level type parameters [T]

object NodeClass:
  extension [TEventMap <: NodeClassEventMap, Self <: NodeClass[TEventMap]](self: Self)
    inline def onUpdate(callback: js.ThisFunction1[self.type, default_4bde67a8, js.Any], updateType: "frame" | "none" | "object" | "render"): Self = self.onUpdate$raw(callback, updateType).asInstanceOf[Self]
    inline def onFrameUpdate(callback: js.ThisFunction1[self.type, default_4bde67a8, js.Any]): Self = self.onFrameUpdate$raw(callback).asInstanceOf[Self]
    inline def onRenderUpdate(callback: js.ThisFunction1[self.type, default_4bde67a8, js.Any]): Self = self.onRenderUpdate$raw(callback).asInstanceOf[Self]
    inline def onObjectUpdate(callback: js.ThisFunction1[self.type, default_4bde67a8, js.Any]): Self = self.onObjectUpdate$raw(callback).asInstanceOf[Self]
    inline def onReference(callback: js.ThisFunction1[self.type, default_4bde67a8 | default_b44adefb, js.Any]): Self = self.onReference$raw(callback).asInstanceOf[Self]
    inline def getScope(): Self = self.getScope$raw().asInstanceOf[Self]
    inline def before(node: NodeClass[NodeClassEventMap] & NodeElements & js.Any): Self = self.before$raw(node).asInstanceOf[Self]
