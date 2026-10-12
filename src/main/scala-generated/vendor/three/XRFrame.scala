// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js

@js.native
trait XRFrame extends js.Object:
  def detectedMeshes: js.UndefOr[XRMeshSet] = js.native
  def detectedPlanes: js.UndefOr[XRPlaneSet] = js.native
  def predictedDisplayTime: Double = js.native
  def session: XRSession = js.native
  def trackedAnchors: js.UndefOr[XRAnchorSet] = js.native
  def createAnchor: js.UndefOr[js.Function2[XRRigidTransform, XRSpace, js.Promise[XRAnchor]]] = js.native
  def getDepthInformation(view: XRView): js.UndefOr[XRCPUDepthInformation | Null] = js.native
  def getHitTestResults(hitTestSource: XRHitTestSource): js.Array[XRHitTestResult] = js.native
  def getHitTestResultsForTransientInput(hitTestSource: XRTransientInputHitTestSource): js.Array[XRTransientInputHitTestResult] = js.native
  def getJointPose: js.UndefOr[js.Function2[XRJointSpace_9cf08a39, XRSpace, js.UndefOr[XRJointPose]]] = js.native
  def getPose(space: XRSpace, baseSpace: XRSpace): js.UndefOr[XRPose] = js.native
  def getViewerPose(referenceSpace: XRReferenceSpace): js.UndefOr[XRViewerPose] = js.native

object XRFrame:
  def of(
    getDepthInformation: js.Function1[XRView, js.UndefOr[XRCPUDepthInformation | Null]],
    getHitTestResults: js.Function1[XRHitTestSource, js.Array[XRHitTestResult]],
    getHitTestResultsForTransientInput: js.Function1[XRTransientInputHitTestSource, js.Array[XRTransientInputHitTestResult]],
    getPose: js.Function2[XRSpace, XRSpace, js.UndefOr[XRPose]],
    getViewerPose: js.Function1[XRReferenceSpace, js.UndefOr[XRViewerPose]],
    predictedDisplayTime: Double,
    session: XRSession,
    createAnchor: js.UndefOr[js.Function2[XRRigidTransform, XRSpace, js.Promise[XRAnchor]]] = js.undefined,
    detectedMeshes: js.UndefOr[XRMeshSet] = js.undefined,
    detectedPlanes: js.UndefOr[XRPlaneSet] = js.undefined,
    getJointPose: js.UndefOr[js.Function2[XRJointSpace_9cf08a39, XRSpace, js.UndefOr[XRJointPose]]] = js.undefined,
    trackedAnchors: js.UndefOr[XRAnchorSet] = js.undefined
  ): XRFrame =
    // #COMPLETION_DRIVE: sanctioned construction boundary for native facade trait
    // #SUGGEST_VERIFY: runtime shape test verifies dictionary keys and receiver round-trip
    val _res = js.Dynamic.literal(
      "getDepthInformation" -> getDepthInformation.asInstanceOf[js.Any],
      "getHitTestResults" -> getHitTestResults.asInstanceOf[js.Any],
      "getHitTestResultsForTransientInput" -> getHitTestResultsForTransientInput.asInstanceOf[js.Any],
      "getPose" -> getPose.asInstanceOf[js.Any],
      "getViewerPose" -> getViewerPose.asInstanceOf[js.Any],
      "predictedDisplayTime" -> predictedDisplayTime.asInstanceOf[js.Any],
      "session" -> session.asInstanceOf[js.Any]
    )
    createAnchor.foreach(v => _res.updateDynamic("createAnchor")(v.asInstanceOf[js.Any]))
    detectedMeshes.foreach(v => _res.updateDynamic("detectedMeshes")(v.asInstanceOf[js.Any]))
    detectedPlanes.foreach(v => _res.updateDynamic("detectedPlanes")(v.asInstanceOf[js.Any]))
    getJointPose.foreach(v => _res.updateDynamic("getJointPose")(v.asInstanceOf[js.Any]))
    trackedAnchors.foreach(v => _res.updateDynamic("trackedAnchors")(v.asInstanceOf[js.Any]))
    _res.asInstanceOf[XRFrame]
