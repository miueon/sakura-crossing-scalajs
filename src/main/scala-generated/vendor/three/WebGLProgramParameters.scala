// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait WebGLProgramParameters extends js.Object:
  var alphaHash: Boolean = js.native
  var alphaMap: Boolean = js.native
  var alphaMapUv: String | false = js.native
  var alphaTest: Boolean = js.native
  var alphaToCoverage: Boolean = js.native
  var anisotropy: Boolean = js.native
  var anisotropyMap: Boolean = js.native
  var anisotropyMapUv: String | false = js.native
  var aoMap: Boolean = js.native
  var aoMapUv: String | false = js.native
  var batching: Boolean = js.native
  var batchingColor: Boolean = js.native
  var bumpMap: Boolean = js.native
  var bumpMapUv: String | false = js.native
  var clearcoat: Boolean = js.native
  var clearcoatMap: Boolean = js.native
  var clearcoatMapUv: String | false = js.native
  var clearcoatNormalMap: Boolean = js.native
  var clearcoatNormalMapUv: String | false = js.native
  var clearcoatRoughnessMap: Boolean = js.native
  var clearcoatRoughnessMapUv: String | false = js.native
  var combine: js.UndefOr[0 | 1 | 2] = js.native
  var customFragmentShaderID: js.UndefOr[String] = js.native
  var customProgramCacheKey: String = js.native
  var customVertexShaderID: js.UndefOr[String] = js.native
  var decodeVideoTexture: Boolean = js.native
  var decodeVideoTextureEmissive: Boolean = js.native
  var defines: js.UndefOr[js.Dictionary[String | Double | Boolean]] = js.native
  var depthPacking: 0 | 3200 | 3201 | 3202 | 3203 = js.native
  var dispersion: Boolean = js.native
  var displacementMap: Boolean = js.native
  var displacementMapUv: String | false = js.native
  var dithering: Boolean = js.native
  var doubleSided: Boolean = js.native
  var emissiveMap: Boolean = js.native
  var emissiveMapUv: String | false = js.native
  var envMap: Boolean = js.native
  var envMapCubeUVHeight: Double | Null = js.native
  var envMapMode: 300 | 303 | 304 | false = js.native
  var extensionClipCullDistance: Boolean = js.native
  var extensionMultiDraw: Boolean = js.native
  var flatShading: Boolean = js.native
  var flipSided: Boolean = js.native
  var fog: Boolean = js.native
  var fogExp2: Boolean = js.native
  var fragmentShader: String = js.native
  var glslVersion: js.UndefOr["100" | "300 es" | Null] = js.native
  var gradientMap: Boolean = js.native
  var hasPositionAttribute: Boolean = js.native
  var index0AttributeName: js.UndefOr[String] = js.native
  var instancing: Boolean = js.native
  var instancingColor: Boolean = js.native
  var instancingMorph: Boolean = js.native
  var iridescence: Boolean = js.native
  var iridescenceMap: Boolean = js.native
  var iridescenceMapUv: String | false = js.native
  var iridescenceThicknessMap: Boolean = js.native
  var iridescenceThicknessMapUv: String | false = js.native
  var isRawShaderMaterial: Boolean = js.native
  var lightMap: Boolean = js.native
  var lightMapUv: String | false = js.native
  var logarithmicDepthBuffer: Boolean = js.native
  var map: Boolean = js.native
  var mapUv: String | false = js.native
  var matcap: Boolean = js.native
  var metalnessMap: Boolean = js.native
  var metalnessMapUv: String | false = js.native
  var morphColors: Boolean = js.native
  var morphNormals: Boolean = js.native
  var morphTargets: Boolean = js.native
  var morphTargetsCount: Double = js.native
  var morphTextureStride: Double = js.native
  var normalMap: Boolean = js.native
  var normalMapObjectSpace: Boolean = js.native
  var normalMapTangentSpace: Boolean = js.native
  var normalMapUv: String | false = js.native
  var numClipIntersection: Double = js.native
  var numClippingPlanes: Double = js.native
  var numDirLights: Double = js.native
  var numDirLightShadows: Double = js.native
  var numHemiLights: Double = js.native
  var numLightProbeGrids: Double = js.native
  var numLightProbes: Double = js.native
  var numPointLights: Double = js.native
  var numPointLightShadows: Double = js.native
  var numRectAreaLights: Double = js.native
  var numSpotLightMaps: Double = js.native
  var numSpotLights: Double = js.native
  var numSpotLightShadows: Double = js.native
  var numSpotLightShadowsWithMaps: Double = js.native
  var `opaque`: Boolean = js.native
  var outputColorSpace: String = js.native
  var packedNormalMap: Boolean = js.native
  var pointsUvs: Boolean = js.native
  var precision: "highp" | "lowp" | "mediump" = js.native
  var premultipliedAlpha: Boolean = js.native
  var rendererExtensionParallelShaderCompile: Boolean = js.native
  var reverseDepthBuffer: Boolean = js.native
  var roughnessMap: Boolean = js.native
  var roughnessMapUv: String | false = js.native
  var shaderID: String = js.native
  var shaderName: String = js.native
  var shaderType: String = js.native
  var shadowMapEnabled: Boolean = js.native
  var shadowMapType: 0 | 1 | 2 | 3 = js.native
  var sheen: Boolean = js.native
  var sheenColorMap: Boolean = js.native
  var sheenColorMapUv: String | false = js.native
  var sheenRoughnessMap: Boolean = js.native
  var sheenRoughnessMapUv: String | false = js.native
  var sizeAttenuation: Boolean = js.native
  var skinning: Boolean = js.native
  var specularColorMap: Boolean = js.native
  var specularColorMapUv: String | false = js.native
  var specularIntensityMap: Boolean = js.native
  var specularIntensityMapUv: String | false = js.native
  var specularMap: Boolean = js.native
  var specularMapUv: String | false = js.native
  var thicknessMap: Boolean = js.native
  var thicknessMapUv: String | false = js.native
  var toneMapping: 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 = js.native
  var transmission: Boolean = js.native
  var transmissionMap: Boolean = js.native
  var transmissionMapUv: String | false = js.native
  var useDepthPacking: Boolean = js.native
  var useFog: Boolean = js.native
  var vertexAlphas: Boolean = js.native
  var vertexColors: Boolean = js.native
  var vertexNormals: Boolean = js.native
  var vertexShader: String = js.native
  var vertexTangents: Boolean = js.native
  var vertexUv1s: Boolean = js.native
  var vertexUv2s: Boolean = js.native
  var vertexUv3s: Boolean = js.native

