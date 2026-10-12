// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js
import scala.scalajs.js.annotation.*

@js.native
trait BlendMode extends js.Object:
  var blendDst: 200 | 201 | 202 | 203 | 204 | 205 | 206 | 207 | 208 | 209 | 211 | 212 | 213 | 214 = js.native
  var blendDstAlpha: 200 | 201 | 202 | 203 | 204 | 205 | 206 | 207 | 208 | 209 | 211 | 212 | 213 | 214 | Null = js.native
  var blendEquation: 100 | 101 | 102 | 103 | 104 = js.native
  var blendEquationAlpha: 100 | 101 | 102 | 103 | 104 | Null = js.native
  var blending: 0 | 1 | 2 | 3 | 4 | 5 | 6 = js.native
  var blendSrc: 200 | 201 | 202 | 203 | 204 | 205 | 206 | 207 | 208 | 209 | 210 | 211 | 212 | 213 | 214 = js.native
  var blendSrcAlpha: 200 | 201 | 202 | 203 | 204 | 205 | 206 | 207 | 208 | 209 | 210 | 211 | 212 | 213 | 214 | Null = js.native
  var premultiplyAlpha: Boolean = js.native
  override def clone(): BlendMode = js.native
  @JSName("copy")
  protected def copy$raw(source: BlendMode): BlendMode = js.native

object BlendMode:
  def of(
    blendDst: 200 | 201 | 202 | 203 | 204 | 205 | 206 | 207 | 208 | 209 | 211 | 212 | 213 | 214,
    blendDstAlpha: 200 | 201 | 202 | 203 | 204 | 205 | 206 | 207 | 208 | 209 | 211 | 212 | 213 | 214 | Null,
    blendEquation: 100 | 101 | 102 | 103 | 104,
    blendEquationAlpha: 100 | 101 | 102 | 103 | 104 | Null,
    blending: 0 | 1 | 2 | 3 | 4 | 5 | 6,
    blendSrc: 200 | 201 | 202 | 203 | 204 | 205 | 206 | 207 | 208 | 209 | 210 | 211 | 212 | 213 | 214,
    blendSrcAlpha: 200 | 201 | 202 | 203 | 204 | 205 | 206 | 207 | 208 | 209 | 210 | 211 | 212 | 213 | 214 | Null,
    clone: js.Function0[BlendMode],
    copy: js.Function1[BlendMode, BlendMode],
    premultiplyAlpha: Boolean
  ): BlendMode =
    // #COMPLETION_DRIVE: sanctioned construction boundary for native facade trait
    // #SUGGEST_VERIFY: runtime shape test verifies dictionary keys and receiver round-trip
    js.Dynamic.literal(
      "blendDst" -> blendDst.asInstanceOf[js.Any],
      "blendDstAlpha" -> blendDstAlpha.asInstanceOf[js.Any],
      "blendEquation" -> blendEquation.asInstanceOf[js.Any],
      "blendEquationAlpha" -> blendEquationAlpha.asInstanceOf[js.Any],
      "blending" -> blending.asInstanceOf[js.Any],
      "blendSrc" -> blendSrc.asInstanceOf[js.Any],
      "blendSrcAlpha" -> blendSrcAlpha.asInstanceOf[js.Any],
      "clone" -> clone.asInstanceOf[js.Any],
      "copy" -> copy.asInstanceOf[js.Any],
      "premultiplyAlpha" -> premultiplyAlpha.asInstanceOf[js.Any]
    ).asInstanceOf[BlendMode]

  extension [Self <: BlendMode](self: Self)
    inline def copy(source: BlendMode): Self = self.copy$raw(source).asInstanceOf[Self]
