package sakura

import sakura.world.SkyDome

class UltravioletShaderSuite extends munit.FunSuite:

  private val curvature = PlanetCurvature.Default

  test("bendChunk carries the curvature's own radius into the compiled GLSL") {
    val chunk = PlanetCurvature(PlanetRadius(312.5)).bendChunk
    assert(chunk.contains("const float uPlanetRadius = 312.5;"))
    assert(chunk.contains("bendToPlanet"))
    assert(chunk != PlanetCurvature(PlanetRadius(250.0)).bendChunk)
  }

  test("the painted sky shader emits its palette and soft 26-band gradient") {
    assert(SkyDome.skyVertexShader.contains("vWorld"))
    val fragment = SkyDome.skyFragmentShader
    List(
      glslVec3Const("uTop", 0x74a8dc),
      glslVec3Const("uMid", 0xb5d4ee),
      glslVec3Const("uHaze", 0xf6e4d4),
      glslFloatConst("uBands", 26.0)
    ).foreach(constant => assert(fragment.contains(constant), s"sky shader lost $constant"))
    List("floor(", "smoothstep(", "mix(").foreach(operation =>
      assert(fragment.contains(operation), s"sky shader lost $operation")
    )
  }

  test("every migrated stage keeps a main entrypoint and declares no three.js builtin") {
    val ramp    = ToonMaterial.CelRamp(0.6, 0.15, 0.65, 0.35, 0.35)
    val outline = Outline.outlineMaterial(0.12, curvature)
    val stages = List(
      "cel vertex"           -> ToonMaterial.celVertexShader(curvature, instanced = false),
      "instanced cel vertex" -> ToonMaterial.celVertexShader(curvature, instanced = true),
      "cel fragment"         -> ToonMaterial.celFragmentShader(0xff0000, ramp),
      "outline vertex"       -> outline.vertexShader,
      "outline fragment"     -> outline.fragmentShader,
      "flat vertex"          -> ToonMaterial.flatVertexShader(curvature),
      "flat fragment"        -> ToonMaterial.flatFragmentShader(0xff0000)
    )
    stages.foreach { (stage, source) =>
      assert(source.contains("void main()"), s"$stage lost its main entrypoint")
      List(
        "uniform mat4 modelMatrix;",
        "uniform mat4 modelViewMatrix;",
        "uniform mat4 projectionMatrix;",
        "uniform mat4 viewMatrix;",
        "uniform mat3 normalMatrix;",
        "attribute vec3 position;",
        "attribute vec3 normal;",
        "attribute vec2 uv;",
        "attribute mat4 instanceMatrix;"
      ).foreach(declaration => assert(!source.contains(declaration), s"$stage redeclares $declaration"))
    }
  }

  test("the cel fragment source keeps three band thresholds and a violet-shifted shadow") {
    val source = ToonMaterial.celFragmentShader(0xff0000, ToonMaterial.propRamp)
    List("uHighlightFloor", "uMidtoneFloor", "uShadowFactor", "uShadowTint").foreach { constant =>
      assert(source.contains(constant), s"$constant dropped out of the ramp")
    }
    assert(source.contains("mix("), "the shadow band must mix, not multiply")
    assert(source.contains("uShadowViolet"), "the shadow mix lost its violet target")
    assert(source.contains("vNormal"), "the fragment must read the vertex normal varying")
  }

  test("runtime colours and ramps reach the source instead of freezing at compile time") {
    val ramp      = ToonMaterial.CelRamp(0.6, 0.15, 0.65, 0.35, 0.35)
    val reference = ToonMaterial.celFragmentShader(0xff0000, ramp)
    assert(reference.contains("const vec3 uBaseColor = vec3(1.0, 0.0, 0.0);"))
    assert(reference.contains("const float uHighlightFloor = 0.6;"))
    assert(ToonMaterial.celFragmentShader(0x00ff00, ramp) != reference)
    assert(ToonMaterial.celFragmentShader(0xff0000, ToonMaterial.CelRamp(0.9, 0.15, 0.65, 0.35, 0.35)) != reference)
    val approach = ToonMaterial.celFragmentShader(0xff0000, ramp, (-0.52, 0.62, -0.56))
    assert(approach.contains("const vec3 uSunDirection = vec3(-0.52, 0.62, -0.56);"))
    assert(approach != reference)
  }

  test("only the instanced vertex variant touches instanceMatrix") {
    val plain     = ToonMaterial.celVertexShader(curvature, instanced = false)
    val instanced = ToonMaterial.celVertexShader(curvature, instanced = true)
    assert(plain.contains("bendToPlanet"))
    assert(plain.contains("vNormal"))
    assert(!plain.contains("instanceMatrix"))
    assert(instanced.contains("instanceMatrix"))
  }

  test("the outline extrusion keeps its uniforms") {
    val outline = Outline.outlineMaterial(0.12, curvature)
    assert(outline.vertexShader.contains("uniform float uThickness;"))
    assert(ToonMaterial.celVertexShader(PlanetCurvature(PlanetRadius(400.0)), instanced = false)
      .contains("const float uPlanetRadius = 400.0;"))
  }
end UltravioletShaderSuite
