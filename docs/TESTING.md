# Beta test matrix

## Confirmed on one Original Bike / Android 11 tablet

- APK installation through OpenPelo's bundled ADB.
- Transparent 1920 x 1080 accessibility overlay with the underlying window retaining focus.
- Taps across an isolated touch-test screen did not increment its underlying touch counter while locked; taps resumed after unlock.
- Automatic 30-second test timeout.
- Both-volume-buttons unlock after a continuous five-second hold, after clearing a conflicting TalkBack shortcut.
- Default Volume Down-only five-second ON/OFF toggling, confirmed by the device owner.
- ON/OFF popups and the illustrated menu on the device.
- Pure Kotlin timing checks for single-key and dual-key holds, repeats, releases, one toggle per hold, interrupted overlap, and separation from the two-key TalkBack gesture.

## Implemented, but not fully verified on hardware

- Three-finger eight-second emergency escape.
- Unlock on physical display power-off.
- Notification action and connected-computer force-stop escape.
- Every 30/60/90/120-minute maximum-duration boundary.
- Short Volume Down press changes media volume by exactly one step; a completed five-second hold should not change it.
- Retained key-event routing when lock state changes during a held key.

## Still needed before claiming a workout fix

- Continuous class video and audio during lock/unlock.
- Cadence, resistance, output, leaderboard, and ride recording continue normally.
- Real sweat, multiple contacts, edge swipes, and system dialogs do not activate workout controls.
- Emergency recovery outside a workout, then during a disposable short test.
- Behavior after service/process restart and reboot, with protection starting OFF.
- Installation/update/uninstall on other tablet and firmware versions.

Please report exactly which rows you tested. A successful install or APK metadata probe does not establish working playback or safe hardware-key behavior. No other Peloton model or Android version has been certified by this project.
