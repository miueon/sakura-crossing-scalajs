// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait MeshNormalMaterialParameters extends MeshNormalMaterialParameters$Partial

object MeshNormalMaterialParameters:
  def apply(
    bumpMap: js.UndefOr[Texture[js.Any, TextureEventMap] | Null] = js.undefined,
    bumpScale: js.UndefOr[Double] = js.undefined,
    normalMap: js.UndefOr[Texture[js.Any, TextureEventMap] | Null] = js.undefined,
    normalMapType: js.UndefOr[0 | 1] = js.undefined,
    normalScale: js.UndefOr[Vector2] = js.undefined,
    displacementMap: js.UndefOr[Texture[js.Any, TextureEventMap] | Null] = js.undefined,
    displacementScale: js.UndefOr[Double] = js.undefined,
    displacementBias: js.UndefOr[Double] = js.undefined,
    wireframe: js.UndefOr[Boolean] = js.undefined,
    wireframeLinewidth: js.UndefOr[Double] = js.undefined,
    flatShading: js.UndefOr[Boolean] = js.undefined,
    name: js.UndefOr[String] = js.undefined,
    blending: js.UndefOr[0 | 1 | 2 | 3 | 4 | 5 | 6] = js.undefined,
    side: js.UndefOr[0 | 1 | 2] = js.undefined,
    vertexColors: js.UndefOr[Boolean] = js.undefined,
    opacity: js.UndefOr[Double] = js.undefined,
    `transparent`: js.UndefOr[Boolean] = js.undefined,
    alphaHash: js.UndefOr[Boolean] = js.undefined,
    blendSrc: js.UndefOr[200 | 201 | 202 | 203 | 204 | 205 | 206 | 207 | 208 | 209 | 210 | 211 | 212 | 213 | 214] = js.undefined,
    blendDst: js.UndefOr[200 | 201 | 202 | 203 | 204 | 205 | 206 | 207 | 208 | 209 | 211 | 212 | 213 | 214] = js.undefined,
    blendEquation: js.UndefOr[100 | 101 | 102 | 103 | 104] = js.undefined,
    blendSrcAlpha: js.UndefOr[200 | 201 | 202 | 203 | 204 | 205 | 206 | 207 | 208 | 209 | 210 | 211 | 212 | 213 | 214 | Null] = js.undefined,
    blendDstAlpha: js.UndefOr[200 | 201 | 202 | 203 | 204 | 205 | 206 | 207 | 208 | 209 | 211 | 212 | 213 | 214 | Null] = js.undefined,
    blendEquationAlpha: js.UndefOr[100 | 101 | 102 | 103 | 104 | Null] = js.undefined,
    blendColor: js.UndefOr[String | Double | Color] = js.undefined,
    blendAlpha: js.UndefOr[Double] = js.undefined,
    depthFunc: js.UndefOr[0 | 1 | 2 | 3 | 4 | 5 | 6 | 7] = js.undefined,
    depthTest: js.UndefOr[Boolean] = js.undefined,
    depthWrite: js.UndefOr[Boolean] = js.undefined,
    stencilWriteMask: js.UndefOr[Double] = js.undefined,
    stencilFunc: js.UndefOr[512 | 513 | 514 | 515 | 516 | 517 | 518 | 519] = js.undefined,
    stencilRef: js.UndefOr[Double] = js.undefined,
    stencilFuncMask: js.UndefOr[Double] = js.undefined,
    stencilFail: js.UndefOr[0 | 5386 | 7283 | 7680 | 7681 | 7682 | 34055 | 34056] = js.undefined,
    stencilZFail: js.UndefOr[0 | 5386 | 7283 | 7680 | 7681 | 7682 | 34055 | 34056] = js.undefined,
    stencilZPass: js.UndefOr[0 | 5386 | 7283 | 7680 | 7681 | 7682 | 34055 | 34056] = js.undefined,
    stencilWrite: js.UndefOr[Boolean] = js.undefined,
    clippingPlanes: js.UndefOr[js.Array[Plane] | Null] = js.undefined,
    clipIntersection: js.UndefOr[Boolean] = js.undefined,
    clipShadows: js.UndefOr[Boolean] = js.undefined,
    shadowSide: js.UndefOr[0 | 1 | 2 | Null] = js.undefined,
    colorWrite: js.UndefOr[Boolean] = js.undefined,
    precision: js.UndefOr["highp" | "lowp" | "mediump" | Null] = js.undefined,
    polygonOffset: js.UndefOr[Boolean] = js.undefined,
    polygonOffsetFactor: js.UndefOr[Double] = js.undefined,
    polygonOffsetUnits: js.UndefOr[Double] = js.undefined,
    dithering: js.UndefOr[Boolean] = js.undefined,
    alphaToCoverage: js.UndefOr[Boolean] = js.undefined,
    premultipliedAlpha: js.UndefOr[Boolean] = js.undefined,
    forceSinglePass: js.UndefOr[Boolean] = js.undefined,
    allowOverride: js.UndefOr[Boolean] = js.undefined,
    visible: js.UndefOr[Boolean] = js.undefined,
    toneMapped: js.UndefOr[Boolean] = js.undefined,
    userData: js.UndefOr[js.Dictionary[js.Any]] = js.undefined,
    alphaTest: js.UndefOr[Double] = js.undefined
  ): MeshNormalMaterialParameters =
    val _res = js.Dynamic.literal()
    bumpMap.foreach(v => _res.updateDynamic("bumpMap")(v.asInstanceOf[js.Any]))
    bumpScale.foreach(v => _res.updateDynamic("bumpScale")(v.asInstanceOf[js.Any]))
    normalMap.foreach(v => _res.updateDynamic("normalMap")(v.asInstanceOf[js.Any]))
    normalMapType.foreach(v => _res.updateDynamic("normalMapType")(v.asInstanceOf[js.Any]))
    normalScale.foreach(v => _res.updateDynamic("normalScale")(v.asInstanceOf[js.Any]))
    displacementMap.foreach(v => _res.updateDynamic("displacementMap")(v.asInstanceOf[js.Any]))
    displacementScale.foreach(v => _res.updateDynamic("displacementScale")(v.asInstanceOf[js.Any]))
    displacementBias.foreach(v => _res.updateDynamic("displacementBias")(v.asInstanceOf[js.Any]))
    wireframe.foreach(v => _res.updateDynamic("wireframe")(v.asInstanceOf[js.Any]))
    wireframeLinewidth.foreach(v => _res.updateDynamic("wireframeLinewidth")(v.asInstanceOf[js.Any]))
    flatShading.foreach(v => _res.updateDynamic("flatShading")(v.asInstanceOf[js.Any]))
    name.foreach(v => _res.updateDynamic("name")(v.asInstanceOf[js.Any]))
    blending.foreach(v => _res.updateDynamic("blending")(v.asInstanceOf[js.Any]))
    side.foreach(v => _res.updateDynamic("side")(v.asInstanceOf[js.Any]))
    vertexColors.foreach(v => _res.updateDynamic("vertexColors")(v.asInstanceOf[js.Any]))
    opacity.foreach(v => _res.updateDynamic("opacity")(v.asInstanceOf[js.Any]))
    `transparent`.foreach(v => _res.updateDynamic("transparent")(v.asInstanceOf[js.Any]))
    alphaHash.foreach(v => _res.updateDynamic("alphaHash")(v.asInstanceOf[js.Any]))
    blendSrc.foreach(v => _res.updateDynamic("blendSrc")(v.asInstanceOf[js.Any]))
    blendDst.foreach(v => _res.updateDynamic("blendDst")(v.asInstanceOf[js.Any]))
    blendEquation.foreach(v => _res.updateDynamic("blendEquation")(v.asInstanceOf[js.Any]))
    blendSrcAlpha.foreach(v => _res.updateDynamic("blendSrcAlpha")(v.asInstanceOf[js.Any]))
    blendDstAlpha.foreach(v => _res.updateDynamic("blendDstAlpha")(v.asInstanceOf[js.Any]))
    blendEquationAlpha.foreach(v => _res.updateDynamic("blendEquationAlpha")(v.asInstanceOf[js.Any]))
    blendColor.foreach(v => _res.updateDynamic("blendColor")(v.asInstanceOf[js.Any]))
    blendAlpha.foreach(v => _res.updateDynamic("blendAlpha")(v.asInstanceOf[js.Any]))
    depthFunc.foreach(v => _res.updateDynamic("depthFunc")(v.asInstanceOf[js.Any]))
    depthTest.foreach(v => _res.updateDynamic("depthTest")(v.asInstanceOf[js.Any]))
    depthWrite.foreach(v => _res.updateDynamic("depthWrite")(v.asInstanceOf[js.Any]))
    stencilWriteMask.foreach(v => _res.updateDynamic("stencilWriteMask")(v.asInstanceOf[js.Any]))
    stencilFunc.foreach(v => _res.updateDynamic("stencilFunc")(v.asInstanceOf[js.Any]))
    stencilRef.foreach(v => _res.updateDynamic("stencilRef")(v.asInstanceOf[js.Any]))
    stencilFuncMask.foreach(v => _res.updateDynamic("stencilFuncMask")(v.asInstanceOf[js.Any]))
    stencilFail.foreach(v => _res.updateDynamic("stencilFail")(v.asInstanceOf[js.Any]))
    stencilZFail.foreach(v => _res.updateDynamic("stencilZFail")(v.asInstanceOf[js.Any]))
    stencilZPass.foreach(v => _res.updateDynamic("stencilZPass")(v.asInstanceOf[js.Any]))
    stencilWrite.foreach(v => _res.updateDynamic("stencilWrite")(v.asInstanceOf[js.Any]))
    clippingPlanes.foreach(v => _res.updateDynamic("clippingPlanes")(v.asInstanceOf[js.Any]))
    clipIntersection.foreach(v => _res.updateDynamic("clipIntersection")(v.asInstanceOf[js.Any]))
    clipShadows.foreach(v => _res.updateDynamic("clipShadows")(v.asInstanceOf[js.Any]))
    shadowSide.foreach(v => _res.updateDynamic("shadowSide")(v.asInstanceOf[js.Any]))
    colorWrite.foreach(v => _res.updateDynamic("colorWrite")(v.asInstanceOf[js.Any]))
    precision.foreach(v => _res.updateDynamic("precision")(v.asInstanceOf[js.Any]))
    polygonOffset.foreach(v => _res.updateDynamic("polygonOffset")(v.asInstanceOf[js.Any]))
    polygonOffsetFactor.foreach(v => _res.updateDynamic("polygonOffsetFactor")(v.asInstanceOf[js.Any]))
    polygonOffsetUnits.foreach(v => _res.updateDynamic("polygonOffsetUnits")(v.asInstanceOf[js.Any]))
    dithering.foreach(v => _res.updateDynamic("dithering")(v.asInstanceOf[js.Any]))
    alphaToCoverage.foreach(v => _res.updateDynamic("alphaToCoverage")(v.asInstanceOf[js.Any]))
    premultipliedAlpha.foreach(v => _res.updateDynamic("premultipliedAlpha")(v.asInstanceOf[js.Any]))
    forceSinglePass.foreach(v => _res.updateDynamic("forceSinglePass")(v.asInstanceOf[js.Any]))
    allowOverride.foreach(v => _res.updateDynamic("allowOverride")(v.asInstanceOf[js.Any]))
    visible.foreach(v => _res.updateDynamic("visible")(v.asInstanceOf[js.Any]))
    toneMapped.foreach(v => _res.updateDynamic("toneMapped")(v.asInstanceOf[js.Any]))
    userData.foreach(v => _res.updateDynamic("userData")(v.asInstanceOf[js.Any]))
    alphaTest.foreach(v => _res.updateDynamic("alphaTest")(v.asInstanceOf[js.Any]))
    _res.asInstanceOf[MeshNormalMaterialParameters]
