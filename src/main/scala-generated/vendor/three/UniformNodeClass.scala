// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js
import scala.scalajs.js.annotation.*

// #WARN: Disambiguated erasure-colliding overload 'select' with @targetName("select_1") and @JSName("select")
@js.native
trait UniformNodeClass[TValue] extends js.Object:
  var global: Boolean = js.native
  var groupNode: default_6eddee1e = js.native
  def id: Double = js.native
  var isInputNode: true = js.native
  def isNode: Boolean = js.native
  def isUniformNode: Boolean = js.native
  var name: String = js.native
  def needsUpdate_=(value: Boolean): Unit = js.native
  var nodeType: String | Null = js.native
  var parents: Boolean = js.native
  var precision: "high" | "low" | "medium" | Null = js.native
  var stackTrace: String | Null = js.native
  def `type`: String = js.native
  var updateAfterType: "frame" | "none" | "object" | "render" = js.native
  var updateBeforeType: "frame" | "none" | "object" | "render" = js.native
  var updateType: "frame" | "none" | "object" | "render" = js.native
  def uuid: String = js.native
  var value: TValue = js.native
  def version: Double = js.native
  def addEventListener[T](`type`: T, listener: js.Function1[js.Any & Event[String, UniformNodeClass[TValue]], Unit]): Unit = js.native
  def analyze(builder: default_b44adefb, output: js.UndefOr[NodeClass[NodeClassEventMap] & NodeElements & js.Any | Null] = js.undefined): Unit = js.native
  def append(): NodeClass[NodeClassEventMap] & NodeElements & js.Any = js.native
  def assign(sourceNode: Double | NodeClass[NodeClassEventMap] & NodeElements & js.Any): UniformNodeClass[TValue] = js.native
  @JSName("before")
  protected def before$raw(node: NodeClass[NodeClassEventMap] & NodeElements & js.Any): UniformNodeClass[TValue] = js.native
  def build(builder: default_b44adefb, output: js.UndefOr[String | NodeClass[NodeClassEventMap] & NodeElements & js.Any | Null] = js.undefined): String | NodeClass[NodeClassEventMap] & NodeElements & js.Any | Null = js.native
  def bypass(callNode: NodeClass[NodeClassEventMap] & NodeElements & js.Any): default = js.native
  def cache(cache: js.UndefOr[NodeCache] = js.undefined): IsolateNode = js.native
  def colorSpaceToWorking(sourceColorSpace: String): default_24971b86 = js.native
  def compute(count: Double, workgroupSize: js.UndefOr[js.Array[Double]] = js.undefined): default_5e5b9ffc = js.native
  def computeKernel(workgroupSize: js.UndefOr[js.Array[Double]] = js.undefined): default_5e5b9ffc = js.native
  def convert(types: String): NodeClass[NodeClassEventMap] & NodeElements & js.Any = js.native
  def customCacheKey(): Double = js.native
  def debug(callback: js.UndefOr[js.Function2[default_b44adefb, String, Unit] | Null] = js.undefined): DebugNode = js.native
  def deserialize(json: NodeJSONInputData): Unit = js.native
  def dispatchEvent[T](event: BaseEvent[String] & js.Any): Unit = js.native
  def dispose(): Unit = js.native
  def generate(builder: default_b44adefb, output: String | Null): String = js.native
  def generateNodeType(builder: default_b44adefb, output: js.UndefOr[String] = js.undefined): String = js.native
  def get(value: String): NodeClass[NodeClassEventMap] & NodeElements & js.Any = js.native
  def getArrayCount(builder: default_b44adefb): Double | Null = js.native
  def getCacheKey(force: js.UndefOr[Boolean] = js.undefined, ignores: js.UndefOr[js.Object] = js.undefined): Double = js.native
  def getChildren(): js.Object = js.native
  def getElementType(builder: default_b44adefb): String = js.native
  def getGroup(): default_6eddee1e = js.native
  def getHash(builder: default_b44adefb): String = js.native
  def getInputType(builder: default_b44adefb): String = js.native
  def getMemberType(builder: default_b44adefb, name: String): String = js.native
  def getNodeType(builder: default_b44adefb, output: js.UndefOr[String] = js.undefined): String = js.native
  @JSName("getScope")
  protected def getScope$raw(): UniformNodeClass[TValue] = js.native
  def getSerializeChildren(): js.Array[NodeChild] = js.native
  def getShared(builder: default_b44adefb): NodeClass[NodeClassEventMap] & NodeElements & js.Any = js.native
  def getUniformHash(builder: default_b44adefb): String = js.native
  def getUpdateAfterType(): "frame" | "none" | "object" | "render" = js.native
  def getUpdateBeforeType(): "frame" | "none" | "object" | "render" = js.native
  def getUpdateType(): "frame" | "none" | "object" | "render" = js.native
  def hasEventListener[T](`type`: T, listener: js.Function1[js.Any & Event[String, UniformNodeClass[TValue]], Unit]): Boolean = js.native
  def isGlobal(builder: default_b44adefb): Boolean = js.native
  def isolate(): IsolateNode = js.native
  @JSName("label")
  protected def label$raw(name: String): UniformNodeClass[TValue] = js.native
  @JSName("onFrameUpdate")
  protected def onFrameUpdate$raw(callback: js.ThisFunction2[this.type, default_4bde67a8, this.type, js.UndefOr[TValue]]): UniformNodeClass[TValue] = js.native
  @JSName("onObjectUpdate")
  protected def onObjectUpdate$raw(callback: js.ThisFunction2[this.type, default_4bde67a8, this.type, js.UndefOr[TValue]]): UniformNodeClass[TValue] = js.native
  @JSName("onReference")
  protected def onReference$raw(callback: js.ThisFunction1[this.type, default_4bde67a8 | default_b44adefb, js.Any]): UniformNodeClass[TValue] = js.native
  @JSName("onRenderUpdate")
  protected def onRenderUpdate$raw(callback: js.ThisFunction2[this.type, default_4bde67a8, this.type, js.UndefOr[TValue]]): UniformNodeClass[TValue] = js.native
  @JSName("onUpdate")
  protected def onUpdate$raw(callback: js.ThisFunction2[this.type, default_4bde67a8, this.type, js.UndefOr[TValue]], updateType: "frame" | "none" | "object" | "render"): UniformNodeClass[TValue] = js.native
  def overrideNodes(overrides: js.Array[js.Tuple2[NodeClass[NodeClassEventMap] & NodeElements & js.Any, NodeClass[NodeClassEventMap] & NodeElements & js.Any | js.Function1[default_b44adefb, NodeClass[NodeClassEventMap] & NodeElements & js.Any] | Null]] | js.Object): OverrideContextNode = js.native
  def removeEventListener[T](`type`: T, listener: js.Function1[js.Any & Event[String, UniformNodeClass[TValue]], Unit]): Unit = js.native
  def renderOutput(toneMapping: js.UndefOr[0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | Null] = js.undefined, outputColorSpace: js.UndefOr[String | Null] = js.undefined): RenderOutputNode = js.native
  def select(ifNode: Double | NodeClass[NodeClassEventMap] & NodeElements & NodeExtensions["float"] & NumOrBoolExtensions["float"] & FloatExtensions & NumExtensions["float"] & FloatOrVecExtensions["float"] & js.Any, elseNode: js.UndefOr[Double | NodeClass[NodeClassEventMap] & NodeElements & NodeExtensions["float"] & NumOrBoolExtensions["float"] & FloatExtensions & NumExtensions["float"] & FloatOrVecExtensions["float"] & js.Any | Null] = js.undefined): NodeClass[NodeClassEventMap] & NodeElements & NodeExtensions["float"] & NumOrBoolExtensions["float"] & FloatExtensions & NumExtensions["float"] & FloatOrVecExtensions["float"] & js.Any = js.native
  // #WARN: Disambiguated erasure-colliding overload 'select' with @targetName("select_1") and @JSName("select")
  @scala.annotation.targetName("select_1")
  @JSName("select")
  def select[TNodeType](ifNode: NodeClass[NodeClassEventMap] & NodeElements & (NodeExtensions[TNodeType] | js.Any) & js.Any & js.Any, elseNode: js.UndefOr[Node[TNodeType] | Null]): NodeClass[NodeClassEventMap] & NodeElements & (NodeExtensions[TNodeType] | js.Any) & js.Any & js.Any = js.native
  def serialize(json: NodeJSONIntermediateOutputData): Unit = js.native
  @JSName("setGroup")
  protected def setGroup$raw(group: default_6eddee1e): UniformNodeClass[TValue] = js.native
  @JSName("setName")
  protected def setName$raw(name: String): UniformNodeClass[TValue] = js.native
  @JSName("setPrecision")
  protected def setPrecision$raw(precision: "high" | "low" | "medium"): UniformNodeClass[TValue] = js.native
  def setup(builder: default_b44adefb): js.UndefOr[NodeClass[NodeClassEventMap] & NodeElements & js.Any | Null] = js.native
  def toInspector(name: js.UndefOr[String] = js.undefined, callback: js.UndefOr[js.Function1[this.type, Node[js.Any]]] = js.undefined): UniformNodeClass[TValue] = js.native
  def toJSON(meta: js.UndefOr[String | NodeJSONMeta] = js.undefined): NodeJSONOutputData = js.native
  def toneMapping(mapping: js.UndefOr[0 | 1 | 2 | 3 | 4 | 5 | 6 | 7] = js.undefined, exposure: js.UndefOr[Double | NodeClass[NodeClassEventMap] & NodeElements & js.Any] = js.undefined): ToneMappingNode = js.native
  def toStack(): NodeClass[NodeClassEventMap] & NodeElements & js.Any = js.native
  def traverse(callback: js.Function1[NodeClass[NodeClassEventMap] & NodeElements & js.Any, Unit]): Unit = js.native
  def update(frame: default_4bde67a8): js.UndefOr[Boolean] = js.native
  def updateAfter(frame: default_4bde67a8): js.UndefOr[Boolean] = js.native
  def updateBefore(frame: default_4bde67a8): js.UndefOr[Boolean] = js.native
  def updateReference(state: default_4bde67a8 | default_b44adefb): facadecontract.std.JSUnknown = js.native
  def workingToColorSpace(targetColorSpace: String): default_24971b86 = js.native
