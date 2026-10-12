package sakura.world

import sakura.ThreeInterop
import sakura.core.{Pal, Toon, Util}
import vendor.three.*

import scala.collection.mutable

/** Cherry trees and the green canopy, ported 1:1 from upstream `src/world/trees.js`.
  *
  * Trunks and branches are merged into a single mesh; canopies are instanced
  * meshes, one per tone. Clusters of faceted blobs read as painted mass far
  * better than leaf geometry would, and the tones give the canopy internal
  * shape without any texture. No canopy receives shadow: a ramp only shapes
  * direct light, so a blob the shadow map has zeroed falls back to ambient --
  * which at these tones is nearly black.
  */
object Trees:

  private val BlobTones  = Vector(Pal.blossomLight, Pal.blossom, Pal.blossomDeep)
  private val BlobTint   = Vector(0xe2c3d2, 0xd8b2c6, 0xc99cba)
  private val GreenTones = Vector(0x8cb884, 0x5f9470, Pal.cedar, Pal.willow)
  private val CedarTones = Vector(Pal.cedarLit, Pal.cedar, Pal.cedarDeep)

  final case class SakuraResult(wood: Mesh[?, ?, ?], canopies: Vector[InstancedMesh[?, ?, ?]])

  def buildSakura(ctx: WorldCtx, spots: Vector[SakuraSpot]): SakuraResult =
    val woodParts = mutable.ArrayBuffer.empty[Util.BakePart]
    val blobs = Vector(mutable.ArrayBuffer.empty[Matrix4], mutable.ArrayBuffer.empty[Matrix4],
      mutable.ArrayBuffer.empty[Matrix4])
    val trunkGeo  = new CylinderGeometry(0.7, 1.0, 1.0, 7.0, 1.0)
    val branchGeo = new CylinderGeometry(0.25, 0.55, 1.0, 5.0, 1.0)
    val twigGeo   = new CylinderGeometry(0.12, 0.3, 1.0, 4.0, 1.0)

    spots.foreach { spot =>
      val rng     = Util.rngKit(spot.seed)
      val s       = spot.scale
      val lean    = spot.lean
      val leanDir = spot.leanDir.getOrElse(rng.range(0.0, math.Pi * 2.0))

      val trunkH = 2.5 * s * rng.range(0.9, 1.12)
      val trunkR = 0.2 * s
      woodParts += Util.BakePart(trunkGeo,
        Some(Util.trs(spot.x, spot.y + trunkH / 2.0, spot.z,
          lean * math.sin(leanDir), 0.0, -lean * math.cos(leanDir), trunkR, trunkH, trunkR)))
      // root flare
      woodParts += Util.BakePart(trunkGeo,
        Some(Util.trs(spot.x, spot.y + 0.16 * s, spot.z,
          0.0, 0.0, 0.0, trunkR * 1.5, 0.34 * s, trunkR * 1.5)))

      // where the trunk actually ends: the rotation applied to (0, trunkH/2, 0)
      val tip = new Vector3(0.0, trunkH / 2.0, 0.0)
        .applyEuler(new Euler(lean * math.sin(leanDir), 0.0, -lean * math.cos(leanDir)))
      val topX = spot.x + tip.x
      val topZ = spot.z + tip.z
      val topY = spot.y + trunkH / 2.0 + tip.y

      // main limbs
      val limbs         = 3 + math.floor(rng.next() * 2.0).toInt
      val canopyCenters = mutable.ArrayBuffer.empty[Vector3]
      (0 until limbs).foreach { i =>
        val a    = (i.toDouble / limbs) * math.Pi * 2.0 + rng.range(-0.4, 0.4)
        val len  = 1.9 * s * rng.range(0.82, 1.2)
        val tilt = rng.range(0.5, 0.85)
        val ex   = topX + math.cos(a) * math.sin(tilt) * len
        val ez   = topZ + math.sin(a) * math.sin(tilt) * len
        val ey   = topY + math.cos(tilt) * len
        val mid  = new Vector3((topX + ex) / 2.0, (topY + ey) / 2.0, (topZ + ez) / 2.0)
        val dir  = new Vector3(ex - topX, ey - topY, ez - topZ)
        val l    = dir.length()
        val q = new Quaternion().setFromUnitVectors(new Vector3(0.0, 1.0, 0.0),
          ThreeInterop.asVector3Like(dir.clone().normalize()))
        woodParts += Util.BakePart(branchGeo,
          Some(new Matrix4().compose(mid, q, new Vector3(0.13 * s, l, 0.13 * s))))
        canopyCenters += new Vector3(ex, ey, ez)

        // one fork per limb
        if rng.next() < 0.75 then
          val dir2 = dir.clone().normalize()
            .add(ThreeInterop.asVector3Like(new Vector3(rng.range(-0.7, 0.7), rng.range(0.1, 0.6), rng.range(-0.7, 0.7))))
            .normalize()
          val l2 = len * rng.range(0.5, 0.8)
          val e2 = new Vector3(ex, ey, ez).addScaledVector(ThreeInterop.asVector3Like(dir2), l2)
          val mid2 = new Vector3().addVectors(ThreeInterop.asVector3Like(new Vector3(ex, ey, ez)),
            ThreeInterop.asVector3Like(e2)).multiplyScalar(0.5)
          val q2 = new Quaternion().setFromUnitVectors(new Vector3(0.0, 1.0, 0.0),
            ThreeInterop.asVector3Like(dir2))
          woodParts += Util.BakePart(twigGeo,
            Some(new Matrix4().compose(mid2, q2, new Vector3(0.09 * s, l2, 0.09 * s))))
          canopyCenters += e2
      }

      // blossom mass: many small blobs, tone biased by height
      val count = 26 + math.floor(rng.next() * 10.0).toInt
      var yMin  = Double.PositiveInfinity
      var yMax  = Double.NegativeInfinity
      canopyCenters.foreach { center =>
        yMin = math.min(yMin, center.y)
        yMax = math.max(yMax, center.y)
      }
      (0 until count).foreach { _ =>
        val center = canopyCenters(math.floor(rng.next() * canopyCenters.length).toInt)
        val r      = 0.56 * s * rng.range(0.68, 1.3)
        val px     = center.x + rng.range(-1.15, 1.15) * s
        val py     = center.y + rng.range(-0.55, 0.95) * s
        val pz     = center.z + rng.range(-1.15, 1.15) * s
        val tone = spot.tone.getOrElse {
          val hi   = (py - yMin) / math.max(0.5, yMax + 1.2 * s - yMin)
          val base = if hi > 0.62 then 0 else if hi < 0.28 then 2 else 1
          if rng.next() < 0.22 then (base + 1) % 3 else base
        }
        blobs(tone) += Util.trs(px, py, pz, rng.range(0.0, 3.0), rng.range(0.0, 3.0), rng.range(0.0, 3.0),
          r, r * rng.range(0.68, 0.88), r)
      }

      // a small cluster crowning the silhouette
      (0 until 4).foreach { _ =>
        val r = 0.6 * s * rng.range(0.8, 1.15)
        blobs(0) += Util.trs(
          topX + rng.range(-0.7, 0.7) * s,
          topY + (1.25 + rng.range(0.0, 0.5)) * s,
          topZ + rng.range(-0.7, 0.7) * s,
          rng.range(0.0, 3.0), rng.range(0.0, 3.0), rng.range(0.0, 3.0),
          r, r * 0.8, r
        )
      }

      if spot.collide then
        ctx.collide(spot.x - trunkR * 1.15, spot.z - trunkR * 1.15,
          spot.x + trunkR * 1.15, spot.z + trunkR * 1.15, spot.y + trunkH)
    }

    val wood =
      new Mesh(Util.bake(woodParts.toList), Toon.cel(Toon.CelOptions(color = Pal.trunk, bands = 3, tint = 0x8a7290)))
    wood.castShadow = true
    wood.receiveShadow = true
    wood.name = "sakuraWood"
    ctx.add(wood)

    val blobGeo  = new IcosahedronGeometry(1.0, 1.0)
    val canopies = mutable.ArrayBuffer.empty[InstancedMesh[?, ?, ?]]
    blobs.zipWithIndex.foreach { case (list, i) =>
      if list.nonEmpty then
        val inst = new InstancedMesh(blobGeo,
          Toon.cel(Toon.CelOptions(color = BlobTones(i), bands = "soft", tint = BlobTint(i))), list.length.toDouble)
        list.zipWithIndex.foreach { case (m, k) => inst.setMatrixAt(k.toDouble, m) }
        inst.castShadow = true
        inst.receiveShadow = false
        inst.name = s"sakuraCanopy$i"
        ctx.add(inst)
        canopies += inst
    }

    SakuraResult(wood, canopies.toVector)
  end buildSakura

  def buildGrove(ctx: WorldCtx, spots: Vector[GroveSpot]): Mesh[?, ?, ?] =
    if spots.isEmpty then null
    else
      val woodParts = mutable.ArrayBuffer.empty[Util.BakePart]
      val blobs     = Vector.fill(4)(mutable.ArrayBuffer.empty[Matrix4])
      val trunkGeo  = new CylinderGeometry(0.62, 1.0, 1.0, 7.0, 1.0)
      val branchGeo = new CylinderGeometry(0.28, 0.6, 1.0, 5.0, 1.0)

      spots.foreach { spot =>
        val rng     = Util.rngKit(spot.seed)
        val s       = spot.scale
        val lean    = spot.lean
        val leanDir = spot.leanDir.getOrElse(rng.range(0.0, math.Pi * 2.0))
        val spread  = spot.spread

        // 柳 -- the weeping willow: same generator, three numbers changed
        val willow = spot.willow
        val trunkH = (if willow then 4.3 else 3.6) * s * rng.range(0.9, 1.15)
        val trunkR = (if willow then 0.185 else 0.24) * s
        woodParts += Util.BakePart(trunkGeo,
          Some(Util.trs(spot.x, spot.y + trunkH / 2.0, spot.z,
            lean * math.sin(leanDir), 0.0, -lean * math.cos(leanDir), trunkR, trunkH, trunkR)))
        woodParts += Util.BakePart(trunkGeo,
          Some(Util.trs(spot.x, spot.y + 0.2 * s, spot.z,
            0.0, 0.0, 0.0, trunkR * 1.55, 0.42 * s, trunkR * 1.55)))

        val tip = new Vector3(0.0, trunkH / 2.0, 0.0)
          .applyEuler(new Euler(lean * math.sin(leanDir), 0.0, -lean * math.cos(leanDir)))
        val topX = spot.x + tip.x
        val topZ = spot.z + tip.z
        val topY = spot.y + trunkH / 2.0 + tip.y

        val limbs   = (if willow then 5 else 3) + math.floor(rng.next() * 3.0).toInt
        val centers = mutable.ArrayBuffer.empty[Vector3]
        (0 until limbs).foreach { i =>
          val a   = (i.toDouble / limbs) * math.Pi * 2.0 + rng.range(-0.4, 0.4)
          val len = (if willow then 1.35 else 1.5) * s * rng.range(0.8, 1.25)
          // out, not up: a willow's limbs are near horizontal before they fall
          val tilt = if willow then rng.range(1.02, 1.42) else rng.range(0.35, 0.7)
          val ex   = topX + math.cos(a) * math.sin(tilt) * len * spread
          val ez   = topZ + math.sin(a) * math.sin(tilt) * len * spread
          val ey   = topY + math.cos(tilt) * len
          val dir  = new Vector3(ex - topX, ey - topY, ez - topZ)
          val l    = dir.length()
          val q = new Quaternion().setFromUnitVectors(new Vector3(0.0, 1.0, 0.0),
            ThreeInterop.asVector3Like(dir.clone().normalize()))
          woodParts += Util.BakePart(branchGeo,
            Some(new Matrix4().compose(
              new Vector3((topX + ex) / 2.0, (topY + ey) / 2.0, (topZ + ez) / 2.0),
              q, new Vector3(0.15 * s, l, 0.15 * s)
            )))
          centers += new Vector3(ex, ey, ez)
        }

        // denser and rounder than blossom; a willow is a curtain of small fronds
        val count = (if willow then 120 else 30) + math.floor(rng.next() * 12.0).toInt
        var yMin  = Double.PositiveInfinity
        var yMax  = Double.NegativeInfinity
        centers.foreach { center =>
          yMin = math.min(yMin, center.y)
          yMax = math.max(yMax, center.y)
        }
        (0 until count).foreach { _ =>
          val center = centers(math.floor(rng.next() * centers.length).toInt)
          if willow then
            val r    = 0.185 * s * rng.range(0.7, 1.25)
            val fall = rng.range(0.05, 1.0)
            val px   = center.x + rng.range(-1.05, 1.05) * s * spread
            val pz   = center.z + rng.range(-1.05, 1.05) * s * spread
            val py   = math.max(spot.y + 0.55 * s, center.y + 0.42 * s - fall * (center.y - spot.y) * 0.95)
            val tone = if rng.next() < 0.62 then 3 else 1
            blobs(tone) += Util.trs(px, py, pz, 0.0, rng.range(0.0, 3.0), rng.range(-0.25, 0.25),
              r, r * rng.range(1.7, 2.6), r * rng.range(0.85, 1.05))
          else
            val r  = 0.72 * s * rng.range(0.7, 1.25)
            val px = center.x + rng.range(-1.25, 1.25) * s * spread
            val py = center.y + rng.range(-0.7, 1.5) * s
            val pz = center.z + rng.range(-1.25, 1.25) * s * spread
            val tone = spot.tone.getOrElse {
              val hi   = (py - yMin) / math.max(0.5, yMax + 1.6 * s - yMin)
              val base = if hi > 0.66 then 0 else if hi < 0.3 then 2 else 1
              if rng.next() < 0.2 then (base + 1) % 3 else base
            }
            blobs(tone) += Util.trs(px, py, pz, rng.range(0.0, 3.0), rng.range(0.0, 3.0), rng.range(0.0, 3.0),
              r, r * rng.range(0.7, 0.92), r)
          end if
        }
        if spot.collide then
          ctx.collide(spot.x - trunkR * 1.7, spot.z - trunkR * 1.7,
            spot.x + trunkR * 1.7, spot.z + trunkR * 1.7, spot.y + trunkH)
      }

      val wood = new Mesh(Util.bake(woodParts.toList),
        Toon.cel(Toon.CelOptions(color = Pal.trunkDark, bands = 3, tint = 0x6f5a80)))
      wood.castShadow = true
      wood.receiveShadow = true
      wood.name = "groveWood"
      ctx.add(wood)

      val blobGeo = new IcosahedronGeometry(1.0, 1.0)
      blobs.zipWithIndex.foreach { case (list, i) =>
        if list.nonEmpty then
          val inst = new InstancedMesh(blobGeo,
            Toon.cel(Toon.CelOptions(color = GreenTones(i), bands = 3, tint = 0x5b6f8c)), list.length.toDouble)
          list.zipWithIndex.foreach { case (m, k) => inst.setMatrixAt(k.toDouble, m) }
          inst.castShadow = true
          inst.receiveShadow = false
          inst.name = s"groveCanopy$i"
          ctx.add(inst)
      }
      wood

  def buildCedar(ctx: WorldCtx, spots: Vector[CedarSpot]): Mesh[?, ?, ?] =
    if spots.isEmpty then null
    else
      val woodParts = mutable.ArrayBuffer.empty[Util.BakePart]
      val tiers     = Vector.fill(3)(mutable.ArrayBuffer.empty[Matrix4])
      // hard taper on the stem, six sides; the cone is base-at-origin
      val trunkGeo = new CylinderGeometry(0.34, 1.0, 1.0, 6.0, 1.0)
      val coneGeo  = new ConeGeometry(1.0, 1.0, 7.0, 1.0)
      coneGeo.translate(0.0, 0.5, 0.0)

      spots.foreach { spot =>
        val rng     = Util.rngKit(spot.seed)
        val s       = spot.scale
        val lean    = spot.lean
        val leanDir = spot.leanDir.getOrElse(rng.range(0.0, math.Pi * 2.0))

        val height = 10.6 * s * rng.range(0.82, 1.18)
        val trunkR = 0.125 * s * rng.range(0.86, 1.22)

        // the stem's axis; everything is placed along it from the foot
        val eul  = new Euler(lean * math.sin(leanDir), 0.0, -lean * math.cos(leanDir))
        val axis = new Vector3(0.0, 1.0, 0.0).applyEuler(eul)
        def at(h: Double): Vector3 =
          new Vector3(spot.x, spot.y, spot.z).addScaledVector(ThreeInterop.asVector3Like(axis), h)

        val c = at(height / 2.0)
        woodParts += Util.BakePart(trunkGeo,
          Some(Util.trs(c.x, c.y, c.z, eul.x, 0.0, eul.z, trunkR, height, trunkR)))
        val f = at(0.3 * s)
        woodParts += Util.BakePart(trunkGeo,
          Some(Util.trs(f.x, f.y, f.z, 0.0, 0.0, 0.0,
            trunkR * 1.9, 0.6 * s, trunkR * 1.9)))

        // the crown: a stack of overlapping cones, lighter at the top
        val base  = height * rng.range(0.30, 0.42)
        val crown = height - base
        val n     = 6 + rng.int(0, 2)
        val rMax  = height * 0.150 * rng.range(0.86, 1.14)

        (0 until n).foreach { k =>
          val u   = k.toDouble / n
          val hk  = base + crown * (u + rng.range(-0.03, 0.03))
          val ck  = (crown / n) * 2.15 * rng.range(0.86, 1.14)
          val rk  = rMax * math.pow(1.0 - u, 0.82) * rng.range(0.88, 1.1)
          val p   = at(hk)
          val off = rng.range(0.0, 6.28)
          val wob = rk * 0.09
          // a per-tier ellipse and a couple of degrees of tilt break every rim
          // out of the horizontal, so the tree is not a stack of discs
          val ell  = rng.range(0.84, 1.18)
          val tx   = eul.x + rng.range(-0.07, 0.07)
          val tz   = eul.z + rng.range(-0.07, 0.07)
          var tone = if u > 0.62 then 0 else if u < 0.26 then 2 else 1
          if rng.chance(0.18) then tone = (tone + 1) % 3
          tiers(tone) += Util.trs(
            p.x + math.cos(off) * wob, p.y, p.z + math.sin(off) * wob,
            tx, rng.range(0.0, 6.28), tz,
            rk, ck, rk * ell
          )
        }
        // the leader: a slim cone finishing the point
        val p = at(base + crown * 0.86)
        tiers(0) += Util.trs(p.x, p.y, p.z, eul.x, rng.range(0.0, 6.28), eul.z,
          rMax * 0.30, crown * 0.30 * rng.range(0.9, 1.25), rMax * 0.30)
        // two sprigs, so no two crowns have the same outline
        (0 until 2).foreach { _ =>
          val u  = rng.range(0.15, 0.8)
          val p2 = at(base + crown * u)
          val rk = rMax * (1.0 - u) * rng.range(0.45, 0.8)
          val a  = rng.range(0.0, 6.28)
          tiers(if rng.chance(0.5) then 1 else 2) += Util.trs(
            p2.x + math.cos(a) * rk * 0.55, p2.y, p2.z + math.sin(a) * rk * 0.55,
            eul.x, rng.range(0.0, 6.28), eul.z,
            rk, rk * rng.range(1.3, 2.0), rk
          )
        }

        if spot.collide then
          val r = trunkR * 1.7
          ctx.collide(spot.x - r, spot.z - r, spot.x + r, spot.z + r, spot.y + height * 0.9)
      }

      val wood = new Mesh(Util.bake(woodParts.toList),
        Toon.cel(Toon.CelOptions(color = Pal.cedarBark, bands = 3, tint = 0x6f5a80)))
      wood.castShadow = true
      wood.receiveShadow = true
      wood.name = "cedarWood"
      ctx.add(wood)

      tiers.zipWithIndex.foreach { case (list, i) =>
        if list.nonEmpty then
          val inst = new InstancedMesh(coneGeo,
            Toon.cel(Toon.CelOptions(color = CedarTones(i), bands = 3, tint = 0x59657f)), list.length.toDouble)
          list.zipWithIndex.foreach { case (m, k) => inst.setMatrixAt(k.toDouble, m) }
          inst.castShadow = true
          inst.receiveShadow = false
          inst.name = s"cedarCanopy$i"
          ctx.add(inst)
      }
      wood

  def buildBamboo(ctx: WorldCtx, clumps: Vector[BambooClump]): Boolean =
    if clumps.isEmpty then false
    else
      val culmGeo = new CylinderGeometry(1.0, 1.16, 1.0, 5.0, 1.0)
      val nodeGeo = new CylinderGeometry(1.25, 1.25, 1.0, 5.0, 1.0)
      val culms   = Vector(mutable.ArrayBuffer.empty[Matrix4], mutable.ArrayBuffer.empty[Matrix4])
      val nodes   = mutable.ArrayBuffer.empty[Matrix4]
      val leaves  = Vector(mutable.ArrayBuffer.empty[Matrix4], mutable.ArrayBuffer.empty[Matrix4])

      clumps.foreach { clump =>
        val rng = Util.rngKit(clump.seed)
        (0 until clump.n).foreach { i =>
          val s     = clump.scale
          val h     = (4.2 + rng.range(-1.0, 1.4)) * s
          val r     = 0.045 * s * rng.range(0.85, 1.2)
          val px    = clump.x + rng.range(-1.0, 1.0) * clump.spread
          val pz    = clump.z + rng.range(-1.0, 1.0) * clump.spread
          val lean  = rng.range(-0.07, 0.07)
          val leanD = rng.range(0.0, 6.3)
          val tone  = i % 2
          culms(tone) += Util.trs(px, clump.y + h / 2.0, pz,
            lean * math.sin(leanD), 0.0, -lean * math.cos(leanD), r, h, r)
          (1 until 5).foreach { k =>
            nodes += Util.trs(
              px + lean * math.sin(leanD) * (h * (k.toDouble / 5.0) - h / 2.0),
              clump.y + h * (k.toDouble / 5.0),
              pz - lean * math.cos(leanD) * (h * (k.toDouble / 5.0) - h / 2.0),
              0.0, 0.0, 0.0, r, 0.035 * s, r
            )
          }
          // leaf spray: a few flattened blobs high on the culm
          (0 until 4).foreach { k =>
            val ly = clump.y + h * rng.range(0.66, 1.0)
            val rr = 0.34 * s * rng.range(0.7, 1.2)
            leaves(k % 2) += Util.trs(
              px + rng.range(-0.5, 0.5) * s, ly, pz + rng.range(-0.5, 0.5) * s,
              rng.range(0.0, 3.0), rng.range(0.0, 3.0), rng.range(0.0, 3.0),
              rr, rr * 0.42, rr * 1.25
            )
          }
        }
      }

      val culmTone = Vector(Pal.bamboo, Pal.bambooDeep)
      culms.zipWithIndex.foreach { case (list, i) =>
        if list.nonEmpty then
          val inst = new InstancedMesh(culmGeo,
            Toon.cel(Toon.CelOptions(color = culmTone(i), bands = 3, tint = 0x5b6f8c)), list.length.toDouble)
          list.zipWithIndex.foreach { case (m, k) => inst.setMatrixAt(k.toDouble, m) }
          inst.castShadow = true
          ctx.add(inst)
      }
      if nodes.nonEmpty then
        val inst = new InstancedMesh(nodeGeo,
          Toon.cel(Toon.CelOptions(color = 0xb8c88a, bands = 2, tint = 0x5b6f8c)), nodes.length.toDouble)
        nodes.zipWithIndex.foreach { case (m, k) => inst.setMatrixAt(k.toDouble, m) }
        ctx.add(inst)
      val leafGeo = new IcosahedronGeometry(1.0, 0.0)
      leaves.zipWithIndex.foreach { case (list, i) =>
        if list.nonEmpty then
          val inst = new InstancedMesh(leafGeo,
            Toon.cel(Toon.CelOptions(color = if i == 1 then Pal.bambooDeep else 0xa6c078,
              bands = 3, tint = 0x5b6f8c)),
            list.length.toDouble)
          list.zipWithIndex.foreach { case (m, k) => inst.setMatrixAt(k.toDouble, m) }
          inst.castShadow = true
          ctx.add(inst)
      }
      true

  def buildShrubs(ctx: WorldCtx, spots: Vector[ShrubSpot]): Unit =
    val geo   = new IcosahedronGeometry(1.0, 1.0)
    val tones = Vector(Pal.leaf, Pal.leafDeep, Pal.leafPale)
    val lists = Vector.fill(3)(mutable.ArrayBuffer.empty[Matrix4])
    spots.foreach { spot =>
      val rng = Util.rngKit(spot.seed)
      (0 until spot.count).foreach { i =>
        val r = spot.r * rng.range(0.75, 1.2)
        lists(i % 3) += Util.trs(
          spot.x + rng.range(-0.5, 0.5) * spot.spread,
          spot.y + r * 0.72,
          spot.z + rng.range(-0.5, 0.5) * spot.spread,
          rng.range(0.0, 3.0), rng.range(0.0, 3.0), rng.range(0.0, 3.0),
          r, r * 0.8, r
        )
      }
    }
    lists.zipWithIndex.foreach { case (list, i) =>
      if list.nonEmpty then
        val inst = new InstancedMesh(geo,
          Toon.cel(Toon.CelOptions(color = tones(i), bands = 3, tint = 0x5b6f8c)), list.length.toDouble)
        list.zipWithIndex.foreach { case (m, k) => inst.setMatrixAt(k.toDouble, m) }
        inst.castShadow = true
        inst.receiveShadow = true
        ctx.add(inst)
    }
  end buildShrubs
end Trees
