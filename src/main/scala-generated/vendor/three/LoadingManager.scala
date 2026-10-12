// Auto-generated Scala.js facade derived from TypeScript surface shim.
// Source shim: src/main/shims/vendor/three.ts
// DO NOT EDIT DIRECTLY. Changes will be overwritten during regeneration.

package vendor.three

import scala.scalajs.js
import scala.scalajs.js.annotation.*

@js.native
@JSImport("three", "LoadingManager")
class LoadingManager(onLoad_1: js.UndefOr[js.Function0[Unit]] = js.undefined, onProgress_1: js.UndefOr[js.Function3[String, Double, Double, Unit]] = js.undefined, onError_1: js.UndefOr[js.Function1[String, Unit]] = js.undefined) extends js.Object:
  var abortController: org.scalajs.dom.AbortController = js.native
  var onStart: js.UndefOr[js.Function3[String, Double, Double, Unit]] = js.native
  def abort(): LoadingManager = js.native
  def addHandler(regex: js.RegExp, loader: Loader[js.Any, String]): LoadingManager = js.native
  def getHandler(file: String): Loader[js.Any, String] | Null = js.native
  def itemEnd(url: String): Unit = js.native
  def itemError(url: String): Unit = js.native
  def itemStart(url: String): Unit = js.native
  def onError(url: String): Unit = js.native
  def onLoad(): Unit = js.native
  def onProgress(url: String, loaded: Double, total: Double): Unit = js.native
  def removeHandler(regex: js.RegExp): LoadingManager = js.native
  def resolveURL(url: String): String = js.native
  def setURLModifier(callback: js.UndefOr[js.Function1[String, String]] = js.undefined): LoadingManager = js.native
