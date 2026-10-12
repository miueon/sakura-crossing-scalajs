package sakura.app

import com.raquo.laminar.api.L.*

private[sakura] object HudComponents:
  def apply(
    playerState: Signal[PlayerState],
    sceneMounted: Signal[Boolean],
    onSelectPreset: CameraPreset => Unit,
    onToggleScene: () => Unit
  ): HtmlElement =
    div(
      className := "hud",
      h1("Sakura Crossing"),
      dl(
        dt("Viewpoint"),
        dd(
          dataAttr("testid") := "active-preset",
          child.text <-- playerState.map(_.activePreset.label)
        ),
        dt("Position"),
        dd(
          dataAttr("testid") := "player-position",
          child.text <-- playerState.map(formatPosition)
        ),
        dt("Locomotion"),
        dd(
          dataAttr("testid") := "locomotion-state",
          child.text <-- playerState.map(_.locomotion.label)
        ),
        dt("Scene"),
        dd(child.text <-- sceneMounted.map(if _ then "Running" else "Paused"))
      ),
      p(
        className := "controls-guide",
        "WASD / arrows move · Shift sprint · 1–9 viewpoints"
      ),
      div(
        className          := "camera-presets",
        role := "group",
        aria.label := "Camera viewpoints",
        CameraPreset.all.map { preset =>
          val active = playerState.map(_.activePreset == preset)
          button(
            dataAttr("preset") := preset.key.toString,
            aria.label := s"${preset.key}. ${preset.label}",
            aria.pressed <-- active.map(isActive => if isActive then "true" else "false"),
            className <-- active.map(isActive =>
              if isActive then "camera-preset is-active" else "camera-preset"
            ),
            onClick --> (_ => onSelectPreset(preset)),
            preset.buttonLabel
          )
        }
      ),
      button(
        dataAttr("testid") := "scene-toggle",
        aria.pressed <-- sceneMounted.map(isMounted => if isMounted then "true" else "false"),
        className := "scene-toggle",
        child.text <-- sceneMounted.map(if _ then "Pause scene" else "Resume scene"),
        onClick --> (_ => onToggleScene())
      )
    )

  private def formatPosition(state: PlayerState): String =
    f"${state.position.x}%.2f, ${state.position.y}%.2f, ${state.position.z}%.2f"
end HudComponents
