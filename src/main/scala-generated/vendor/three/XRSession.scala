// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait XRSession extends org.scalajs.dom.EventTarget:
  def depthActive: js.UndefOr[Boolean] = js.native
  def depthDataFormat: js.UndefOr["float32" | "luminance-alpha" | "unsigned-short"] = js.native
  def depthType: js.UndefOr["raw" | "smooth"] = js.native
  def depthUsage: js.UndefOr["cpu-optimized" | "gpu-optimized"] = js.native
  def domOverlayState: js.UndefOr[XRDOMOverlayState] = js.native
  def enabledFeatures: js.UndefOr[js.Array[String]] = js.native
  def environmentBlendMode: "additive" | "alpha-blend" | "opaque" = js.native
  def frameRate: js.UndefOr[Double] = js.native
  def inputSources: XRInputSourceArray = js.native
  def interactionMode: js.UndefOr["screen-space" | "world-space"] = js.native
  def isSystemKeyboardSupported: Boolean = js.native
  def pauseDepthSensing: js.UndefOr[js.Function0[Unit]] = js.native
  def persistentAnchors: js.UndefOr[js.Array[String]] = js.native
  def renderState: XRRenderState = js.native
  def resumeDepthSensing: js.UndefOr[js.Function0[Unit]] = js.native
  def supportedFrameRates: js.UndefOr[js.typedarray.Float32Array] = js.native
  def visibilityState: "hidden" | "visible" | "visible-blurred" = js.native
  def cancelAnimationFrame(id: Double): Unit = js.native
  def deletePersistentAnchor: js.UndefOr[js.Function1[String, js.Promise[Unit]]] = js.native
  def end(): js.Promise[Unit] = js.native
  def initiateRoomCapture: js.UndefOr[js.ThisFunction0[XRSession, js.Promise[js.UndefOr[Nothing]]]] = js.native
  def onend(evt: XRSessionEvent): js.Any = js.native
  def onframeratechange(evt: XRSessionEvent): js.Any = js.native
  def oninputsourceschange(evt: XRInputSourcesChangeEvent): js.Any = js.native
  def onselect(evt: XRInputSourceEvent): js.Any = js.native
  def onselectend(evt: XRInputSourceEvent): js.Any = js.native
  def onselectstart(evt: XRInputSourceEvent): js.Any = js.native
  def onsqueeze(evt: XRInputSourceEvent): js.Any = js.native
  def onsqueezeend(evt: XRInputSourceEvent): js.Any = js.native
  def onsqueezestart(evt: XRInputSourceEvent): js.Any = js.native
  def onvisibilitychange(evt: XRSessionEvent): js.Any = js.native
  def requestAnimationFrame(callback: js.Function2[Double, XRFrame, Unit]): Double = js.native
  def requestHitTest: js.UndefOr[js.Function2[XRRay, XRReferenceSpace, js.UndefOr[js.Promise[js.Array[XRHitResult]]]]] = js.native
  def requestHitTestSource: js.UndefOr[js.Function1[XRHitTestOptionsInit, js.UndefOr[js.Promise[XRHitTestSource]]]] = js.native
  def requestHitTestSourceForTransientInput: js.UndefOr[js.Function1[XRTransientInputHitTestOptionsInit, js.UndefOr[js.Promise[XRTransientInputHitTestSource]]]] = js.native
  def requestReferenceSpace(`type`: "bounded-floor" | "local" | "local-floor" | "unbounded" | "viewer"): js.Promise[XRBoundedReferenceSpace | XRReferenceSpace] = js.native
  def restorePersistentAnchor: js.UndefOr[js.Function1[String, js.Promise[XRAnchor]]] = js.native
  def updateRenderState(renderStateInit: js.UndefOr[XRRenderStateInit] = js.undefined): js.Promise[Unit] = js.native
  def updateTargetFrameRate(rate: Double): js.Promise[Unit] = js.native
// #WARN: SHIM-065: Native facade trait 'XRSession' extends 'org.scalajs.dom.EventTarget' but parent owner cannot be resolved in the generation unit
