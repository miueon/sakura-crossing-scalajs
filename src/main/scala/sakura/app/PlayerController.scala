package sakura.app

import sakura.world.Street

import scala.math

final private[sakura] case class PlayerPosition(x: Double, y: Double, z: Double)

private[sakura] enum LocomotionMode:
  case Walking, Swimming

  def label: String = this match
    case Walking  => "Walking"
    case Swimming => "Swimming"

private[sakura] enum PlayerKey:
  case W, A, S, D
  case ArrowUp, ArrowLeft, ArrowDown, ArrowRight
  case ShiftLeft, ShiftRight

private[sakura] object PlayerKey:
  def fromCode(code: String): Option[PlayerKey] =
    code match
      case "KeyW"       => Some(W)
      case "KeyA"       => Some(A)
      case "KeyS"       => Some(S)
      case "KeyD"       => Some(D)
      case "ArrowUp"    => Some(ArrowUp)
      case "ArrowLeft"  => Some(ArrowLeft)
      case "ArrowDown"  => Some(ArrowDown)
      case "ArrowRight" => Some(ArrowRight)
      case "ShiftLeft"  => Some(ShiftLeft)
      case "ShiftRight" => Some(ShiftRight)
      case _            => None

final private[sakura] case class PlayerInput private[sakura] (pressed: Set[PlayerKey])

private[sakura] object PlayerInput:
  val empty: PlayerInput = PlayerInput(Set.empty)

  def update(input: PlayerInput, code: String, isPressed: Boolean): PlayerInput =
    PlayerKey.fromCode(code) match
      case Some(key) if isPressed  => input.copy(pressed = input.pressed + key)
      case Some(key) if !isPressed => input.copy(pressed = input.pressed - key)
      case _                       => input

  def isMovementCode(code: String): Boolean =
    PlayerKey.fromCode(code).exists {
      case PlayerKey.ShiftLeft | PlayerKey.ShiftRight => false
      case _                                          => true
    }

/** Where the player is and which way they face, in the flat authoring frame. `pose.lift` is how
  * far above the local ground the eye sits, which is how a platform or a bridge deck is
  * expressed: walking anywhere flattens it back to the ground.
  */
final private[sakura] case class PlayerState(
  position: PlayerPosition,
  pose: CameraPose,
  velocityX: Double,
  velocityZ: Double,
  activePreset: CameraPreset,
  locomotion: LocomotionMode,
  viewRevision: Long
)