// #WARN: SHIM-065: Native facade trait 'UniformNodeClass' cannot receive a builder companion: JS property 'select' is written by 2 members

object UniformNodeClass:
  extension [TValue, Self <: UniformNodeClass[TValue]](self: Self)
    inline def setName(name: String): Self = self.setName$raw(name).asInstanceOf[Self]
    inline def label(name: String): Self = self.label$raw(name).asInstanceOf[Self]
    inline def setGroup(group: default_6eddee1e): Self = self.setGroup$raw(group).asInstanceOf[Self]
    inline def onUpdate(callback: js.ThisFunction2[self.type, default_4bde67a8, self.type, js.UndefOr[TValue]], updateType: "frame" | "none" | "object" | "render"): Self = self.onUpdate$raw(callback, updateType).asInstanceOf[Self]
    inline def onFrameUpdate(callback: js.ThisFunction2[self.type, default_4bde67a8, self.type, js.UndefOr[TValue]]): Self = self.onFrameUpdate$raw(callback).asInstanceOf[Self]
    inline def onRenderUpdate(callback: js.ThisFunction2[self.type, default_4bde67a8, self.type, js.UndefOr[TValue]]): Self = self.onRenderUpdate$raw(callback).asInstanceOf[Self]
    inline def onObjectUpdate(callback: js.ThisFunction2[self.type, default_4bde67a8, self.type, js.UndefOr[TValue]]): Self = self.onObjectUpdate$raw(callback).asInstanceOf[Self]
    inline def onReference(callback: js.ThisFunction1[self.type, default_4bde67a8 | default_b44adefb, js.Any]): Self = self.onReference$raw(callback).asInstanceOf[Self]
    inline def getScope(): Self = self.getScope$raw().asInstanceOf[Self]
    inline def before(node: NodeClass[NodeClassEventMap] & NodeElements & js.Any): Self = self.before$raw(node).asInstanceOf[Self]
    inline def setPrecision(precision: "high" | "low" | "medium"): Self = self.setPrecision$raw(precision).asInstanceOf[Self]
