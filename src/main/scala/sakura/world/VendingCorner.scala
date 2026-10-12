package sakura.world

import sakura.*
import vendor.three.*

import scala.collection.mutable
import scala.math
import scala.scalajs.js

/** The street-side vending corner: two drink machines with their painted, forward-standing
  * display fronts, a recycling bin and a corrugated canopy on back posts, all on one concrete
  * pad.
  *
  * The body, pad, bin and posts are cel-shaded through the shared [[CelBatch]]; the canopy's
  * corrugation is geometric — ridge strips whose facets carry the cel bands — because the
  * scene's draw-call budget is spent where only a texture can go. The one plain mesh is the two
  * merged display fronts: their canvas texture is the point, and a backlit face must not be
  * quantised into shadow.
  */
object VendingCorner:

  final private[sakura] case class Plan(centerX: Double, centerZ: Double, facing: Double, baseY: Double)

  private[sakura] def plan(
    centerX: Double,
    centerZ: Double,
    facing: Double,
    groundAt: (Double, Double) => Double
  ): Plan =
    Plan(centerX, centerZ, facing, groundAt(centerX, centerZ))

  val FrontKey: String = "vending-front"

  private val machineWidth  = 1.22
  private val machineHeight = 1.92
  private val machineDepth  = 0.75
  private val machineOffset = 0.65
  private val padTop        = 0.15

  /** Adds one corner seated at `(centerX, centerZ)` with its fronts rotated `facing` radians
    * from +z.
    *
    * @return the merged display-front mesh and its resources.
    */
  private[sakura] def addTo(
    batch: CelBatch,
    plan: Plan,
    curvature: PlanetCurvature,
    textures: TextureCache
  ): (List[Mesh[?, ?, ?]], List[SceneResource]) =
    val centerX = plan.centerX
    val centerZ = plan.centerZ
    val facing  = plan.facing
    val place   = WorldPlacer(batch, centerX, plan.baseY, centerZ, facing)

    place(new BoxGeometry(4.7, 0.8, 2.3), Palette.concrete, (0.0, padTop - 0.4, 0.0))
    List(-machineOffset, machineOffset).foreach { offsetX =>
      place(new BoxGeometry(machineWidth, machineHeight, machineDepth), Palette.plaster,
        (offsetX, padTop + machineHeight / 2.0, 0.0))
    }

    val binX = -machineOffset - machineWidth / 2.0 - 0.62
    place(new BoxGeometry(0.62, 1.05, 0.6), Palette.roofTile, (binX, padTop + 0.525, -0.05))
    place(new BoxGeometry(0.64, 0.18, 0.62), Palette.plaster, (binX, padTop + 0.95, -0.05))

    canopy(place)

    val frontTexture = textures.get(FrontKey)
    // One wide plate carries both faces: the texture repeats horizontally so each machine reads
    // the same sign-window-slot design, and the corner spends one draw on both fronts.
    frontTexture.wrapS = 1000
    frontTexture.repeat.set(2.0, 1.0)
    val frontGeometry = new BoxGeometry(2.0 * machineOffset + machineWidth, machineHeight - 0.14, 0.05)
    frontGeometry.translate(0.0, padTop + machineHeight / 2.0, machineDepth / 2.0 + 0.03)
    frontGeometry.rotateY(facing)
    frontGeometry.translate(centerX, plan.baseY, centerZ)
    bendToPlanet(frontGeometry, curvature)

    val frontMaterial = new MeshBasicMaterial(
      MeshBasicMaterialParameters(map = js.defined(frontTexture))
    )
    val frontMesh = new Mesh(frontGeometry, frontMaterial)
    (List(frontMesh), List(SceneResource.Geometry(frontGeometry), SceneResource.Material(frontMaterial)))
  end addTo

  /** The corrugated canopy: a slab and its ridge strips, tilted to drain, on two back posts. */
  private def canopy(place: WorldPlacer): Unit =
    place(new BoxGeometry(4.5, 0.07, 1.2), Palette.concrete, (0.0, 2.42, 0.12), tilt = -0.06)
    (0 until 7).foreach { ridge =>
      val x = -1.95 + ridge * 0.65
      place(new BoxGeometry(0.22, 0.1, 1.2), Palette.concrete, (x, 2.46, 0.12), tilt = -0.06)
    }
    List(-1.95, 1.95).foreach { offsetX =>
      place(new BoxGeometry(0.1, 2.42, 0.1), Palette.darkTimber, (offsetX, padTop + 1.21, -0.45))
    }

  /** Displaces world-space vertices by the planet bend, for the one plain mesh the corner owns. */
  private def bendToPlanet(geometry: BufferGeometry[?, ?], curvature: PlanetCurvature): Unit =
    val position = geometry.getAttribute("position") match
      case attribute: BufferAttribute[?] => attribute
      case _ =>
        throw new IllegalStateException("sakura-crossing: a vending front has no position attribute")
    (0 until position.count.toInt).foreach { index =>
      val x = position.getX(index)
      val y = position.getY(index)
      val z = position.getZ(index)
      position.setXYZ(index, x, y + curvature.bend(x, 0.0, z), z)
    }
    ThreeInterop.markForUpload(position)
end VendingCorner
