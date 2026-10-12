package sakura.world

import sakura.ThreeInterop
import sakura.core.{Pal, Textures, Toon, Util}
import vendor.three.*

import scala.collection.mutable

/** Vending machines, ported 1:1 from upstream `src/world/vending.js`.
  *
  * The shelves are real geometry -- instanced bottles and cans behind a sheet of
  * glass -- because those rows of saturated colour are the single best accent
  * the middle ground has. The lit panels use emissive toon material rather than
  * bloom, so they glow without hazing the frame.
  */
object Vending:

  private val BodyW = 1.12
  private val BodyH = 1.95
  private val BodyD = 0.72

  final private case class Variant(body: Int, accent: Int, header: Int, shelfBack: Int)

  private val Variants = Vector(
    Variant(Pal.vendWhite, Pal.red, 0, 0x2d3140),
    Variant(Pal.vendRed, 0xfdf6ec, 1, 0x2a2c38),
    Variant(Pal.vendTeal, 0xfdf6ec, 2, 0x27313a)
  )

  /** The machine group plus the interaction surface and dispense stepper the
    * shop and the district wrappers drive; upstream carries these on userData.
    */
  final case class VendingMachine(
    group: Group[Object3DEventMap],
    hitbox: Mesh[?, ?, ?],
    dispense: () => Double => Boolean
  )

  final case class VendingOptions(
    x: Double,
    z: Double,
    y: Double = 0.0,
    ry: Double = 0.0,
    variant: Int = 0,
    seed: Int = 1,
    label: String = "自動販売機  ·  buy a drink"
  )

  /** Place a machine in the world and wire up its interaction. The dispense
    * animation gets its own little updater here so machines outside the corner
    * shop work too.
    */
  def addVending(ctx: WorldCtx, options: VendingOptions): Group[Object3DEventMap] =
    val machine = makeVendingMachine(options.variant, options.seed)
    machine.group.position.set(options.x, options.y, options.z)
    machine.group.rotation.y = options.ry
    Util.shadowify(machine.group, cast = true, receive = true)
    ctx.add(machine.group)
    val half = 0.56
    ctx.collide(options.x - half, options.z - half, options.x + half, options.z + half, options.y + BodyH)
    val anims = mutable.ListBuffer.empty[Double => Boolean]
    ctx.interact(machine.hitbox, options.label, () => anims += machine.dispense())
    ctx.update { dt =>
      anims.filterInPlace(step => !step(dt))
      ()
    }
    machine.group

  /** @param variant 0 white / 1 red / 2 teal
    */
  def makeVendingMachine(variant: Int, seed: Int): VendingMachine =
    val v   = Variants(math.floorMod(variant, Variants.length))
    val rng = Util.rngKit(1000 + seed * 37)
    val g   = new Group()
    g.name = "vending"
    // holds the animated dispensed can, so it is re-seated rather than bent
    ThreeInterop.setUserDataFlag(g, "planetRigid")

    val matBody   = Toon.cel(Toon.CelOptions(color = v.body, bands = 3, tint = 0x6f6790))
    val matAccent = Toon.cel(Toon.CelOptions(color = v.accent, bands = 3, tint = 0x6f6790))
    val matDark   = Toon.cel(Toon.CelOptions(color = 0x30333f, bands = 2, tint = 0x4b4560))
    val matPlinth = Toon.cel(Toon.CelOptions(color = Pal.metalDark, bands = 3, tint = 0x5c5680))
    val matShelf  = Toon.cel(Toon.CelOptions(color = 0xf3f0ea, bands = 2, tint = 0x7d74a0))

    val front = BodyD / 2.0 + 0.001
    val yBase = 0.09

    // The 取出口: a rectangle taken out of the front of the body, so the body is
    // five boxes with a notch out of the front rather than a printed panel.
    val portX0 = -0.50
    val portX1 = 0.10
    val portY0 = 0.125
    val portY1 = 0.29
    val portZ  = front - 0.11
    val portW  = portX1 - portX0
    val portMx = (portX0 + portX1) / 2.0

    // plinth + body: one merged mesh, so the ink pass outlines only the outside
    // of the machine and the mouth of the port
    val plinth = Util.box(BodyW - 0.05, 0.06, BodyD - 0.05, matPlinth, 0.0, 0.06, 0.0)
    ThreeInterop.addChild(g, plinth)
    val bx        = BodyW / 2.0
    val bz        = BodyD / 2.0
    val by1       = yBase + BodyH
    val bodyParts = mutable.ArrayBuffer.empty[Util.BakePart]
    def slab(x0: Double, x1: Double, y0: Double, y1: Double, z0: Double, z1: Double): Unit =
      bodyParts += Util.BakePart(
        geometry = new BoxGeometry(x1 - x0, y1 - y0, z1 - z0),
        matrix = Some(Util.trs((x0 + x1) / 2.0, (y0 + y1) / 2.0, (z0 + z1) / 2.0))
      )
    slab(-bx, bx, portY1, by1, -bz, bz)
    slab(-bx, bx, yBase, portY0, -bz, bz)
    slab(-bx, portX0, portY0, portY1, -bz, bz)
    slab(portX1, bx, portY0, portY1, -bz, bz)
    slab(portX0, portX1, portY0, portY1, -bz, portZ)
    val body = new Mesh(Util.bake(bodyParts.toList), matBody)
    body.castShadow = true
    body.receiveShadow = true
    ThreeInterop.addChild(g, body)
    sakura.core.Outline.hullOutline(body, sakura.core.Outline.OutlineOptions(thickness = 0.0036))

    // lit header
    val header = Util.box(BodyW - 0.06, 0.3, 0.03,
      Toon.flat(Toon.FlatOptions(color = 0xffffff, map = Some(Textures.vendHeader(v.header)), cache = false)),
      0.0, yBase + BodyH - 0.22, front + 0.015)
    ThreeInterop.addChild(g, header)
    ThreeInterop.addChild(g, Util.box(BodyW - 0.02, 0.36, 0.03, matAccent, 0.0, yBase + BodyH - 0.22, front))

    // product display: built forward of the body face as a glazed cabinet
    val dispW  = BodyW - 0.22
    val dispH  = 0.94
    val dispY  = yBase + 0.86
    val dz     = front + 0.004
    val zShelf = dz + 0.055
    val zGlass = dz + 0.125
    ThreeInterop.addChild(g,
      Util.box(dispW, dispH, 0.02,
        Toon.cel(Toon.CelOptions(color = v.shelfBack, bands = 2, tint = 0x413c58)), 0.0, dispY, dz))
    Vector(-1.0, 1.0).foreach { s =>
      ThreeInterop.addChild(g, Util.box(0.03, dispH, 0.13, matBody, s * (dispW / 2.0 + 0.015), dispY, dz + 0.065))
    }
    ThreeInterop.addChild(g, Util.box(dispW + 0.06, 0.03, 0.13, matBody, 0.0, dispY + dispH / 2.0 + 0.015, dz + 0.065))
    ThreeInterop.addChild(g, Util.box(dispW + 0.06, 0.03, 0.13, matBody, 0.0, dispY - dispH / 2.0 - 0.015, dz + 0.065))

    val shelves       = 4
    val bottleGeoTall = new CylinderGeometry(0.032, 0.032, 0.165, 8.0)
    val bottleGeoCan  = new CylinderGeometry(0.03, 0.03, 0.11, 8.0)
    val perShelf      = 6
    val total         = shelves * perShelf
    val drinkMat      = Toon.cel(Toon.CelOptions(color = 0xffffff, bands = "soft3", tint = 0x9c93b8, cache = false))
    val inst          = new InstancedMesh(bottleGeoTall, drinkMat, total.toDouble)
    val instCan       = new InstancedMesh(bottleGeoCan, drinkMat, total.toDouble)
    val d             = new Object3D()
    val col           = new Color()
    var nT            = 0
    var nC            = 0
    (0 until shelves).foreach { s =>
      val sy = dispY - dispH / 2.0 + 0.13 + s * 0.225
      ThreeInterop.addChild(g, Util.box(dispW - 0.02, 0.018, 0.11, matShelf, 0.0, sy - 0.095, zShelf))
      ThreeInterop.addChild(g,
        Util.box(dispW - 0.02, 0.055, 0.012,
          Toon.flat(Toon.FlatOptions(color = 0xffffff, map = Some(Textures.vendPrice()), cache = false)),
          0.0, sy - 0.075, zGlass + 0.004))
      val tall = s % 2 == 0
      (0 until perShelf).foreach { i =>
        val px = -dispW / 2.0 + 0.09 + (i * (dispW - 0.16)) / (perShelf - 1)
        col.set(Pal.drinks((s * perShelf + i + variant * 3) % Pal.drinks.length).toDouble)
        d.position.set(px, sy + (if tall then 0.085 else 0.06), zShelf + 0.008)
        d.rotation.set(0.0, rng.range(0.0, 3.0), 0.0)
        d.updateMatrix()
        if tall then
          inst.setMatrixAt(nT.toDouble, d.matrix)
          inst.setColorAt(nT.toDouble, col)
          nT += 1
        else
          instCan.setMatrixAt(nC.toDouble, d.matrix)
          instCan.setColorAt(nC.toDouble, col)
          nC += 1
      }
    }
    inst.count = nT.toDouble
    instCan.count = nC.toDouble
    if inst.instanceColor != null then ThreeInterop.markForUpload(inst.instanceColor)
    if instCan.instanceColor != null then ThreeInterop.markForUpload(instCan.instanceColor)
    ThreeInterop.setUserDataFlag(inst, "noOutline")
    ThreeInterop.setUserDataFlag(instCan, "noOutline")
    ThreeInterop.addChild(g, inst)
    ThreeInterop.addChild(g, instCan)

    // glass over the display, with one flat highlight
    val glass = Util.box(dispW + 0.03, dispH + 0.03, 0.012,
      Toon.flat(Toon.FlatOptions(color = 0xd8e8f2, transparent = true, opacity = 0.13,
        depthWrite = Some(false), cache = false)),
      0.0, dispY, zGlass)
    ThreeInterop.setUserDataFlag(glass, "noOutline")
    ThreeInterop.addChild(g, glass)
    val hi = new Mesh(
      new PlaneGeometry(dispW * 0.22, dispH * 0.98),
      Toon.flat(Toon.FlatOptions(color = 0xf2f8ff, transparent = true, opacity = 0.13,
        depthWrite = Some(false), cache = false))
    )
    hi.position.set(-dispW * 0.24, dispY, zGlass + 0.009)
    hi.rotation.z = 0.22
    ThreeInterop.setUserDataFlag(hi, "noOutline")
    ThreeInterop.addChild(g, hi)
    Vector(
      (dispW + 0.09, 0.038, 0.0, dispY + dispH / 2.0 + 0.032), (dispW + 0.09, 0.038, 0.0, dispY - dispH / 2.0 - 0.032),
      (0.038, dispH + 0.1, -dispW / 2.0 - 0.032, dispY), (0.038, dispH + 0.1, dispW / 2.0 + 0.032, dispY)
    ).foreach { case (w, h, x, y) =>
      ThreeInterop.addChild(g, Util.box(w, h, 0.05, matAccent, x, y, zGlass - 0.01))
    }

    // cold / hot label
    ThreeInterop.addChild(g,
      Util.box(0.42, 0.1, 0.012,
        Toon.flat(Toon.FlatOptions(color = 0xffffff, map = Some(Textures.vendCold(false)), cache = false)),
        -0.28, yBase + 0.36, front + 0.008))
    ThreeInterop.addChild(g,
      Util.box(0.3, 0.1, 0.012,
        Toon.flat(Toon.FlatOptions(color = 0xffffff, map = Some(Textures.vendCold(true)), cache = false)),
        0.2, yBase + 0.36, front + 0.008))

    // selection buttons in the one band clear of the port and the plates
    val btnLit = Toon.cel(Toon.CelOptions(color = 0xfff2cf, bands = 2,
      emissive = Some(0xffd98a), emissiveIntensity = 0.55, tint = 0x8f86ad))
    (0 until 2).foreach { r =>
      (0 until 5).foreach { i =>
        val bx2 = -0.45 + i * 0.16
        val by  = 0.37 - r * 0.06
        ThreeInterop.addChild(g,
          Util.box(0.15, 0.05, 0.015, if r == 0 then btnLit else matAccent,
            bx2, by, front + 0.008))
      }
    }

    // coin slot + electronic payment pad
    ThreeInterop.addChild(g, Util.box(0.2, 0.34, 0.02, matDark, 0.4, yBase + 0.62, front + 0.008))
    ThreeInterop.addChild(g,
      Util.box(0.12, 0.02, 0.025,
        Toon.cel(Toon.CelOptions(color = 0xe8e4dc, bands = 2)), 0.4, yBase + 0.72, front + 0.014))
    ThreeInterop.addChild(g,
      Util.box(0.13, 0.05, 0.025,
        Toon.flat(Toon.FlatOptions(color = 0x8fd4c8)), 0.4, yBase + 0.52, front + 0.014))

    // change return: the cup, the lever, the chrome flap
    ThreeInterop.addChild(g, Util.box(0.17, 0.12, 0.03, matDark, 0.4, yBase + 0.4, front + 0.005))
    ThreeInterop.addChild(g,
      Util.box(0.13, 0.075, 0.02,
        Toon.cel(Toon.CelOptions(color = 0x1d2029, bands = 2, tint = 0x3f3a55)), 0.4, yBase + 0.385, front + 0.02))
    ThreeInterop.addChild(g,
      Util.box(0.15, 0.022, 0.028,
        Toon.cel(Toon.CelOptions(color = 0xc8ccd4, bands = 2, tint = 0x6a6288)), 0.4, yBase + 0.455, front + 0.02))
    ThreeInterop.addChild(g, Util.box(0.05, 0.05, 0.03, matPlinth, 0.29, yBase + 0.47, front + 0.02))

    // condenser grille, low on the front beside the delivery port
    (0 until 3).foreach { i =>
      ThreeInterop.addChild(g, Util.box(0.34, 0.022, 0.018, matDark, 0.33, portY0 + 0.03 + i * 0.038, front + 0.006))
    }
    ThreeInterop.addChild(g,
      Util.box(0.38, portY1 - portY0, 0.012,
        Toon.cel(Toon.CelOptions(color = v.body, bands = 3, tint = 0x6f6790)), 0.33, (portY0 + portY1) / 2.0,
        front + 0.001))

    // inside the port: lined with dark back, sides and head, and a lighter tray
    val matPort = Toon.cel(Toon.CelOptions(color = 0x35373f, bands = 2, tint = 0x3f3a55))
    val matTray = Toon.cel(Toon.CelOptions(color = 0x6e7280, bands = 2, tint = 0x4b4560,
      emissive = Some(0x2b2e38), emissiveIntensity = 0.7))
    val pocketD = front - 0.016 - (portZ + 0.006)
    val pocketZ = portZ + 0.006 + pocketD / 2.0
    val pocketH = portY1 - portY0 - 0.02
    val pocketY = (portY0 + portY1) / 2.0
    ThreeInterop.addChild(g, Util.box(portW - 0.012, portY1 - portY0, 0.012, matPort, portMx, pocketY, portZ + 0.007))
    ThreeInterop.addChild(g, Util.box(portW - 0.012, 0.012, pocketD, matTray, portMx, portY0 + 0.007, pocketZ))
    ThreeInterop.addChild(g, Util.box(portW - 0.012, 0.01, pocketD, matPort, portMx, portY1 - 0.006, pocketZ))
    Vector(-1.0, 1.0).foreach { s =>
      ThreeInterop.addChild(g,
        Util.box(0.01, pocketH, pocketD, matPort, portMx + s * (portW / 2.0 - 0.007), pocketY, pocketZ))
    }

    // the fixed translucent flap over the port; the falling can is the motion
    val edge = Toon.flat(Toon.FlatOptions(color = 0x2e2c38, transparent = true, opacity = 0.40,
      depthWrite = Some(false), cache = false))
    val face = Toon.flat(Toon.FlatOptions(color = 0xffffff, map = Some(Textures.vendSlot()),
      transparent = true, opacity = 0.40, depthWrite = Some(false), cache = false))
    val flap = new Mesh(new BoxGeometry(portW - 0.03, portY1 - portY0 - 0.014, 0.008),
      ThreeInterop.materialArray(edge, edge, edge, edge, face, edge))
    flap.position.set(portMx, pocketY, front - 0.012)
    ThreeInterop.setUserDataFlag(flap, "noOutline")
    ThreeInterop.addChild(g, flap)

    // base: a recessed kick plate on four levelling feet
    ThreeInterop.addChild(g, Util.box(BodyW - 0.06, 0.055, BodyD - 0.06, matDark, 0.0, 0.062, 0.0))
    Vector(-1.0, 1.0).foreach { sx =>
      Vector(-1.0, 1.0).foreach { sz =>
        ThreeInterop.addChild(g,
          Util.cyl(0.028, 0.032, 0.035, 6, matPlinth,
            sx * (BodyW / 2.0 - 0.09), 0.018, sz * (BodyD / 2.0 - 0.09)))
      }
    }

    // side seams
    Vector(-1.0, 1.0).foreach { s =>
      ThreeInterop.addChild(g,
        Util.box(0.02, BodyH - 0.1, 0.02, matPlinth,
          s * (BodyW / 2.0 + 0.002), yBase + BodyH / 2.0, front - 0.02))
    }

    // the can that drops when used: emissive so it reads against the dark tray
    val canR   = 0.033
    val canCol = Vector(0xf0c34a, 0xe4776a, 0x74c8dc)(math.floorMod(variant, 3))
    val can = new Mesh(
      new CylinderGeometry(canR, canR, 0.12, 10.0),
      Toon.cel(Toon.CelOptions(color = canCol, bands = 2, tint = 0x6f6790,
        emissive = Some(canCol), emissiveIntensity = 0.42))
    )
    can.visible = false
    can.castShadow = true
    ThreeInterop.addChild(g, can)

    val canTop  = portY1 + 0.09
    val canRest = portY0 + 0.013 + canR
    val tFall   = math.sqrt((canTop - canRest) / 4.9)
    val tGone   = 2.6

    // one can, so one animation: a press supersedes the press before it
    var tokenValue = 0

    val dispense: () => Double => Boolean = () =>
      tokenValue += 1
      val mine = tokenValue
      can.visible = true
      can.position.set(portMx, canTop, portZ + 0.055)
      can.rotation.set(0.0, 0.0, math.Pi / 2.0)
      var t = 0.0
      (dt: Double) =>
        if mine != tokenValue then true
        else
          t += dt
          if t < tFall then
            can.position.y = canTop - 4.9 * t * t
            // rocking rather than spinning: it is falling a fifth of a metre
            can.rotation.z = math.Pi / 2.0 + math.sin(t * 26.0) * 0.42
          else
            // one small bounce off the tray, damped out in a third of a second
            val u = t - tFall
            can.position.y = canRest + math.abs(math.sin(u * 17.0)) * 0.03 * math.exp(-u * 7.0)
            can.rotation.z = math.Pi / 2.0
          if t > tGone then
            can.visible = false
            true
          else false

    // interaction hitbox
    val hit = Util.box(BodyW, BodyH, BodyD + 0.7,
      Toon.flat(Toon.FlatOptions(color = 0xff0000, cache = false)), 0.0, yBase + BodyH / 2.0, 0.35)
    hit.visible = false
    ThreeInterop.addChild(g, hit)
    g.userData("hitbox") = hit

    VendingMachine(group = g, hitbox = hit, dispense = dispense)
  end makeVendingMachine
end Vending
