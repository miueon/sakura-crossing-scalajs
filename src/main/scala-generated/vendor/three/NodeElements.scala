// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js
import scala.scalajs.js.annotation.*

// #WARN: Disambiguated erasure-colliding overload 'select' with @targetName("select_1") and @JSName("select")
@js.native
trait NodeElements extends js.Object:
  def append(): NodeClass[NodeClassEventMap] & NodeElements & js.Any = js.native
  def assign(sourceNode: Double | NodeClass[NodeClassEventMap] & NodeElements & js.Any): NodeElements = js.native
  def bypass(callNode: NodeClass[NodeClassEventMap] & NodeElements & js.Any): default = js.native
  def cache(cache: js.UndefOr[NodeCache] = js.undefined): IsolateNode = js.native
  def colorSpaceToWorking(sourceColorSpace: String): default_24971b86 = js.native
  def compute(count: Double, workgroupSize: js.UndefOr[js.Array[Double]] = js.undefined): default_5e5b9ffc = js.native
  def computeKernel(workgroupSize: js.UndefOr[js.Array[Double]] = js.undefined): default_5e5b9ffc = js.native
  def convert(types: String): NodeClass[NodeClassEventMap] & NodeElements & js.Any = js.native
  def debug(callback: js.UndefOr[js.Function2[default_b44adefb, String, Unit] | Null] = js.undefined): DebugNode = js.native
  def get(value: String): NodeClass[NodeClassEventMap] & NodeElements & js.Any = js.native
  def isolate(): IsolateNode = js.native
  def overrideNodes(overrides: js.Array[js.Tuple2[NodeClass[NodeClassEventMap] & NodeElements & js.Any, NodeClass[NodeClassEventMap] & NodeElements & js.Any | js.Function1[default_b44adefb, NodeClass[NodeClassEventMap] & NodeElements & js.Any] | Null]] | js.Object): OverrideContextNode = js.native
  def renderOutput(toneMapping: js.UndefOr[0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | Null] = js.undefined, outputColorSpace: js.UndefOr[String | Null] = js.undefined): RenderOutputNode = js.native
  def select(ifNode: Double | NodeClass[NodeClassEventMap] & NodeElements & NodeExtensions["float"] & NumOrBoolExtensions["float"] & FloatExtensions & NumExtensions["float"] & FloatOrVecExtensions["float"] & js.Any, elseNode: js.UndefOr[Double | NodeClass[NodeClassEventMap] & NodeElements & NodeExtensions["float"] & NumOrBoolExtensions["float"] & FloatExtensions & NumExtensions["float"] & FloatOrVecExtensions["float"] & js.Any | Null] = js.undefined): NodeClass[NodeClassEventMap] & NodeElements & NodeExtensions["float"] & NumOrBoolExtensions["float"] & FloatExtensions & NumExtensions["float"] & FloatOrVecExtensions["float"] & js.Any = js.native
  // #WARN: Disambiguated erasure-colliding overload 'select' with @targetName("select_1") and @JSName("select")
  @scala.annotation.targetName("select_1")
  @JSName("select")
  def select[TNodeType](ifNode: NodeClass[NodeClassEventMap] & NodeElements & (NodeExtensions[TNodeType] | js.Any) & js.Any & js.Any, elseNode: js.UndefOr[Node[TNodeType] | Null]): NodeClass[NodeClassEventMap] & NodeElements & (NodeExtensions[TNodeType] | js.Any) & js.Any & js.Any = js.native
  def toInspector(name: js.UndefOr[String] = js.undefined, callback: js.UndefOr[js.Function1[this.type, Node[js.Any]]] = js.undefined): NodeElements = js.native
  def toneMapping(mapping: js.UndefOr[0 | 1 | 2 | 3 | 4 | 5 | 6 | 7] = js.undefined, exposure: js.UndefOr[Double | NodeClass[NodeClassEventMap] & NodeElements & js.Any] = js.undefined): ToneMappingNode = js.native
  def toStack(): NodeClass[NodeClassEventMap] & NodeElements & js.Any = js.native
  def workingToColorSpace(targetColorSpace: String): default_24971b86 = js.native
// #WARN: SHIM-065: Native facade trait 'NodeElements' cannot receive a builder companion: JS property 'select' is written by 2 members
