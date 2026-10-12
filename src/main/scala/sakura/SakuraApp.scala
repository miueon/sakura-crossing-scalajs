package sakura

import com.raquo.laminar.api.L.*
import org.scalajs.dom
import sakura.app.{
  CameraController, CameraPreset, HudComponents, PlayerController, PlayerInput, PlayerPosition, PlayerState
}
import sakura.core.{Outline, Pal, Sky, Textures}
import sakura.world.Planet
import sakura.world.WorldIndex
import vendor.three.*

import scala.math
import scala.scalajs.js

/** Page shell for the crossing.
  *
  * The renderer host owns scene construction, the render loop and GPU disposal; all three are
  * bound to the host element's Laminar mount/unmount lifecycle so a re-render cannot leak a
  * canvas, a WebGL context or a running frame callback.
  *
  * Everything inside is upstream `src/main.js`: one `buildWorld`, a four-light rig seated into
  * the player's local surface frame every frame, distance fog, and the post pipeline.
  */
object SakuraApp:

  /** Sun, fill and bounce directions, expressed in the player's local surface frame. */
  private val SunLocal    = (-52.0, 62.0, 56.0)
  private val FillLocal   = (48.0, 26.0, -44.0)
  private val BounceLocal = (10.0, -18.0, 40.0)
  private val BounceColor = 0xd8cbe8

  private val FogNear = 44.0
  private val FogFar  = 205.0

  def apply(): HtmlElement =
    val playerState =
      try Var(PlayerController.initial(CameraPreset.Crossing))
      catch
        case error: Throwable =>
          error.printStackTrace()
          throw error
    val sceneMounted = Var(true)
    val sceneHost    = rendererHost(playerState)

    div(
      className := "scene",
      child(sceneHost) <-- sceneMounted.signal,
      HudComponents(
        playerState.signal,
        sceneMounted.signal,
        preset => playerState.update(PlayerController.select(_, preset)),
        () => sceneMounted.invert()
      )
    )
  end apply

  private def rendererHost(playerState: Var[PlayerState]): HtmlElement =
    div(
      className := "canvas-host",
      onMountUnmountCallbackWithState(
        mount = context =>
          try attachScene(context.thisNode.ref, playerState)
          catch
            case error: Throwable =>
              error.printStackTrace()
              throw error,
        unmount = (_, maybeHandle) => maybeHandle.foreach(_.dispose())
      )
    )

  final private class SceneHandle(
    renderer: WebGLRenderer,
    pipeline: PostPipeline,
    cancelLoop: () => Unit
  ):
    def dispose(): Unit =
      cancelLoop()
      pipeline.dispose()
      renderer.dispose()
      val canvas = renderer.domElement
      if canvas.parentNode != null then canvas.parentNode.removeChild(canvas)

  private def attachScene(host: dom.HTMLElement, playerState: Var[PlayerState]): SceneHandle =
    val width  = if host.clientWidth > 0 then host.clientWidth.toDouble else 1280.0
    val height = if host.clientHeight > 0 then host.clientHeight.toDouble else 720.0

    val scene  = new Scene()
    val camera = new PerspectiveCamera(46.0, width / height, 0.25, 600.0)
    camera.rotation.order = "YXZ"
    scene.fog = new Fog(Pal.fog.toDouble, FogNear, FogFar)

    val renderer = new WebGLRenderer(
      WebGLRendererParameters(antialias = Some(js.defined(false)), stencil = Some(js.defined(false)))
    )
    renderer.setPixelRatio(1.0)
    renderer.outputColorSpace = SRGBColorSpace
    renderer.toneMapping = NoToneMapping.toInt
    renderer.shadowMap.enabled = true
    renderer.shadowMap.`type` = PCFShadowMap.toInt
    renderer.setClearColor(Pal.fog.toDouble, 1.0)

    val (sun, fill, bounce, hemi) = lightRig(scene)
    val sky                       = Sky.buildSky(scene, 500.0, () => Textures.cloudTex())
    val world                     = WorldIndex.buildWorld(scene)

    Outline.setOutlineResolution(width, height)

    val pipeline = new PostPipeline(renderer, scene, camera)
    pipeline.setSize(width, height)
    host.appendChild(renderer.domElement)

    /** Seats the rig into the surface frame at a flat point, exactly as upstream `main.js` does:
      * the lighting is pinned to the local surface rather than to world space.
      */
    def seatRig(x: Double, z: Double): Unit =
      val basis  = Planet.basisAt(x, z)
      val origin = Planet.positionAt(x, 0.0, z)
      Outline.seatLight(sun, SunLocal, basis, origin)
      Outline.seatLight(fill, FillLocal, basis, origin)
      Outline.seatLight(bounce, BounceLocal, basis, origin)
      hemi.position.copy(ThreeInterop.asVector3Like(basis.up))

    def renderAt(nextState: PlayerState, elapsed: Double): Unit =
      val eyeY = PlayerController.eyeY(nextState, elapsed)
      CameraController.place(camera, nextState.pose, eyeY)
      seatRig(nextState.position.x, nextState.position.z)
      // the sky dome is centred on the flat origin, so it has to trail the camera
      sky.dome.position.copy(ThreeInterop.asVector3Like(camera.position))
      sky.clouds.position.copy(ThreeInterop.asVector3Like(camera.position))
      pipeline.render()

    var elapsed: Double  = 0.0
    var lastTime: Double = -1.0
    var animId: Int      = 0
    var running: Boolean = true
    var input            = PlayerInput.empty

    val onKeyDown: js.Function1[dom.KeyboardEvent, Unit] = event =>
      CameraPreset.fromKey(event.key) match
        case Some(preset) if !event.repeat =>
          event.preventDefault()
          playerState.update(PlayerController.select(_, preset))
        case Some(_) => ()
        case None =>
          input = PlayerInput.update(input, event.code, isPressed = true)
          if PlayerInput.isMovementCode(event.code) then event.preventDefault()
    val onKeyUp: js.Function1[dom.KeyboardEvent, Unit] = event =>
      input = PlayerInput.update(input, event.code, isPressed = false)
      if PlayerInput.isMovementCode(event.code) then event.preventDefault()
    val onBlur: js.Function1[dom.Event, Unit] = _ => input = PlayerInput.empty

    dom.window.addEventListener("keydown", onKeyDown)
    dom.window.addEventListener("keyup", onKeyUp)
    dom.window.addEventListener("blur", onBlur)
    val onResize: js.Function1[dom.Event, Unit] = _ =>
      val resizedWidth  = if host.clientWidth > 0 then host.clientWidth.toDouble else 1280.0
      val resizedHeight = if host.clientHeight > 0 then host.clientHeight.toDouble else 720.0
      camera.aspect = resizedWidth / resizedHeight
      camera.updateProjectionMatrix()
      pipeline.setSize(resizedWidth, resizedHeight)
      Outline.setOutlineResolution(resizedWidth, resizedHeight)
    dom.window.addEventListener("resize", onResize)

    def frame(timestamp: Double): Unit =
      if running then
        val dt = if lastTime < 0.0 then 0.0 else math.min((timestamp - lastTime) / 1000.0, 1.0 / 20.0)
        lastTime = timestamp
        elapsed += dt
        val currentState = playerState.now()
        val nextState = PlayerController.step(currentState, input, dt,
          (x, z, fromY) => world.heightAt(x, z, Some(fromY)))
        if nextState != currentState then playerState.set(nextState)
        world.update(dt)
        renderAt(nextState, elapsed)
        animId = dom.window.requestAnimationFrame((t: Double) => frame(t))

    CameraController.place(camera, playerState.now().pose, PlayerController.eyeY(playerState.now(), 0.0))
    pipeline.render()
    animId = dom.window.requestAnimationFrame((t: Double) => frame(t))
    installDevShot(playerState, world, camera, pipeline, (sun, fill, bounce, hemi), () => running = false)

    new SceneHandle(
      renderer = renderer,
      pipeline = pipeline,
      cancelLoop = () =>
        running = false
        if animId != 0 then dom.window.cancelAnimationFrame(animId)
        dom.window.removeEventListener("keydown", onKeyDown)
        dom.window.removeEventListener("keyup", onKeyUp)
        dom.window.removeEventListener("blur", onBlur)
        dom.window.removeEventListener("resize", onResize)
    )
  end attachScene

  /** The classic two-light anime rig: a warm quantised key with a snapped shadow camera, a cool
    * bounce from the opposite quarter that carries most of the shadow side, a weak second bounce
    * from below so undersides never go flat, and a hemisphere with a violet ground colour.
    */
  private def lightRig(
    scene: Scene[Object3DEventMap]
  ): (DirectionalLight, DirectionalLight, DirectionalLight, HemisphereLight) =
    val sun = new DirectionalLight(Pal.sun.toDouble, 2.25)
    sun.castShadow = true
    sun.shadow.mapSize.set(2048.0, 2048.0)
    sun.shadow.camera.left = -34.0
    sun.shadow.camera.right = 34.0
    sun.shadow.camera.top = 34.0
    sun.shadow.camera.bottom = -34.0
    sun.shadow.camera.near = 1.0
    sun.shadow.camera.far = 200.0
    sun.shadow.camera.updateProjectionMatrix()
    sun.shadow.bias = -0.0004
    sun.shadow.normalBias = 0.035
    ThreeInterop.addChild(scene, sun)
    ThreeInterop.addChild(scene, sun.target)

    val fill = new DirectionalLight(Pal.fill.toDouble, 1.08)
    ThreeInterop.addChild(scene, fill)
    ThreeInterop.addChild(scene, fill.target)

    val bounce = new DirectionalLight(BounceColor.toDouble, 0.34)
    ThreeInterop.addChild(scene, bounce)
    ThreeInterop.addChild(scene, bounce.target)

    val hemi = new HemisphereLight(Pal.hemiSky.toDouble, Pal.hemiGround.toDouble, 1.12)
    ThreeInterop.addChild(scene, hemi)
    (sun, fill, bounce, hemi)
  end lightRig

  /** Deterministic capture hook for the parity gate, the same helper upstream keeps behind
    * `import.meta.env.DEV`: place the player, the consist and the crossing where the shot wants
    * them, seat the rig on the resulting surface frame, render once, and -- with `freeze` -- stop
    * the loop so the frame the caller reads is the frame that was rendered.
    */
  private def installDevShot(
    playerState: Var[PlayerState],
    world: WorldIndex.World,
    camera: PerspectiveCamera,
    pipeline: PostPipeline,
    rig: (DirectionalLight, DirectionalLight, DirectionalLight, HemisphereLight),
    freeze: () => Unit
  ): Unit =
    def optional(options: js.Dictionary[js.Any], key: String): Option[js.Any] =
      options.get(key).filter(value => !js.isUndefined(value) && value != null)

    def number(options: js.Dictionary[js.Any], key: String): Option[Double] =
      optional(options, key).map(_.asInstanceOf[Double])

    /* Upstream's own option names are accepted alongside the scalars: its capture harness calls the
     * hook with `pos: [x, y, z]` and `scale`, so a reference frame can be re-requested here without
     * a translation step. `grade` is deliberately not accepted -- upstream stores it on an inert
     * flag that `render` never reads, so honouring it would produce frames upstream cannot render.
     */
    def positionOf(options: js.Dictionary[js.Any]): Option[(Double, Double)] =
      optional(options, "pos") match
        case Some(value) =>
          val point = value.asInstanceOf[js.Array[Double]]
          Some((point(0), point(2)))
        case None => number(options, "x").zip(number(options, "z"))

    val (sun, fill, bounce, hemi) = rig
    val shot: js.Function4[String, Double, Double, js.Dictionary[js.Any], Unit] =
      (_, shotWidth, shotHeight, options) =>
        positionOf(options).foreach { (x, z) =>
          val state = playerState.now()
          playerState.set(state.copy(
            position = PlayerPosition(x, world.heightAt(x, z), z),
            pose = state.pose.copy(x = x, z = z),
            velocityX = 0.0,
            velocityZ = 0.0
          ))
        }
        number(options, "yaw").foreach(yaw =>
          playerState.update(state => state.copy(pose = state.pose.copy(yaw = yaw)))
        )
        number(options, "pitch").foreach(pitch =>
          playerState.update(state => state.copy(pose = state.pose.copy(pitch = pitch)))
        )
        number(options, "trainX").foreach { trainX =>
          world.train.x = trainX
          world.train.update(0.0)
        }
        number(options, "armT").foreach { armT =>
          world.crossing.setArms(armT)
          world.crossing.setLamps(armT > 0.02, number(options, "blink").getOrElse(0.0))
        }

        optional(options, "ink").foreach(value => pipeline.isInkEnabled = value.asInstanceOf[Boolean])
        pipeline.forceScale = Some(number(options, "scale").getOrElse(1.0))

        camera.aspect = shotWidth / shotHeight
        camera.updateProjectionMatrix()
        // setSize owns the render targets, the ink weight and the outline resolution for the size it
        // settles on, so a shot must not second-guess any of them.
        pipeline.setSize(shotWidth, shotHeight)

        val seated = playerState.now()
        val basis  = Planet.basisAt(seated.position.x, seated.position.z)
        val origin = Planet.positionAt(seated.position.x, 0.0, seated.position.z)
        Outline.seatLight(sun, SunLocal, basis, origin)
        Outline.seatLight(fill, FillLocal, basis, origin)
        Outline.seatLight(bounce, BounceLocal, basis, origin)
        hemi.position.copy(ThreeInterop.asVector3Like(basis.up))
        CameraController.place(camera, seated.pose, PlayerController.eyeY(seated, 0.0))
        pipeline.render()

        if optional(options, "freeze").exists(_.asInstanceOf[Boolean]) then freeze()

    dom.window.asInstanceOf[js.Dynamic].updateDynamic("__shot")(shot)
  end installDevShot
end SakuraApp