private[sakura] object PlayerController:
  private val SwimEyeHeight        = 1.15
  private val SwimBobAmplitude     = 0.08
  private val WalkSpeed            = 2.55
  private val SprintSpeed          = 5.1
  private val SwimSpeed            = 1.25
  private val Acceleration         = 10.0
  private val SeaLevel             = 0.0
  private val CanalCenterZ         = -24.0
  private val CanalHalfWidth       = 2.5
  private val CanalWaterOffset     = 1.15
  private val CanalStartX          = -98.0
  private val CanalEndX            = 106.0
  private val MinimumReleasedSpeed = 0.005

  private val CanalWaterY = Street.groundY(CanalCenterZ) - CanalWaterOffset

  def initial(preset: CameraPreset): PlayerState =
    val pose = preset.pose
    PlayerState(
      position = PlayerPosition(pose.x, Street.groundY(pose.z), pose.z),
      pose = pose,
      velocityX = 0.0,
      velocityZ = 0.0,
      activePreset = preset,
      locomotion = locomotionAt(pose.x, pose.z),
      viewRevision = 0L
    )

  def select(current: PlayerState, preset: CameraPreset): PlayerState =
    initial(preset).copy(viewRevision = current.viewRevision + 1L)

  /** One step of movement, with the ground supplied by the world rather than assumed: `groundAt`
    * takes the flat point and the height the query is made from, so a platform is only reachable
    * when the player is already within a step of it.
    */
  def step(
    state: PlayerState,
    input: PlayerInput,
    deltaSeconds: Double,
    groundAt: (Double, Double, Double) => Double
  ): PlayerState =
    val delta = math.max(0.0, deltaSeconds)
    val forwardAmount = pressed(input, PlayerKey.W, PlayerKey.ArrowUp) -
      pressed(input, PlayerKey.S, PlayerKey.ArrowDown)
    val strafeAmount = pressed(input, PlayerKey.D, PlayerKey.ArrowRight) -
      pressed(input, PlayerKey.A, PlayerKey.ArrowLeft)
    val (forwardX, forwardZ) = CameraController.forward(state.pose)
    val wants                = forwardAmount != 0.0 || strafeAmount != 0.0

    val desiredX       = forwardX * forwardAmount - forwardZ * strafeAmount
    val desiredZ       = forwardZ * forwardAmount + forwardX * strafeAmount
    val desiredLength  = math.hypot(desiredX, desiredZ)
    val directionScale = if desiredLength > 1.0 then 1.0 / desiredLength else 1.0
    val speed =
      state.locomotion match
        case LocomotionMode.Swimming                                                               => SwimSpeed
        case LocomotionMode.Walking if isPressed(input, PlayerKey.ShiftLeft, PlayerKey.ShiftRight) => SprintSpeed
        case LocomotionMode.Walking                                                                => WalkSpeed
    val wantedVelocityX = desiredX * directionScale * speed
    val wantedVelocityZ = desiredZ * directionScale * speed
    val response        = 1.0 - math.exp(-Acceleration * delta)
    val rawVelocityX    = state.velocityX + (wantedVelocityX - state.velocityX) * response
    val rawVelocityZ    = state.velocityZ + (wantedVelocityZ - state.velocityZ) * response
    val velocityX       = if !wants && math.abs(rawVelocityX) < MinimumReleasedSpeed then 0.0 else rawVelocityX
    val velocityZ       = if !wants && math.abs(rawVelocityZ) < MinimumReleasedSpeed then 0.0 else rawVelocityZ

    val nextX        = state.position.x + velocityX * delta
    val nextZ        = state.position.z + velocityZ * delta
    val waterSurface = surfaceAt(nextX, nextZ)
    val nextY        = waterSurface.getOrElse(groundAt(nextX, nextZ, state.position.y))
    val nextMode =
      if waterSurface.exists(_ <= SeaLevel) then LocomotionMode.Swimming else LocomotionMode.Walking
    val nextState = state.copy(
      position = PlayerPosition(nextX, nextY, nextZ),
      /* A preset may seat the eye on a deck or a platform; the moment the player walks, they are
       * standing on the ground again. */
      pose = state.pose.copy(x = nextX, z = nextZ, lift = if wants then 0.0 else state.pose.lift),
      velocityX = velocityX,
      velocityZ = velocityZ,
      locomotion = nextMode
    )

    if nextState == state then state else nextState
  end step

  /** The height the camera sits at, given the world's answer for the ground under the player. */
  def eyeY(state: PlayerState, elapsedSeconds: Double): Double =
    val eyeHeight = state.locomotion match
      case LocomotionMode.Walking => CameraPreset.EyeHeight
      case LocomotionMode.Swimming =>
        SwimEyeHeight + SwimBobAmplitude * math.sin(3.0 * elapsedSeconds)
    state.position.y + eyeHeight + state.pose.lift

  private def locomotionAt(x: Double, z: Double): LocomotionMode =
    if surfaceAt(x, z).exists(_ <= SeaLevel) then LocomotionMode.Swimming else LocomotionMode.Walking

  private def surfaceAt(x: Double, z: Double): Option[Double] =
    Option.when(x > CanalStartX && x < CanalEndX && math.abs(z - CanalCenterZ) <= CanalHalfWidth)(CanalWaterY)

  private def pressed(input: PlayerInput, first: PlayerKey, second: PlayerKey): Double =
    if isPressed(input, first, second) then 1.0 else 0.0

  private def isPressed(input: PlayerInput, first: PlayerKey, second: PlayerKey): Boolean =
    input.pressed.contains(first) || input.pressed.contains(second)
end PlayerController
