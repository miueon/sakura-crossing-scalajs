package sakura.world

import sakura.ThreeInterop
import sakura.core.{Pal, Textures, Toon, Util}
import vendor.three.*

import scala.collection.mutable

/** 青空商店 -- the corner shop by the crossing, ported 1:1 from upstream
  * `src/world/shop.js`.
  *
  * Red-and-white striped awning, a half-closed shutter, a wall of posters,
  * stacked drink crates and two vending machines: the cluttered, colourful
  * anchor of the middle ground.  All the clutter lives on the pavement strip
  * so the frame stays organised rather than merely busy.
  */
object Shop:

  final case class ShutterState(
    group: Group[Object3DEventMap],
    rail: Mesh[?, ?, ?],
    sh: Double,
    shTop: Double,
    w: Double,
    zc: Double
  )

  final class ShopState(shutter: ShutterState):
    var shutterOpen: Double   = 0.0
    var shutterTarget: Double = 0.0
    private val anims         = mutable.ArrayBuffer.empty[Double => Boolean]

    def toggleShutter(): Unit =
      shutterTarget = if shutterTarget > 0.5 then 0.0 else 1.0

    def dispense(machine: Vending.VendingMachine): Unit =
      anims += machine.dispense()

    def update(dt: Double): Unit =
      val k = 1.0 - math.exp(-dt * 2.6)
      shutterOpen += (shutterTarget - shutterOpen) * k
      // the curtain rolls up into the drum: top edge pinned, height shrinks
      val sy = math.max(0.08, 1.0 - shutterOpen * 0.92)
      shutter.group.scale.y = sy
      shutter.group.position.y = shutter.shTop - (shutter.sh * sy) / 2.0
      shutter.rail.position.y = shutter.shTop - shutter.sh * sy + 0.05
      anims.filterInPlace(step => !step(dt))
      ()
  end ShopState

  final case class ShopResult(
    group: Group[Object3DEventMap],
    xFront: Double,
    zNear: Double,
    zFar: Double,
    walkY: Double,
    shopState: ShopState
  )

  def buildShop(ctx: WorldCtx): ShopResult =
    val rng = Util.rngKit(3311)
    val g   = new Group()
    g.name = "shop"
    ctx.add(g)

    val zNear  = 4.55
    val zFar   = 12.6
    val zMid   = (zNear + zFar) / 2.0
    val xFront = Street.centerX(zMid) + Street.RoadHalf + Street.WalkW + 0.12
    val depth  = 7.2
    val y0     = Street.groundY(zMid)

    val matWall      = Toon.cel(Toon.CelOptions(color = Pal.wallCream, bands = 3, tint = 0x6f6790))
    val matWall2     = Toon.cel(Toon.CelOptions(color = Pal.wallWhite, bands = 3, tint = 0x6f6790))
    val matTile      = Toon.cel(Toon.CelOptions(color = 0xe9edf0, bands = 3, tint = 0x6f6790))
    val matRoof      = Toon.cel(Toon.CelOptions(color = Pal.roofSlate, bands = 3, tint = 0x544e74))
    val matRed       = Toon.cel(Toon.CelOptions(color = Pal.red, bands = 3, tint = 0x7a4060))
    val matRedSoft   = Toon.cel(Toon.CelOptions(color = Pal.redSoft, bands = 3, tint = 0x7a4060))
    val matMetal     = Toon.cel(Toon.CelOptions(color = Pal.metal, bands = 3, tint = 0x666090))
    val matMetalDark = Toon.cel(Toon.CelOptions(color = Pal.metalDark, bands = 3, tint = 0x5c5680))
    val matGlass = Toon.flat(Toon.FlatOptions(color = Pal.glass, transparent = true,
      opacity = 0.5, depthWrite = Some(false), cache = false))

    val h1 = 3.15 // ground floor height
    val h2 = 2.75 // upper floor

    /* ------------------------------ main volume ------------------------------ */
    val body = Util.box(depth, h1, zFar - zNear, matWall, xFront + depth / 2.0, y0 + h1 / 2.0, zMid)
    body.castShadow = true
    body.receiveShadow = true
    ThreeInterop.addChild(g, body)
    sakura.core.Outline.hullOutline(body, sakura.core.Outline.OutlineOptions(thickness = 0.0032))

    // upper storey, set back a little from the shopfront
    val body2 = Util.box(depth - 0.5, h2, zFar - zNear - 0.3, matWall2,
      xFront + 0.35 + (depth - 0.5) / 2.0, y0 + h1 + h2 / 2.0, zMid)
    body2.castShadow = true
    body2.receiveShadow = true
    ThreeInterop.addChild(g, body2)
    sakura.core.Outline.hullOutline(body2, sakura.core.Outline.OutlineOptions(thickness = 0.0032))

    // parapet + roof cap + string course between floors
    ThreeInterop.addChild(g,
      Util.box(depth - 0.4, 0.22, zFar - zNear - 0.2, matRoof,
        xFront + 0.4 + (depth - 0.5) / 2.0, y0 + h1 + h2 + 0.11, zMid))
    ThreeInterop.addChild(g,
      Util.box(depth + 0.06, 0.18, zFar - zNear + 0.06, matRoof,
        xFront + depth / 2.0, y0 + h1 + 0.05, zMid))

    ctx.collide(xFront - 0.05, zNear, xFront + depth, zFar, y0 + h1 + h2)

    /* ------------------------- tiled base along the front ------------------------- */
    ThreeInterop.addChild(g, Util.box(0.1, 0.55, zFar - zNear, matTile, xFront - 0.04, y0 + 0.27, zMid))

    /* -------------------------------- shutter -------------------------------- */
    val shutterGroup = new Group()
    val shutterZc    = zNear + 4.35
    val shutterW     = 3.0
    ThreeInterop.addChild(g,
      Util.box(0.18, 0.34, shutterW + 0.3, matMetalDark,
        xFront - 0.02, y0 + h1 - 0.22, shutterZc))
    Vector(-1.0, 1.0).foreach { s =>
      ThreeInterop.addChild(g,
        Util.box(0.2, h1 - 0.4, 0.14, matMetalDark,
          xFront - 0.03, y0 + (h1 - 0.4) / 2.0, shutterZc + s * (shutterW / 2.0 + 0.07)))
    }
    // recessed doorway behind the shutter, kept readable rather than black
    ThreeInterop.addChild(g,
      Util.box(0.5, h1 - 0.5, shutterW,
        Toon.cel(Toon.CelOptions(color = 0x9c95a6, bands = 2, tint = 0x6b6488)),
        xFront + 0.3, y0 + (h1 - 0.5) / 2.0, shutterZc))

    val shutterTexture = Textures.shutterTex(26)
    shutterTexture.wrapS = 1001
    shutterTexture.wrapT = 1001
    val shutterMat = Toon.cel(Toon.CelOptions(color = Pal.shutter, bands = 3,
      map = Some(shutterTexture), tint = 0x4b4560, cache = false))
    val shHeight = h1 - 0.55
    val shTop    = y0 + shHeight
    val slat     = Util.box(0.07, shHeight, shutterW, shutterMat, 0.0, 0.0, 0.0)
    slat.castShadow = true
    slat.receiveShadow = true
    ThreeInterop.addChild(shutterGroup, slat)
    ThreeInterop.setUserDataFlag(shutterGroup, "planetRigid")
    shutterGroup.position.set(xFront - 0.055, y0 + shHeight / 2.0, shutterZc)
    ThreeInterop.addChild(g, shutterGroup)
    sakura.core.Outline.hullOutline(slat, sakura.core.Outline.OutlineOptions(thickness = 0.003))

    // bottom rail travels with the lower edge but never squashes
    val shutterRail = Util.box(0.11, 0.11, shutterW + 0.02, matMetalDark, xFront - 0.055, y0 + 0.05, shutterZc)
    ThreeInterop.addChild(g, shutterRail)
    val shutterState = ShutterState(shutterGroup, shutterRail, shHeight, shTop, shutterW, shutterZc)

    val shutterHit = Util.box(0.8, 2.2, shutterW,
      Toon.flat(Toon.FlatOptions(color = 0xff0000, cache = false)), xFront - 0.4, y0 + 1.1, shutterZc)
    shutterHit.visible = false
    ThreeInterop.addChild(g, shutterHit)
    val shopState = new ShopState(shutterState)
    ctx.interact(shutterHit, "シャッター  ·  open the shutter", () => shopState.toggleShutter())

    /* ------------------------------ shopfront glass ------------------------------ */
    val glassZc = zFar - 1.15
    val glassW  = 1.9
    ThreeInterop.addChild(g,
      Util.box(0.12, h1 - 0.9, glassW, matGlass,
        xFront - 0.05, y0 + 0.45 + (h1 - 0.9) / 2.0, glassZc))
    Vector(-1.0, 0.0, 1.0).foreach { dz =>
      ThreeInterop.addChild(g,
        Util.box(0.16, h1 - 0.85, 0.09, matMetal,
          xFront - 0.06, y0 + 0.45 + (h1 - 0.9) / 2.0, glassZc + dz))
    }
    ThreeInterop.addChild(g, Util.box(0.2, 0.45, glassW + 0.1, matTile, xFront - 0.06, y0 + 0.22, glassZc))
    ThreeInterop.addChild(g, Util.box(0.2, 0.16, glassW + 0.1, matMetal, xFront - 0.06, y0 + h1 - 0.5, glassZc))
    // warm interior glimpse
    ThreeInterop.addChild(g,
      Util.box(0.5, h1 - 1.1, glassW - 0.3,
        Toon.flat(Toon.FlatOptions(color = 0xe8dfc8)), xFront + 0.35, y0 + 0.5 + (h1 - 1.1) / 2.0, glassZc))
    // shelving silhouettes inside
    (0 until 3).foreach { i =>
      ThreeInterop.addChild(g,
        Util.box(0.3, 0.08, glassW - 0.5,
          Toon.cel(Toon.CelOptions(color = 0xbdb3a0, bands = 2)), xFront + 0.2, y0 + 0.8 + i * 0.55, glassZc))
    }

    /* -------------------------------- awning -------------------------------- */
    // sloping canvas built from alternating stripes, so the red/white banding
    // is geometry rather than a texture -- crisper under cel shading
    val awningZA   = zNear + 2.95
    val awningZB   = zFar + 0.05
    val awningLen  = awningZB - awningZA
    val awningOut  = 1.85
    val awningYA   = y0 + h1 - 0.18
    val awningDrop = 0.42
    val stripes    = 18
    val partsA     = mutable.ArrayBuffer.empty[Util.BakePart]
    val partsB     = mutable.ArrayBuffer.empty[Util.BakePart]
    val stripeW    = awningLen / stripes
    (0 until stripes).foreach { i =>
      val geometry = new BoxGeometry(awningOut, 0.07, stripeW)
      (if i % 2 == 0 then partsA else partsB) += Util.BakePart(geometry,
        Some(Util.trs(-awningOut / 2.0, 0.0, awningZA + stripeW * (i + 0.5) - (awningZA + awningZB) / 2.0)))
    }
    val canvasGrp = new Group()
    canvasGrp.position.set(xFront, awningYA, (awningZA + awningZB) / 2.0)
    val canvasA = new Mesh(Util.bake(partsA.toList), matRed)
    val canvasB = new Mesh(Util.bake(partsB.toList), matRedSoft)
    canvasA.castShadow = true
    canvasB.castShadow = true
    canvasA.receiveShadow = true
    canvasB.receiveShadow = true
    ThreeInterop.addChild(canvasGrp, canvasA)
    ThreeInterop.addChild(canvasGrp, canvasB)
    // positive Z rotation drops the outward (-X) edge
    val slope = math.atan2(awningDrop, awningOut)
    canvasGrp.rotation.z = slope
    ThreeInterop.addChild(g, canvasGrp)
    sakura.core.Outline.hullOutline(canvasA, sakura.core.Outline.OutlineOptions(thickness = 0.003))
    sakura.core.Outline.hullOutline(canvasB, sakura.core.Outline.OutlineOptions(thickness = 0.003))

    val edgeX = xFront - awningOut * math.cos(slope)
    val edgeY = awningYA - awningOut * math.sin(slope)

    // front fascia with a scalloped lower edge
    val fascia = Util.box(0.08, 0.34, awningLen, matRed, edgeX + 0.03, edgeY - 0.15, (awningZA + awningZB) / 2.0)
    fascia.castShadow = true
    ThreeInterop.addChild(g, fascia)
    sakura.core.Outline.hullOutline(fascia, sakura.core.Outline.OutlineOptions(thickness = 0.003))
    val scallops        = math.round(awningLen / 0.55).toInt
    val scallopGeometry = new CircleGeometry(0.16, 12.0)
    val scallopParts    = mutable.ArrayBuffer.empty[Util.BakePart]
    (0 until scallops).foreach { i =>
      scallopParts += Util.BakePart(scallopGeometry,
        Some(Util.trs(0.0, 0.0,
          awningZA + (awningLen / scallops) * (i + 0.5) - (awningZA + awningZB) / 2.0, 0.0, -math.Pi / 2.0, 0.0)))
    }
    val scallopMesh = new Mesh(Util.bake(scallopParts.toList), matRed)
    scallopMesh.position.set(edgeX - 0.012, edgeY - 0.32, (awningZA + awningZB) / 2.0)
    ThreeInterop.addChild(g, scallopMesh)

    // support arms
    Vector(awningZA + 0.5, (awningZA + awningZB) / 2.0, awningZB - 0.5).foreach { zc =>
      val arm = Util.box(awningOut * 1.02, 0.07, 0.07, matMetalDark, 0.0, 0.0, 0.0)
      arm.position.set(xFront - awningOut / 2.0 * math.cos(slope),
        awningYA - (awningOut / 2.0) * math.sin(slope) - 0.08, zc)
      arm.rotation.z = slope
      ThreeInterop.addChild(g, arm)
      val strut = Util.box(0.06, 0.78, 0.06, matMetalDark, xFront - 0.14, awningYA - 0.4, zc)
      strut.rotation.z = -0.42
      ThreeInterop.addChild(g, strut)
    }

    /* ------------------------------- shop sign ------------------------------- */
    val signZc  = zMid + 0.1
    val signLen = 5.6
    val board = new Mesh(
      new BoxGeometry(0.14, 0.78, signLen),
      ThreeInterop.materialArray(
        Toon.flat(Toon.FlatOptions(color = 0xffffff, map = Some(Textures.shopSign()), cache = false)),
        Toon.flat(Toon.FlatOptions(color = Pal.wallWhite)),
        Toon.flat(Toon.FlatOptions(color = Pal.wallGray)),
        Toon.flat(Toon.FlatOptions(color = Pal.wallGray)),
        Toon.flat(Toon.FlatOptions(color = Pal.wallWhite)),
        Toon.flat(Toon.FlatOptions(color = Pal.wallWhite))
      )
    )
    board.position.set(xFront - 0.06, y0 + h1 + 0.62, signZc)
    board.castShadow = true
    ThreeInterop.addChild(g, board)
    sakura.core.Outline.hullOutline(board, sakura.core.Outline.OutlineOptions(thickness = 0.0034))
    ThreeInterop.addChild(g, Util.box(0.2, 0.1, signLen + 0.1, matMetal, xFront - 0.06, y0 + h1 + 1.05, signZc))

    /* ------------------------------ upper storey ------------------------------ */
    val xWall = xFront + 0.35
    val yW    = y0 + h1 + 1.38
    Vector(zNear + 2.0, zNear + 4.6, zFar - 1.1).foreach { zc =>
      ThreeInterop.addChild(g, Util.box(0.16, 1.44, 1.66, matMetal, xWall + 0.05, yW, zc))
      ThreeInterop.addChild(g,
        Util.box(0.05, 1.26, 1.5,
          Toon.flat(Toon.FlatOptions(color = Pal.glassDark)), xWall - 0.035, yW, zc))
      ThreeInterop.addChild(g, Util.box(0.05, 1.26, 0.07, matMetal, xWall - 0.07, yW, zc))
      ThreeInterop.addChild(g, Util.box(0.2, 0.09, 1.74, matMetal, xWall, yW - 0.78, zc))
      // a flat highlight across the glass
      val hi = new Mesh(new PlaneGeometry(0.5, 1.7),
        Toon.flat(Toon.FlatOptions(color = 0xeaf3fb, transparent = true, opacity = 0.22,
          depthWrite = Some(false), cache = false)))
      hi.position.set(xWall - 0.09, yW, zc - 0.28)
      hi.rotation.set(0.0, -math.Pi / 2.0, 0.4)
      ThreeInterop.setUserDataFlag(hi, "noOutline")
      ThreeInterop.addChild(g, hi)
    }
    // balcony over the middle window, cantilevered out to the shopfront line
    val balconyZ = zNear + 4.6
    val balconyX = xFront + 0.02
    ThreeInterop.addChild(g, Util.box(0.42, 0.09, 2.2, matMetalDark, balconyX + 0.18, y0 + h1 + 0.62, balconyZ))
    Vector(0.44, 0.9).foreach { y =>
      ThreeInterop.addChild(g, Util.box(0.06, 0.06, 2.2, matMetal, balconyX, y0 + h1 + 0.62 + y, balconyZ))
    }
    (0 to 9).foreach { i =>
      ThreeInterop.addChild(g,
        Util.box(0.04, 0.52, 0.04, matMetal,
          balconyX, y0 + h1 + 0.9, balconyZ - 1.05 + i * 0.233))
    }
    // laundry hung out to dry
    ThreeInterop.addChild(g, Util.box(0.04, 0.04, 1.9, matMetal, balconyX + 0.05, y0 + h1 + 1.62, balconyZ))
    Vector((0.34, 0.6, Pal.wallBlue), (0.28, 0.48, Pal.blossom), (0.3, 0.54, Pal.wallWhite)).zipWithIndex.foreach {
      case ((w, h, color), i) =>
        val towel = new Mesh(new PlaneGeometry(w, h),
          Toon.cel(Toon.CelOptions(color = color, bands = 2, side = 2, tint = 0x6f6790)))
        towel.position.set(balconyX + 0.05, y0 + h1 + 1.6 - h / 2.0, balconyZ - 0.62 + i * 0.62)
        towel.rotation.y = math.Pi / 2.0
        towel.castShadow = true
        ThreeInterop.addChild(g, towel)
    }

    /* ------------------------- wall clutter and services ------------------------- */
    val aircon = Util.box(0.62, 0.62, 0.95,
      Toon.cel(Toon.CelOptions(color = 0xe4e2e6, bands = 3, tint = 0x6f6790)),
      xFront + 0.3, y0 + h1 + 0.45, zFar - 0.75)
    aircon.castShadow = true
    ThreeInterop.addChild(g, aircon)
    val airconFan = Util.cyl(0.22, 0.22, 0.06, 12, matMetalDark, xFront - 0.02, y0 + h1 + 0.45, zFar - 0.75)
    airconFan.rotateZ(math.Pi / 2.0)
    ThreeInterop.addChild(g, airconFan)
    // pipes running down the wall
    Vector((zFar - 0.2, h1), (zNear + 0.35, h1 + 1.2)).foreach { case (zc, h) =>
      ThreeInterop.addChild(g, Util.cyl(0.05, 0.05, h, 8, matMetal, xFront - 0.06, y0 + h / 2.0, zc))
      (0 until math.floor(h / 1.1).toInt).foreach { i =>
        ThreeInterop.addChild(g, Util.box(0.1, 0.06, 0.1, matMetalDark, xFront - 0.04, y0 + 0.6 + i * 1.1, zc))
      }
    }
    // electricity meter box
    val meter = new Mesh(new BoxGeometry(0.14, 0.5, 0.36),
      ThreeInterop.materialArray(
        Toon.flat(Toon.FlatOptions(color = 0xffffff, map = Some(Textures.meterBox()), cache = false)),
        Toon.flat(Toon.FlatOptions(color = Pal.wallGray)),
        Toon.flat(Toon.FlatOptions(color = Pal.wallGray)),
        Toon.flat(Toon.FlatOptions(color = Pal.wallGray)),
        Toon.flat(Toon.FlatOptions(color = Pal.wallGray)),
        Toon.flat(Toon.FlatOptions(color = Pal.wallGray))
      ))
    meter.position.set(xFront - 0.06, y0 + 1.75, zNear + 0.75)
    ThreeInterop.addChild(g, meter)

    // posters taped beside the door
    Vector(0, 1, 2).zipWithIndex.foreach { case (v, i) =>
      val poster = new Mesh(new PlaneGeometry(0.42, 0.58),
        Toon.flat(Toon.FlatOptions(color = 0xffffff, map = Some(Textures.poster(v)), cache = false)))
      poster.position.set(xFront - 0.055, y0 + 1.92 - (i % 2) * 0.12, zNear + 2.55 + i * 0.48)
      poster.rotation.y = -math.Pi / 2.0
      poster.rotation.z = rng.range(-0.03, 0.03)
      ThreeInterop.setUserDataFlag(poster, "noOutline")
      ThreeInterop.addChild(g, poster)
      ()
    }

    // hanging vertical banner
    val banner = new Mesh(new PlaneGeometry(0.42, 1.7),
      Toon.flat(Toon.FlatOptions(color = 0xffffff, map = Some(Textures.shopBanner()), side = 2, cache = false)))
    banner.position.set(xFront - 1.6, y0 + h1 - 1.35, zNear + 0.55)
    banner.rotation.y = -math.Pi / 2.0
    banner.castShadow = true
    ThreeInterop.addChild(g, banner)
    ThreeInterop.addChild(g, Util.box(0.04, 0.04, 0.5, matMetalDark, xFront - 1.6, y0 + h1 - 0.5, zNear + 0.55))

    // security-camera plate high on the corner
    val cameraPlate = new Mesh(new PlaneGeometry(0.3, 0.6),
      Toon.flat(Toon.FlatOptions(color = 0xffffff, map = Some(Textures.warningPlate(0)), cache = false)))
    cameraPlate.position.set(xFront - 0.055, y0 + 2.55, zNear + 1.0)
    cameraPlate.rotation.y = -math.Pi / 2.0
    ThreeInterop.addChild(g, cameraPlate)

    // the north gable: pipes, a meter, a window and a ladder break it into bands
    val gableZ = zFar + 0.01
    ThreeInterop.addChild(g,
      Util.box(depth - 0.6, 0.16, 0.14, matRoof,
        xFront + 0.4 + depth / 2.0 - 0.3, y0 + 1.55, gableZ))
    Vector(1.5, 4.9).foreach { xo =>
      ThreeInterop.addChild(g,
        Util.cyl(0.05, 0.05, h1 + 0.4, 8, matMetal,
          xFront + xo, y0 + (h1 + 0.4) / 2.0, gableZ + 0.04))
      (0 until 3).foreach { i =>
        ThreeInterop.addChild(g,
          Util.box(0.11, 0.06, 0.11, matMetalDark,
            xFront + xo, y0 + 0.7 + i * 1.1, gableZ + 0.08))
      }
    }
    // small frosted window, upper storey
    ThreeInterop.addChild(g, Util.box(1.05, 0.95, 0.14, matMetal, xFront + 2.9, y0 + h1 + 1.5, gableZ - 0.02))
    ThreeInterop.addChild(g,
      Util.box(0.9, 0.8, 0.06,
        Toon.flat(Toon.FlatOptions(color = 0xc4d2dc)), xFront + 2.9, y0 + h1 + 1.5, gableZ + 0.05))
    ThreeInterop.addChild(g, Util.box(0.06, 0.8, 0.07, matMetal, xFront + 2.9, y0 + h1 + 1.5, gableZ + 0.08))
    // access ladder up to the roof
    Vector(0.0, 0.42).foreach { dz =>
      ThreeInterop.addChild(g,
        Util.box(0.05, h2 + 0.5, 0.05, matMetalDark,
          xFront + 5.6, y0 + h1 + (h2 + 0.5) / 2.0, gableZ + 0.12 + dz * 0.0))
      ThreeInterop.addChild(g,
        Util.box(0.05, h2 + 0.5, 0.05, matMetalDark,
          xFront + 5.6 + 0.42, y0 + h1 + (h2 + 0.5) / 2.0, gableZ + 0.12))
    }
    (0 until 7).foreach { i =>
      ThreeInterop.addChild(g,
        Util.box(0.46, 0.035, 0.035, matMetalDark,
          xFront + 5.81, y0 + h1 + 0.25 + i * 0.42, gableZ + 0.12))
    }
    // a couple of posters on the gable
    Vector(3, 1).zipWithIndex.foreach { case (v, i) =>
      val poster = new Mesh(new PlaneGeometry(0.44, 0.6),
        Toon.flat(Toon.FlatOptions(color = 0xffffff, map = Some(Textures.poster(v)), cache = false)))
      poster.position.set(xFront + 0.75 + i * 0.55, y0 + 1.05, gableZ + 0.05)
      poster.rotation.z = rng.range(-0.02, 0.02)
      ThreeInterop.setUserDataFlag(poster, "noOutline")
      ThreeInterop.addChild(g, poster)
    }

    /* --------------------------- pavement clutter --------------------------- */
    val walkY  = y0 + Street.WalkH
    val crateA = Toon.cel(Toon.CelOptions(color = Pal.crate, bands = 3, tint = 0x4a4a92))
    val crateB = Toon.cel(Toon.CelOptions(color = Pal.crateAlt, bands = 3, tint = 0x7a4060))
    val stack  = new Group()
    val crateW = 0.52
    val crateH = 0.29
    val crateD = 0.36
    Vector((0.0, 0, 0.0), (0.0, 1, 0.0), (0.0, 2, 0.0), (0.58, 0, 0.05), (0.58, 1, 0.05), (-0.05, 0, 0.42))
      .zipWithIndex.foreach { case ((dx, ly, dz), i) =>
        val crate = Util.box(crateW, crateH, crateD, if i % 2 == 1 then crateA else crateB,
          dx, crateH / 2.0 + ly * crateH, dz)
        crate.castShadow = true
        crate.receiveShadow = true
        ThreeInterop.addChild(stack, crate)
        // hollow rim
        ThreeInterop.addChild(stack,
          Util.box(crateW - 0.06, 0.05, crateD - 0.06,
            Toon.cel(Toon.CelOptions(color = 0x2f3140, bands = 2)), dx, crateH - 0.01 + ly * crateH, dz))
      }
    stack.position.set(xFront + 0.55, walkY, zFar - 0.85)
    ThreeInterop.addChild(g, stack)
    ctx.collide(xFront + 0.2, zFar - 1.4, xFront + 1.35, zFar - 0.3, walkY + 0.9)

    // produce baskets by the door
    (0 until 3).foreach { i =>
      val basket = new Mesh(new CylinderGeometry(0.24, 0.19, 0.16, 10.0),
        Toon.cel(Toon.CelOptions(color = if i == 1 then Pal.basket else Pal.crate, bands = 3, tint = 0x6f6790)))
      basket.position.set(xFront - 0.7 + i * 0.05, walkY + 0.385, zFar - 2.9 + i * 0.52)
      basket.castShadow = true
      ThreeInterop.addChild(g, basket)
      // contents: a couple of bright rounded shapes
      (0 until 3).foreach { k =>
        val fruit = new Mesh(new IcosahedronGeometry(0.075, 0.0),
          Toon.cel(Toon.CelOptions(color = Vector(0xf0a63c, 0x8fbf4a, 0xe0574a)(k), bands = 2, tint = 0x6f6790)))
        fruit.position.set(basket.position.x + rng.range(-0.1, 0.1), basket.position.y + 0.1,
          basket.position.z + rng.range(-0.1, 0.1))
        ThreeInterop.addChild(g, fruit)
      }
    }
    // trestle table under the baskets
    ThreeInterop.addChild(g,
      Util.box(0.6, 0.05, 1.7,
        Toon.cel(Toon.CelOptions(color = Pal.metalWarm, bands = 3)), xFront - 0.68, walkY + 0.28, zFar - 2.4))
    Vector(-0.7, 0.7).foreach { dz =>
      ThreeInterop.addChild(g, Util.box(0.05, 0.28, 0.05, matMetalDark, xFront - 0.68, walkY + 0.14, zFar - 2.4 + dz))
    }
    ctx.collide(xFront - 1.0, zFar - 3.3, xFront - 0.35, zFar - 1.5, walkY + 0.4)

    // bin beside the shutter
    val binZ = zNear + 0.12
    val bin = new Mesh(new CylinderGeometry(0.24, 0.21, 0.72, 12.0),
      Toon.cel(Toon.CelOptions(color = Pal.bin, bands = 3, tint = 0x4a4a92)))
    bin.position.set(xFront - 0.62, walkY + 0.36, binZ)
    bin.castShadow = true
    ThreeInterop.addChild(g, bin)
    ThreeInterop.addChild(g, Util.cyl(0.26, 0.26, 0.06, 12, matMetalDark, xFront - 0.62, walkY + 0.74, binZ))

    /* ---------------------------- vending machines ---------------------------- */
    val machines = mutable.ArrayBuffer.empty[Vending.VendingMachine]
    Vector((0, zNear + 1.0, 1), (2, zNear + 2.25, 2)).foreach { case (variant, spotZ, seed) =>
      val machine = Vending.makeVendingMachine(variant, seed)
      machine.group.position.set(xFront - 0.42, walkY, spotZ)
      machine.group.rotation.y = -math.Pi / 2.0
      Util.shadowify(machine.group, cast = true, receive = true)
      ThreeInterop.addChild(g, machine.group)
      machines += machine
      ctx.collide(xFront - 0.85, spotZ - 0.6, xFront + 0.02, spotZ + 0.6, walkY + 2.0)
      ctx.interact(machine.hitbox, "自動販売機  ·  buy a drink", () => shopState.dispense(machine))
    }

    /* ------------------------------ shop state ------------------------------ */
    // start with the shutter mostly down: it reads better as a graphic block
    shopState.shutterTarget = 0.18
    shopState.shutterOpen = 0.18
    ctx.update(dt => shopState.update(dt))

    ShopResult(group = g, xFront = xFront, zNear = zNear, zFar = zFar, walkY = walkY, shopState = shopState)
  end buildShop
end Shop
