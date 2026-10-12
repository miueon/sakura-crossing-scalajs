// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js
import scala.scalajs.js.annotation.*

@js.native
trait VaryingNodeInterface extends js.Object:
  var interpolationSampling: "centroid" | "either" | "first" | "normal" | "sample" | Null = js.native
  var interpolationType: "flat" | "linear" | "perspective" | Null = js.native
  def isVaryingNode: true = js.native
  var name: String | Null = js.native
  var node: NodeClass[NodeClassEventMap] & NodeElements & js.Any = js.native
  @JSName("setInterpolation")
  protected def setInterpolation$raw(`type`: "flat" | "linear" | "perspective" | Null, sampling: js.UndefOr["centroid" | "either" | "first" | "normal" | "sample" | Null] = js.undefined): VaryingNodeInterface = js.native
  def setupVarying(builder: default_b44adefb): NodeVarying = js.native

object VaryingNodeInterface:
  def of(
    interpolationSampling: "centroid" | "either" | "first" | "normal" | "sample" | Null,
    interpolationType: "flat" | "linear" | "perspective" | Null,
    isVaryingNode: true,
    name: String | Null,
    node: NodeClass[NodeClassEventMap] & NodeElements & js.Any,
    setInterpolation: js.Function2["flat" | "linear" | "perspective" | Null, js.UndefOr["centroid" | "either" | "first" | "normal" | "sample" | Null], VaryingNodeInterface],
    setupVarying: js.Function1[default_b44adefb, NodeVarying]
  ): VaryingNodeInterface =
    // #COMPLETION_DRIVE: sanctioned construction boundary for native facade trait
    // #SUGGEST_VERIFY: runtime shape test verifies dictionary keys and receiver round-trip
    js.Dynamic.literal(
      "interpolationSampling" -> interpolationSampling.asInstanceOf[js.Any],
      "interpolationType" -> interpolationType.asInstanceOf[js.Any],
      "isVaryingNode" -> isVaryingNode.asInstanceOf[js.Any],
      "name" -> name.asInstanceOf[js.Any],
      "node" -> node.asInstanceOf[js.Any],
      "setInterpolation" -> setInterpolation.asInstanceOf[js.Any],
      "setupVarying" -> setupVarying.asInstanceOf[js.Any]
    ).asInstanceOf[VaryingNodeInterface]

  extension [Self <: VaryingNodeInterface](self: Self)
    inline def setInterpolation(`type`: "flat" | "linear" | "perspective" | Null, sampling: js.UndefOr["centroid" | "either" | "first" | "normal" | "sample" | Null] = js.undefined): Self = self.setInterpolation$raw(`type`, sampling).asInstanceOf[Self]
