# v0.2.0-preview14 — Full Lock + Social Mode beta

Consolidates the locally tested updates since public preview2.

- One app and one accessibility service, with **Full Lock** or **Social Mode** selected in settings.
- Social Mode: recognized avatar high-fives, incoming high-five/milestone replies, leaderboard page scrolling, music-card opening and song hearts. Pause, metrics, time-bar and unrelated touches stay blocked.
- Corrected workout-window lookup, hierarchy flags, gesture timing, music-control matching and incoming-reply targeting. Unsupported screens remain blocked and supported controls can become available when recognized.
- **2-hour default**; selectable 30/60/90/120/180/240/360-minute maximum. Normal activation immediately uses this setting. Removed the mandatory first-lock hardware check and automatic 30-second preview.
- Optional separate **Test Touch Guard** screen retains a 30-second test timeout.
- Five-second Volume Down toggle, complete release between holds. Optional both-volume mode remains available.
- No persistent corner countdown. ON says “Sweat away and go get the PR 💪 🙌”; high-five acceptance feedback says “High five sent!”.
- OFF now reminds riders that the screen may need to be completely dry, with dry hands, before touch controls work normally.
- Local lock/shutoff diagnostics and recent Social results help explain failures without saving screen content.

Owner testing confirmed the listed social actions on one Original Bike with Android 11 and Peloton 2.8.3457 (versionCode 2083457). Other devices and future Peloton versions remain unverified. Extended testing of the newest Social build and the complete emergency-recovery matrix remain outstanding. High-five feedback reflects an accepted control action, not independent delivery verification.

No Internet permission, uploads, account login, rooting, firmware changes or Peloton app modifications. See [installation](INSTALLATION.md), [testing](TESTING.md) and [privacy](../PRIVACY.md).
