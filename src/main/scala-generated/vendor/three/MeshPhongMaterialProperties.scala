// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait MeshPhongMaterialProperties extends MaterialProperties:
  var alphaMap: Texture[js.Any, TextureEventMap] | Null = js.native
  var aoMap: Texture[js.Any, TextureEventMap] | Null = js.native
  var aoMapIntensity: Double = js.native
  var bumpMap: Texture[js.Any, TextureEventMap] | Null = js.native
  var bumpScale: Double = js.native
  var color: Color = js.native
  var combine: 0 | 1 | 2 = js.native
  var displacementBias: Double = js.native
  var displacementMap: Texture[js.Any, TextureEventMap] | Null = js.native
  var displacementScale: Double = js.native
  var emissive: Color = js.native
  var emissiveIntensity: Double = js.native
  var emissiveMap: Texture[js.Any, TextureEventMap] | Null = js.native
  var envMap: Texture[js.Any, TextureEventMap] | Null = js.native
  var envMapIntensity: Double = js.native
  var envMapRotation: Euler = js.native
  var flatShading: Boolean = js.native
  var fog: Boolean = js.native
  var lightMap: Texture[js.Any, TextureEventMap] | Null = js.native
  var lightMapIntensity: Double = js.native
  var map: Texture[js.Any, TextureEventMap] | Null = js.native
  var normalMap: Texture[js.Any, TextureEventMap] | Null = js.native
  var normalMapType: 0 | 1 = js.native
  var normalScale: Vector2 = js.native
  var reflectivity: Double = js.native
  var refractionRatio: Double = js.native
  var shininess: Double = js.native
  var specular: Color = js.native
  var specularMap: Texture[js.Any, TextureEventMap] | Null = js.native
  var wireframe: Boolean = js.native
  var wireframeLinecap: "bevel" | "miter" | "round" = js.native
  var wireframeLinejoin: "bevel" | "miter" | "round" = js.native
  var wireframeLinewidth: Double = js.native

object MeshPhongMaterialProperties:
  def apply(
    color: Color,
    specular: Color,
    shininess: Double,
    map: Texture[js.Any, TextureEventMap] | Null,
    lightMap: Texture[js.Any, TextureEventMap] | Null,
    lightMapIntensity: Double,
    aoMap: Texture[js.Any, TextureEventMap] | Null,
    aoMapIntensity: Double,
    emissive: Color,
    emissiveIntensity: Double,
    emissiveMap: Texture[js.Any, TextureEventMap] | Null,
    bumpMap: Texture[js.Any, TextureEventMap] | Null,
    bumpScale: Double,
    normalMap: Texture[js.Any, TextureEventMap] | Null,
    normalMapType: 0 | 1,
    normalScale: Vector2,
    displacementMap: Texture[js.Any, TextureEventMap] | Null,
    displacementScale: Double,
    displacementBias: Double,
    specularMap: Texture[js.Any, TextureEventMap] | Null,
    alphaMap: Texture[js.Any, TextureEventMap] | Null,
    envMap: Texture[js.Any, TextureEventMap] | Null,
    envMapRotation: Euler,
    combine: 0 | 1 | 2,
    reflectivity: Double,
    envMapIntensity: Double,
    refractionRatio: Double,
    wireframe: Boolean,
    wireframeLinewidth: Double,
    wireframeLinecap: "bevel" | "miter" | "round",
    wireframeLinejoin: "bevel" | "miter" | "round",
    flatShading: Boolean,
    fog: Boolean,
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
  ): MeshPhongMaterialProperties =
    js.Dynamic.literal(
      "color" -> color.asInstanceOf[js.Any],
      "specular" -> specular.asInstanceOf[js.Any],
      "shininess" -> shininess.asInstanceOf[js.Any],
      "map" -> map.asInstanceOf[js.Any],
      "lightMap" -> lightMap.asInstanceOf[js.Any],
      "lightMapIntensity" -> lightMapIntensity.asInstanceOf[js.Any],
      "aoMap" -> aoMap.asInstanceOf[js.Any],
      "aoMapIntensity" -> aoMapIntensity.asInstanceOf[js.Any],
      "emissive" -> emissive.asInstanceOf[js.Any],
      "emissiveIntensity" -> emissiveIntensity.asInstanceOf[js.Any],
      "emissiveMap" -> emissiveMap.asInstanceOf[js.Any],
      "bumpMap" -> bumpMap.asInstanceOf[js.Any],
      "bumpScale" -> bumpScale.asInstanceOf[js.Any],
      "normalMap" -> normalMap.asInstanceOf[js.Any],
      "normalMapType" -> normalMapType.asInstanceOf[js.Any],
      "normalScale" -> normalScale.asInstanceOf[js.Any],
      "displacementMap" -> displacementMap.asInstanceOf[js.Any],
      "displacementScale" -> displacementScale.asInstanceOf[js.Any],
      "displacementBias" -> displacementBias.asInstanceOf[js.Any],
      "specularMap" -> specularMap.asInstanceOf[js.Any],
      "alphaMap" -> alphaMap.asInstanceOf[js.Any],
      "envMap" -> envMap.asInstanceOf[js.Any],
      "envMapRotation" -> envMapRotation.asInstanceOf[js.Any],
      "combine" -> combine.asInstanceOf[js.Any],
      "reflectivity" -> reflectivity.asInstanceOf[js.Any],
      "envMapIntensity" -> envMapIntensity.asInstanceOf[js.Any],
      "refractionRatio" -> refractionRatio.asInstanceOf[js.Any],
      "wireframe" -> wireframe.asInstanceOf[js.Any],
      "wireframeLinewidth" -> wireframeLinewidth.asInstanceOf[js.Any],
      "wireframeLinecap" -> wireframeLinecap.asInstanceOf[js.Any],
      "wireframeLinejoin" -> wireframeLinejoin.asInstanceOf[js.Any],
      "flatShading" -> flatShading.asInstanceOf[js.Any],
      "fog" -> fog.asInstanceOf[js.Any],
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
    ).asInstanceOf[MeshPhongMaterialProperties]
