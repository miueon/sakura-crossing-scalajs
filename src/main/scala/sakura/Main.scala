package sakura

import com.raquo.laminar.api.L.*
import org.scalajs.dom

/** The `#app` mount point is a hard contract with index.html: a missing one is a build or deploy
  * defect, not a condition to degrade from into a blank page.
  */
object Main:
  def main(args: Array[String]): Unit =
    val mount = Option(dom.document.getElementById("app")).getOrElse(
      throw new IllegalStateException("sakura-crossing: index.html is missing the #app mount point")
    )
    renderOnDomContentLoaded(mount, SakuraApp())
