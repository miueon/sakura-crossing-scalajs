# Sakura Crossing — Scala 3 + Scala.js + Laminar + Three.js Showcase

**[Live Interactive Demo → https://miueon.github.io/sakura-crossing-scalajs/](https://miueon.github.io/sakura-crossing-scalajs/)**

A 1:1 procedural anime-NPR 3D Japanese neighborhood and railway crossing built in **Scala 3.8**, **Scala.js 1.22**, **Laminar 17**, **Ultraviolet** (typed Scala 3 → GLSL shader DSL), and **Three.js r185** — with **zero external 3D model or texture assets** and **zero hand-written JavaScript facade boilerplate**.

> **Early Flagship Showcase for _Facade Contract_ (Coming Soon)**
>
> Historically, building rich Three.js or modern TypeScript-ecosystem applications in Scala.js meant hand-writing and maintaining thousands of lines of brittle `@js.native` facade traits.
>
> This repository is an early real-world stress test and preview of our upcoming **Facade Contract** toolchain. From a single two-line shim ([`src/main/shims/vendor/three.ts`](src/main/shims/vendor/three.ts)) containing `export * from "three";`, **Facade Contract** automatically synthesized and contract-verified all **1,042 Scala 3 facade files** committed under [`src/main/scala-generated/vendor/three/`](src/main/scala-generated/vendor/three/).
>
> **Facade Contract** is currently in active pre-release development and will be open-sourced soon — stay tuned!

---

## Screenshots (Live Scala.js + WebGL Renders)

| **1. 桜坂踏切 — The Level Crossing** | **2. さくら坂商店街 — Sakurazaka Shotengai** |
| :---: | :---: |
| ![Hero 1: The Level Crossing](docs/screenshots/hero-1-crossing.png) | ![Hero 2: Sakurazaka Shotengai](docs/screenshots/hero-2-shotengai.png) |
| **3. 湯の坂温泉街 — Yunosaka Onsen** | **4. 桜守神社 — Sakuramori Shrine** |
| ![Hero 3: Yunosaka Onsen](docs/screenshots/hero-3-onsen.png) | ![Hero 4: Sakuramori Shrine](docs/screenshots/hero-4-shrine.png) |

---

## What Makes This Showcase Special

### 1. Whole-Package `export * from "three"` -> 1,042 Scala 3 Facades
Look inside [`src/main/shims/vendor/three.ts`](src/main/shims/vendor/three.ts):

```ts
/// <reference types="facade-contract/shim-v2" />
const _pkg = Scala.package("vendor.three", { module: "three" });
export * from "three";
```

Nothing in `three` was hand-curated for this project. Every `WebGLRenderer`, `WebGLRenderTarget`, `DepthTexture`, `MeshToonMaterial`, `DataTexture`, `InstancedMesh`, `BufferGeometry`, `DirectionalLight`, `Matrix4`, and `Quaternion` used across the 28 Scala world and rendering modules comes directly from the 1,042 generated Scala 3 sources in [`src/main/scala-generated/vendor/three/`](src/main/scala-generated/vendor/three/). Because those generated `.scala` sources are committed in this repository, you can clone and run `sbt fullLinkJS` out of the box without needing the unpublished generator toolchain installed locally.

### 2. Typed Scala 3 Shaders via Ultraviolet
All custom GLSL shaders are authored in Scala 3 using [**Ultraviolet**](https://github.com/PurpleKingdomGames/ultraviolet) and compiled to GLSL at compile time:
- **`PostPipeline.scala`** — A 3-pass offscreen `WebGLRenderTarget` + `DepthTexture` post-processing pipeline:
  1. **Screen-space depth-second-difference ink pass (`inkFragmentShader`)**: linearizes depth and detects convex crease edges across pixel neighborhoods.
  2. **Anime color-grading pass (`gradeFragmentShader`)**: applies cool violet shadow tinting (`0xada8d0`), warm sunlight highlights (`0xfff7e8`), saturation boost, vignette, and linear-to-sRGB gamma encoding.
  3. **Edge-directed FXAA pass (`fxaaFragmentShader`)**: smooths high-contrast toon and outline edges.
- **`core/Outline.scala` & `core/Toon.scala`** — Clip-space smoothed-normal inverted-hull outline shells (`hullOutline`) paired with 3- and 4-band `DataTexture` `MeshToonMaterial` cel shading and Ultraviolet-patched violet shadow tinting (`lights_toon_pars_fragment`).

### 3. True Spherical Planet Bake ($R = 160\text{ m}$) & Zero External Assets
- **Spherical World (`world/Planet.scala`)**: All 11 districts (`Street`, `Railway`, `Train`, `Shop`, `Shrine`, `Shotengai`, `RestCorner`, `Library`, `NorthBlock`, `Matsuri`, `Onsen`, `Alleys`, `District`, `Traffic`, `Details`) are authored in flat $(x, y, z)$ coordinates and CPU-baked onto a true $R = 160\text{ m}$ sphere (`bakeToPlanet` with edge subdivision at $\le 4.0\text{ m}$), producing **1,379 top-level scene nodes, 7,980 meshes, 9,667 instanced elements, 2,298,028 vertices, and 753,239 baked triangles**.
- **65+ Procedural Canvas2D Textures (`core/Textures.scala`)**: Every Japanese shop fascia (`青空商店`, `古本 つばめ書房`, `クリーニング しらゆき`, `さくら庵`, `こけし堂`, `ひばり台図書館`, `米・酒 なかの`), noren curtain (`そば`, `ゆ`), festival banner (`八月十五日`, `射的`), road marking (`止まれ`, `踏切注意`), license plate (`さ 21-08`), and wood/tatami/shoji grain is painted procedurally in Scala via HTML5 Canvas2D.
- **Laminar 17 Reactive Controls (`app/`)**: Reactive `Var`/`Signal` state drives keyboard locomotion (`WASD` / Arrow keys, `Shift` sprint, canal water swimming state), 9 camera presets (`1`–`9`), and clean WebGL/DOM listener lifecycle teardown.

---

## Structural & Pixel-Level Parity Verification

This port is verified against the upstream reference implementation ([`Kenton-GMI/sakura-crossing`](https://github.com/Kenton-GMI/sakura-crossing)) at two levels:

1. **395 MUnit Scene-Graph Oracle Tests (`sbt test`)**: Verify per-child mesh counts, instanced counts, vertex counts, multi-material group slices, shadow flags, 6-tuple world bounding boxes (`±0.001 m`), collider boxes, platforms, and deterministic RNG sequences across every world builder.
2. **Live Dual-Engine 1600×900 Playwright Pixel Gate (`pnpm test`)**: Boots both the upstream JS engine and this Scala.js build side-by-side in the same headless Chromium instance via `window.__shot` and measures per-channel pixel differences:

| Hero Viewpoint | Full-Frame Mean Abs Diff (`/ 255`) | Foreground ROI Mean Abs Diff (`/ 255`) | ROI Pixel Match Ratio | Luma Structure Correlation ($r$) |
| :--- | :---: | :---: | :---: | :---: |
| **Hero-1: The Level Crossing** | **0.58** | **0.67** | **99.0%** | **0.993** |
| **Hero-2: Sakurazaka Shotengai** | **0.57** | **0.39** | **99.5%** | **0.995** |
| **Hero-3: Yunosaka Onsen** | **0.24** | **0.19** | **99.7%** | **0.994** |
| **Hero-4: Sakuramori Shrine** | **0.38** | **0.09** | **99.5%** | **0.999** |

---

## Quick Start (Local Development)

### Prerequisites
- **JDK 21+** and **sbt 2.x** (or `sbt` launcher)
- **Node.js 22+** and **pnpm**

### 1. Install dependencies & run the dev server
```bash
pnpm install
sbt fastLinkJS
pnpm run dev
```
Then open **[http://localhost:5274/](http://localhost:5274/)**.

### 2. Run the 395 MUnit structural parity tests
```bash
sbt "testOnly *"
```

### 3. Build the production static bundle (`dist/`)
```bash
pnpm run build
pnpm run preview
```

---

## Credits & Attribution

- Original 3D world design, procedural art direction, and reference implementation by [**Kenton-GMI/sakura-crossing**](https://github.com/Kenton-GMI/sakura-crossing).
- Scala 3 / Scala.js / Laminar / Ultraviolet port and **Facade Contract** whole-package Three.js showcase by [**@miueon**](https://github.com/miueon).
