// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait MaterialProperties extends js.Object:
  var allowOverride: Boolean = js.native
  var alphaHash: Boolean = js.native
  var alphaTest: Double = js.native
  var alphaToCoverage: Boolean = js.native
  var blendAlpha: Double = js.native
  var blendColor: Color = js.native
  var blendDst: 200 | 201 | 202 | 203 | 204 | 205 | 206 | 207 | 208 | 209 | 211 | 212 | 213 | 214 = js.native
  var blendDstAlpha: 200 | 201 | 202 | 203 | 204 | 205 | 206 | 207 | 208 | 209 | 211 | 212 | 213 | 214 | Null = js.native
  var blendEquation: 100 | 101 | 102 | 103 | 104 = js.native
  var blendEquationAlpha: 100 | 101 | 102 | 103 | 104 | Null = js.native
  var blending: 0 | 1 | 2 | 3 | 4 | 5 | 6 = js.native
  var blendSrc: 200 | 201 | 202 | 203 | 204 | 205 | 206 | 207 | 208 | 209 | 210 | 211 | 212 | 213 | 214 = js.native
  var blendSrcAlpha: 200 | 201 | 202 | 203 | 204 | 205 | 206 | 207 | 208 | 209 | 210 | 211 | 212 | 213 | 214 | Null = js.native
  var clipIntersection: Boolean = js.native
  var clippingPlanes: js.Array[Plane] | Null = js.native
  var clipShadows: Boolean = js.native
  var colorWrite: Boolean = js.native
  var depthFunc: 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 = js.native
  var depthTest: Boolean = js.native
  var depthWrite: Boolean = js.native
  var dithering: Boolean = js.native
  var forceSinglePass: Boolean = js.native
  var name: String = js.native
  var opacity: Double = js.native
  var polygonOffset: Boolean = js.native
  var polygonOffsetFactor: Double = js.native
  var polygonOffsetUnits: Double = js.native
  var precision: "highp" | "lowp" | "mediump" | Null = js.native
  var premultipliedAlpha: Boolean = js.native
  var shadowSide: 0 | 1 | 2 | Null = js.native
  var side: 0 | 1 | 2 = js.native
  var stencilFail: 0 | 5386 | 7283 | 7680 | 7681 | 7682 | 34055 | 34056 = js.native
  var stencilFunc: 512 | 513 | 514 | 515 | 516 | 517 | 518 | 519 = js.native
  var stencilFuncMask: Double = js.native
  var stencilRef: Double = js.native
  var stencilWrite: Boolean = js.native
  var stencilWriteMask: Double = js.native
  var stencilZFail: 0 | 5386 | 7283 | 7680 | 7681 | 7682 | 34055 | 34056 = js.native
  var stencilZPass: 0 | 5386 | 7283 | 7680 | 7681 | 7682 | 34055 | 34056 = js.native
  var toneMapped: Boolean = js.native
  var `transparent`: Boolean = js.native
  var userData: js.Dictionary[js.Any] = js.native
  var vertexColors: Boolean = js.native
  var visible: Boolean = js.native

