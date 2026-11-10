# Touch Guard privacy

Applies to v0.2.0-preview14. Touch Guard is one app with one Android accessibility service and two modes selected in its settings.

**Full Lock** blocks touchscreen touches without inspecting screen content.

**Social Mode** reads known Peloton control IDs, positions and relevant high-five notification wording locally during protected gestures. It identifies supported avatar high-fives, incoming high-five/milestone replies, leaderboard page scrolling and music controls. Unsupported screens or controls remain blocked. Android grants content-access capability to the single service for both modes; the app only invokes social-control identification when Social Mode is selected or active.

No Internet permission, uploads, analytics, ads, account login or stored screen content. App settings and a short local diagnostic history are saved. Diagnostics contain only app-defined reason strings, control counts, gesture timing/movement metadata and lock/shutoff state; no rider names, song titles, notification wording, screenshots or other screen content. Transient target identities and a hash of notification wording are held in memory during a gesture to recheck that its target has not changed.

The service handles the volume shortcut and transparent blocking overlay. Android's separate accessibility shortcut is not needed. Neither mode edits Peloton's app, firmware or workout records. Android backup is disabled. Disabling the service stops its access; uninstalling clears its private settings and diagnostics.
