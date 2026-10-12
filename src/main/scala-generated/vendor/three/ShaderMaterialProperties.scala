// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait ShaderMaterialProperties extends MaterialProperties:
  var clipping: Boolean = js.native
  var defaultAttributeValues: RawShaderMaterialDefaultAttributeValues = js.native
  def defines: js.Dictionary[js.Any]
  def defines_=(value: js.UndefOr[js.Dictionary[js.Any]]): Unit
  var extensions: RawShaderMaterialExtensions = js.native
  var fog: Boolean = js.native
  var fragmentShader: String = js.native
  var glslVersion: "100" | "300 es" | Null = js.native
  var index0AttributeName: js.UndefOr[String] = js.native
  var lights: Boolean = js.native
  var linewidth: Double = js.native
  var uniforms: js.Dictionary[IUniform[js.Any]] = js.native
  var uniformsGroups: js.Array[UniformsGroup[UniformsGroupEventMap]] = js.native
  var uniformsNeedUpdate: Boolean = js.native
  var vertexShader: String = js.native
  var wireframe: Boolean = js.native
  var wireframeLinewidth: Double = js.native

object ShaderMaterialProperties:
  def apply(
    defines: js.Dictionary[js.Any],
    uniforms: js.Dictionary[IUniform[js.Any]],
    uniformsGroups: js.Array[UniformsGroup[UniformsGroupEventMap]],
    vertexShader: String,
    fragmentShader: String,
    linewidth: Double,
    wireframe: Boolean,
    wireframeLinewidth: Double,
    fog: Boolean,
    lights: Boolean,
    clipping: Boolean,
    extensions: RawShaderMaterialExtensions,
    defaultAttributeValues: RawShaderMaterialDefaultAttributeValues,
    index0AttributeName: js.UndefOr[String],
    uniformsNeedUpdate: Boolean,
    glslVersion: "100" | "300 es" | Null,
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
  ): ShaderMaterialProperties =
    js.Dynamic.literal(
      "defines" -> defines.asInstanceOf[js.Any],
      "uniforms" -> uniforms.asInstanceOf[js.Any],
      "uniformsGroups" -> uniformsGroups.asInstanceOf[js.Any],
      "vertexShader" -> vertexShader.asInstanceOf[js.Any],
      "fragmentShader" -> fragmentShader.asInstanceOf[js.Any],
      "linewidth" -> linewidth.asInstanceOf[js.Any],
      "wireframe" -> wireframe.asInstanceOf[js.Any],
      "wireframeLinewidth" -> wireframeLinewidth.asInstanceOf[js.Any],
      "fog" -> fog.asInstanceOf[js.Any],
      "lights" -> lights.asInstanceOf[js.Any],
      "clipping" -> clipping.asInstanceOf[js.Any],
      "extensions" -> extensions.asInstanceOf[js.Any],
      "defaultAttributeValues" -> defaultAttributeValues.asInstanceOf[js.Any],
      "index0AttributeName" -> index0AttributeName.asInstanceOf[js.Any],
      "uniformsNeedUpdate" -> uniformsNeedUpdate.asInstanceOf[js.Any],
      "glslVersion" -> glslVersion.asInstanceOf[js.Any],
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
    ).asInstanceOf[ShaderMaterialProperties]