object MaterialProperties:
  def apply(
    name: String,
    blending: 0 | 1 | 2 | 3 | 4 | 5 | 6,
    side: 0 | 1 | 2,
    vertexColors: Boolean,
    opacity: Double,
    `transparent`: Boolean,
    alphaHash: Boolean,
    blendSrc: 200 | 201 | 202 | 203 | 204 | 205 | 206 | 207 | 208 | 209 | 210 | 211 | 212 | 213 | 214,
    blendDst: 200 | 201 | 202 | 203 | 204 | 205 | 206 | 207 | 208 | 209 | 211 | 212 | 213 | 214,
    blendEquation: 100 | 101 | 102 | 103 | 104,
    blendSrcAlpha: 200 | 201 | 202 | 203 | 204 | 205 | 206 | 207 | 208 | 209 | 210 | 211 | 212 | 213 | 214 | Null,
    blendDstAlpha: 200 | 201 | 202 | 203 | 204 | 205 | 206 | 207 | 208 | 209 | 211 | 212 | 213 | 214 | Null,
    blendEquationAlpha: 100 | 101 | 102 | 103 | 104 | Null,
    blendColor: Color,
    blendAlpha: Double,
    depthFunc: 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7,
    depthTest: Boolean,
    depthWrite: Boolean,
    stencilWriteMask: Double,
    stencilFunc: 512 | 513 | 514 | 515 | 516 | 517 | 518 | 519,
    stencilRef: Double,
    stencilFuncMask: Double,
    stencilFail: 0 | 5386 | 7283 | 7680 | 7681 | 7682 | 34055 | 34056,
    stencilZFail: 0 | 5386 | 7283 | 7680 | 7681 | 7682 | 34055 | 34056,
    stencilZPass: 0 | 5386 | 7283 | 7680 | 7681 | 7682 | 34055 | 34056,
    stencilWrite: Boolean,
    clippingPlanes: js.Array[Plane] | Null,
    clipIntersection: Boolean,
    clipShadows: Boolean,
    shadowSide: 0 | 1 | 2 | Null,
    colorWrite: Boolean,
    precision: "highp" | "lowp" | "mediump" | Null,
    polygonOffset: Boolean,
    polygonOffsetFactor: Double,
    polygonOffsetUnits: Double,
    dithering: Boolean,
    alphaToCoverage: Boolean,
    premultipliedAlpha: Boolean,
    forceSinglePass: Boolean,
    allowOverride: Boolean,
    visible: Boolean,
    toneMapped: Boolean,
    userData: js.Dictionary[js.Any],
    alphaTest: Double
  ): MaterialProperties =
    js.Dynamic.literal(
      "name" -> name.asInstanceOf[js.Any],
      "blending" -> blending.asInstanceOf[js.Any],
      "side" -> side.asInstanceOf[js.Any],
      "vertexColors" -> vertexColors.asInstanceOf[js.Any],
      "opacity" -> opacity.asInstanceOf[js.Any],
      "transparent" -> `transparent`.asInstanceOf[js.Any],
      "alphaHash" -> alphaHash.asInstanceOf[js.Any],
      "blendSrc" -> blendSrc.asInstanceOf[js.Any],
      "blendDst" -> blendDst.asInstanceOf[js.Any],
      "blendEquation" -> blendEquation.asInstanceOf[js.Any],
      "blendSrcAlpha" -> blendSrcAlpha.asInstanceOf[js.Any],
      "blendDstAlpha" -> blendDstAlpha.asInstanceOf[js.Any],
      "blendEquationAlpha" -> blendEquationAlpha.asInstanceOf[js.Any],
      "blendColor" -> blendColor.asInstanceOf[js.Any],
      "blendAlpha" -> blendAlpha.asInstanceOf[js.Any],
      "depthFunc" -> depthFunc.asInstanceOf[js.Any],
      "depthTest" -> depthTest.asInstanceOf[js.Any],
      "depthWrite" -> depthWrite.asInstanceOf[js.Any],
      "stencilWriteMask" -> stencilWriteMask.asInstanceOf[js.Any],
      "stencilFunc" -> stencilFunc.asInstanceOf[js.Any],
      "stencilRef" -> stencilRef.asInstanceOf[js.Any],
      "stencilFuncMask" -> stencilFuncMask.asInstanceOf[js.Any],
      "stencilFail" -> stencilFail.asInstanceOf[js.Any],
      "stencilZFail" -> stencilZFail.asInstanceOf[js.Any],
      "stencilZPass" -> stencilZPass.asInstanceOf[js.Any],
      "stencilWrite" -> stencilWrite.asInstanceOf[js.Any],
      "clippingPlanes" -> clippingPlanes.asInstanceOf[js.Any],
      "clipIntersection" -> clipIntersection.asInstanceOf[js.Any],
      "clipShadows" -> clipShadows.asInstanceOf[js.Any],
      "shadowSide" -> shadowSide.asInstanceOf[js.Any],
      "colorWrite" -> colorWrite.asInstanceOf[js.Any],
      "precision" -> precision.asInstanceOf[js.Any],
      "polygonOffset" -> polygonOffset.asInstanceOf[js.Any],
      "polygonOffsetFactor" -> polygonOffsetFactor.asInstanceOf[js.Any],
      "polygonOffsetUnits" -> polygonOffsetUnits.asInstanceOf[js.Any],
      "dithering" -> dithering.asInstanceOf[js.Any],
      "alphaToCoverage" -> alphaToCoverage.asInstanceOf[js.Any],
      "premultipliedAlpha" -> premultipliedAlpha.asInstanceOf[js.Any],
      "forceSinglePass" -> forceSinglePass.asInstanceOf[js.Any],
      "allowOverride" -> allowOverride.asInstanceOf[js.Any],
      "visible" -> visible.asInstanceOf[js.Any],
      "toneMapped" -> toneMapped.asInstanceOf[js.Any],
      "userData" -> userData.asInstanceOf[js.Any],
      "alphaTest" -> alphaTest.asInstanceOf[js.Any]
    ).asInstanceOf[MaterialProperties]