object WebGLProgramParameters:
  def apply(
    shaderID: String,
    shaderType: String,
    shaderName: String,
    vertexShader: String,
    fragmentShader: String,
    defines: js.UndefOr[js.Dictionary[String | Double | Boolean]],
    customVertexShaderID: js.UndefOr[String],
    customFragmentShaderID: js.UndefOr[String],
    isRawShaderMaterial: Boolean,
    glslVersion: js.UndefOr["100" | "300 es" | Null],
    precision: "highp" | "lowp" | "mediump",
    batching: Boolean,
    batchingColor: Boolean,
    instancing: Boolean,
    instancingColor: Boolean,
    instancingMorph: Boolean,
    outputColorSpace: String,
    alphaToCoverage: Boolean,
    map: Boolean,
    matcap: Boolean,
    envMap: Boolean,
    envMapMode: 300 | 303 | 304 | false,
    envMapCubeUVHeight: Double | Null,
    aoMap: Boolean,
    lightMap: Boolean,
    bumpMap: Boolean,
    normalMap: Boolean,
    displacementMap: Boolean,
    emissiveMap: Boolean,
    normalMapObjectSpace: Boolean,
    normalMapTangentSpace: Boolean,
    packedNormalMap: Boolean,
    metalnessMap: Boolean,
    roughnessMap: Boolean,
    anisotropy: Boolean,
    anisotropyMap: Boolean,
    clearcoat: Boolean,
    clearcoatMap: Boolean,
    clearcoatNormalMap: Boolean,
    clearcoatRoughnessMap: Boolean,
    dispersion: Boolean,
    iridescence: Boolean,
    iridescenceMap: Boolean,
    iridescenceThicknessMap: Boolean,
    sheen: Boolean,
    sheenColorMap: Boolean,
    sheenRoughnessMap: Boolean,
    specularMap: Boolean,
    specularColorMap: Boolean,
    specularIntensityMap: Boolean,
    transmission: Boolean,
    transmissionMap: Boolean,
    thicknessMap: Boolean,
    gradientMap: Boolean,
    `opaque`: Boolean,
    alphaMap: Boolean,
    alphaTest: Boolean,
    alphaHash: Boolean,
    combine: js.UndefOr[0 | 1 | 2],
    mapUv: String | false,
    aoMapUv: String | false,
    lightMapUv: String | false,
    bumpMapUv: String | false,
    normalMapUv: String | false,
    displacementMapUv: String | false,
    emissiveMapUv: String | false,
    metalnessMapUv: String | false,
    roughnessMapUv: String | false,
    anisotropyMapUv: String | false,
    clearcoatMapUv: String | false,
    clearcoatNormalMapUv: String | false,
    clearcoatRoughnessMapUv: String | false,
    iridescenceMapUv: String | false,
    iridescenceThicknessMapUv: String | false,
    sheenColorMapUv: String | false,
    sheenRoughnessMapUv: String | false,
    specularMapUv: String | false,
    specularColorMapUv: String | false,
    specularIntensityMapUv: String | false,
    transmissionMapUv: String | false,
    thicknessMapUv: String | false,
    alphaMapUv: String | false,
    vertexTangents: Boolean,
    vertexNormals: Boolean,
    vertexColors: Boolean,
    vertexAlphas: Boolean,
    vertexUv1s: Boolean,
    vertexUv2s: Boolean,
    vertexUv3s: Boolean,
    pointsUvs: Boolean,
    fog: Boolean,
    useFog: Boolean,
    fogExp2: Boolean,
    flatShading: Boolean,
    sizeAttenuation: Boolean,
    logarithmicDepthBuffer: Boolean,
    reverseDepthBuffer: Boolean,
    skinning: Boolean,
    hasPositionAttribute: Boolean,
    morphTargets: Boolean,
    morphNormals: Boolean,
    morphColors: Boolean,
    morphTargetsCount: Double,
    morphTextureStride: Double,
    numDirLights: Double,
    numPointLights: Double,
    numSpotLights: Double,
    numSpotLightMaps: Double,
    numRectAreaLights: Double,
    numHemiLights: Double,
    numDirLightShadows: Double,
    numPointLightShadows: Double,
    numSpotLightShadows: Double,
    numSpotLightShadowsWithMaps: Double,
    numLightProbes: Double,
    numLightProbeGrids: Double,
    numClippingPlanes: Double,
    numClipIntersection: Double,
    dithering: Boolean,
    shadowMapEnabled: Boolean,
    shadowMapType: 0 | 1 | 2 | 3,
    toneMapping: 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7,
    decodeVideoTexture: Boolean,
    decodeVideoTextureEmissive: Boolean,
    premultipliedAlpha: Boolean,
    doubleSided: Boolean,
    flipSided: Boolean,
    useDepthPacking: Boolean,
    depthPacking: 0 | 3200 | 3201 | 3202 | 3203,
    index0AttributeName: js.UndefOr[String],
    extensionClipCullDistance: Boolean,
    extensionMultiDraw: Boolean,
    rendererExtensionParallelShaderCompile: Boolean,
    customProgramCacheKey: String
  ): WebGLProgramParameters =
    js.Dynamic.literal(
      "shaderID" -> shaderID.asInstanceOf[js.Any],
      "shaderType" -> shaderType.asInstanceOf[js.Any],
      "shaderName" -> shaderName.asInstanceOf[js.Any],
      "vertexShader" -> vertexShader.asInstanceOf[js.Any],
      "fragmentShader" -> fragmentShader.asInstanceOf[js.Any],
      "defines" -> defines.asInstanceOf[js.Any],
      "customVertexShaderID" -> customVertexShaderID.asInstanceOf[js.Any],
      "customFragmentShaderID" -> customFragmentShaderID.asInstanceOf[js.Any],
      "isRawShaderMaterial" -> isRawShaderMaterial.asInstanceOf[js.Any],
      "glslVersion" -> glslVersion.asInstanceOf[js.Any],
      "precision" -> precision.asInstanceOf[js.Any],
      "batching" -> batching.asInstanceOf[js.Any],
      "batchingColor" -> batchingColor.asInstanceOf[js.Any],
      "instancing" -> instancing.asInstanceOf[js.Any],
      "instancingColor" -> instancingColor.asInstanceOf[js.Any],
      "instancingMorph" -> instancingMorph.asInstanceOf[js.Any],
      "outputColorSpace" -> outputColorSpace.asInstanceOf[js.Any],
      "alphaToCoverage" -> alphaToCoverage.asInstanceOf[js.Any],
      "map" -> map.asInstanceOf[js.Any],
      "matcap" -> matcap.asInstanceOf[js.Any],
      "envMap" -> envMap.asInstanceOf[js.Any],
      "envMapMode" -> envMapMode.asInstanceOf[js.Any],
      "envMapCubeUVHeight" -> envMapCubeUVHeight.asInstanceOf[js.Any],
      "aoMap" -> aoMap.asInstanceOf[js.Any],
      "lightMap" -> lightMap.asInstanceOf[js.Any],
      "bumpMap" -> bumpMap.asInstanceOf[js.Any],
      "normalMap" -> normalMap.asInstanceOf[js.Any],
      "displacementMap" -> displacementMap.asInstanceOf[js.Any],
      "emissiveMap" -> emissiveMap.asInstanceOf[js.Any],
      "normalMapObjectSpace" -> normalMapObjectSpace.asInstanceOf[js.Any],
      "normalMapTangentSpace" -> normalMapTangentSpace.asInstanceOf[js.Any],
      "packedNormalMap" -> packedNormalMap.asInstanceOf[js.Any],
      "metalnessMap" -> metalnessMap.asInstanceOf[js.Any],
      "roughnessMap" -> roughnessMap.asInstanceOf[js.Any],
      "anisotropy" -> anisotropy.asInstanceOf[js.Any],
      "anisotropyMap" -> anisotropyMap.asInstanceOf[js.Any],
      "clearcoat" -> clearcoat.asInstanceOf[js.Any],
      "clearcoatMap" -> clearcoatMap.asInstanceOf[js.Any],
      "clearcoatNormalMap" -> clearcoatNormalMap.asInstanceOf[js.Any],
      "clearcoatRoughnessMap" -> clearcoatRoughnessMap.asInstanceOf[js.Any],
      "dispersion" -> dispersion.asInstanceOf[js.Any],
      "iridescence" -> iridescence.asInstanceOf[js.Any],
      "iridescenceMap" -> iridescenceMap.asInstanceOf[js.Any],
      "iridescenceThicknessMap" -> iridescenceThicknessMap.asInstanceOf[js.Any],
      "sheen" -> sheen.asInstanceOf[js.Any],
      "sheenColorMap" -> sheenColorMap.asInstanceOf[js.Any],
      "sheenRoughnessMap" -> sheenRoughnessMap.asInstanceOf[js.Any],
      "specularMap" -> specularMap.asInstanceOf[js.Any],
      "specularColorMap" -> specularColorMap.asInstanceOf[js.Any],
      "specularIntensityMap" -> specularIntensityMap.asInstanceOf[js.Any],
      "transmission" -> transmission.asInstanceOf[js.Any],
      "transmissionMap" -> transmissionMap.asInstanceOf[js.Any],
      "thicknessMap" -> thicknessMap.asInstanceOf[js.Any],
      "gradientMap" -> gradientMap.asInstanceOf[js.Any],
      "opaque" -> `opaque`.asInstanceOf[js.Any],
      "alphaMap" -> alphaMap.asInstanceOf[js.Any],
      "alphaTest" -> alphaTest.asInstanceOf[js.Any],
      "alphaHash" -> alphaHash.asInstanceOf[js.Any],
      "combine" -> combine.asInstanceOf[js.Any],
      "mapUv" -> mapUv.asInstanceOf[js.Any],
      "aoMapUv" -> aoMapUv.asInstanceOf[js.Any],
      "lightMapUv" -> lightMapUv.asInstanceOf[js.Any],
      "bumpMapUv" -> bumpMapUv.asInstanceOf[js.Any],
      "normalMapUv" -> normalMapUv.asInstanceOf[js.Any],
      "displacementMapUv" -> displacementMapUv.asInstanceOf[js.Any],
      "emissiveMapUv" -> emissiveMapUv.asInstanceOf[js.Any],
      "metalnessMapUv" -> metalnessMapUv.asInstanceOf[js.Any],
      "roughnessMapUv" -> roughnessMapUv.asInstanceOf[js.Any],
      "anisotropyMapUv" -> anisotropyMapUv.asInstanceOf[js.Any],
      "clearcoatMapUv" -> clearcoatMapUv.asInstanceOf[js.Any],
      "clearcoatNormalMapUv" -> clearcoatNormalMapUv.asInstanceOf[js.Any],
      "clearcoatRoughnessMapUv" -> clearcoatRoughnessMapUv.asInstanceOf[js.Any],
      "iridescenceMapUv" -> iridescenceMapUv.asInstanceOf[js.Any],
      "iridescenceThicknessMapUv" -> iridescenceThicknessMapUv.asInstanceOf[js.Any],
      "sheenColorMapUv" -> sheenColorMapUv.asInstanceOf[js.Any],
      "sheenRoughnessMapUv" -> sheenRoughnessMapUv.asInstanceOf[js.Any],
      "specularMapUv" -> specularMapUv.asInstanceOf[js.Any],
      "specularColorMapUv" -> specularColorMapUv.asInstanceOf[js.Any],
      "specularIntensityMapUv" -> specularIntensityMapUv.asInstanceOf[js.Any],
      "transmissionMapUv" -> transmissionMapUv.asInstanceOf[js.Any],
      "thicknessMapUv" -> thicknessMapUv.asInstanceOf[js.Any],
      "alphaMapUv" -> alphaMapUv.asInstanceOf[js.Any],
      "vertexTangents" -> vertexTangents.asInstanceOf[js.Any],
      "vertexNormals" -> vertexNormals.asInstanceOf[js.Any],
      "vertexColors" -> vertexColors.asInstanceOf[js.Any],
      "vertexAlphas" -> vertexAlphas.asInstanceOf[js.Any],
      "vertexUv1s" -> vertexUv1s.asInstanceOf[js.Any],
      "vertexUv2s" -> vertexUv2s.asInstanceOf[js.Any],
      "vertexUv3s" -> vertexUv3s.asInstanceOf[js.Any],
      "pointsUvs" -> pointsUvs.asInstanceOf[js.Any],
      "fog" -> fog.asInstanceOf[js.Any],
      "useFog" -> useFog.asInstanceOf[js.Any],
      "fogExp2" -> fogExp2.asInstanceOf[js.Any],
      "flatShading" -> flatShading.asInstanceOf[js.Any],
      "sizeAttenuation" -> sizeAttenuation.asInstanceOf[js.Any],
      "logarithmicDepthBuffer" -> logarithmicDepthBuffer.asInstanceOf[js.Any],
      "reverseDepthBuffer" -> reverseDepthBuffer.asInstanceOf[js.Any],
      "skinning" -> skinning.asInstanceOf[js.Any],
      "hasPositionAttribute" -> hasPositionAttribute.asInstanceOf[js.Any],
      "morphTargets" -> morphTargets.asInstanceOf[js.Any],
      "morphNormals" -> morphNormals.asInstanceOf[js.Any],
      "morphColors" -> morphColors.asInstanceOf[js.Any],
      "morphTargetsCount" -> morphTargetsCount.asInstanceOf[js.Any],
      "morphTextureStride" -> morphTextureStride.asInstanceOf[js.Any],
      "numDirLights" -> numDirLights.asInstanceOf[js.Any],
      "numPointLights" -> numPointLights.asInstanceOf[js.Any],
      "numSpotLights" -> numSpotLights.asInstanceOf[js.Any],
      "numSpotLightMaps" -> numSpotLightMaps.asInstanceOf[js.Any],
      "numRectAreaLights" -> numRectAreaLights.asInstanceOf[js.Any],
      "numHemiLights" -> numHemiLights.asInstanceOf[js.Any],
      "numDirLightShadows" -> numDirLightShadows.asInstanceOf[js.Any],
      "numPointLightShadows" -> numPointLightShadows.asInstanceOf[js.Any],
      "numSpotLightShadows" -> numSpotLightShadows.asInstanceOf[js.Any],
      "numSpotLightShadowsWithMaps" -> numSpotLightShadowsWithMaps.asInstanceOf[js.Any],
      "numLightProbes" -> numLightProbes.asInstanceOf[js.Any],
      "numLightProbeGrids" -> numLightProbeGrids.asInstanceOf[js.Any],
      "numClippingPlanes" -> numClippingPlanes.asInstanceOf[js.Any],
      "numClipIntersection" -> numClipIntersection.asInstanceOf[js.Any],
      "dithering" -> dithering.asInstanceOf[js.Any],
      "shadowMapEnabled" -> shadowMapEnabled.asInstanceOf[js.Any],
      "shadowMapType" -> shadowMapType.asInstanceOf[js.Any],
      "toneMapping" -> toneMapping.asInstanceOf[js.Any],
      "decodeVideoTexture" -> decodeVideoTexture.asInstanceOf[js.Any],
      "decodeVideoTextureEmissive" -> decodeVideoTextureEmissive.asInstanceOf[js.Any],
      "premultipliedAlpha" -> premultipliedAlpha.asInstanceOf[js.Any],
      "doubleSided" -> doubleSided.asInstanceOf[js.Any],
      "flipSided" -> flipSided.asInstanceOf[js.Any],
      "useDepthPacking" -> useDepthPacking.asInstanceOf[js.Any],
      "depthPacking" -> depthPacking.asInstanceOf[js.Any],
      "index0AttributeName" -> index0AttributeName.asInstanceOf[js.Any],
      "extensionClipCullDistance" -> extensionClipCullDistance.asInstanceOf[js.Any],
      "extensionMultiDraw" -> extensionMultiDraw.asInstanceOf[js.Any],
      "rendererExtensionParallelShaderCompile" -> rendererExtensionParallelShaderCompile.asInstanceOf[js.Any],
      "customProgramCacheKey" -> customProgramCacheKey.asInstanceOf[js.Any]
    ).asInstanceOf[WebGLProgramParameters]
