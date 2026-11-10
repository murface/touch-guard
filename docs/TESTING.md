# Touch Guard beta: evidence and limits

Current release: **v0.2.0-preview14**, Android API 30+. Hardware observations and owner reports below concern **one Original Bike running Android 11**, with Peloton workout app **2.8.3457 / versionCode 2083457**. They do not establish compatibility with every tablet, firmware or future app update.

## Confirmed through owner testing and device inspection

- Signed APK installation and updates using OpenPelo, preserving settings.
- Full Lock intercepting touches on the isolated test screen; touches resume after unlock.
- Default five-second Volume Down ON/OFF toggling, with full release between holds.
- Optional both-volume-buttons unlock after clearing a conflicting TalkBack shortcut.
- One app/service, mode selector, illustrated menu/settings, transient ON/OFF feedback.
- Social Mode leaderboard page scrolling and avatar high-fives. The owner reported return high-fives; the usual Peloton animation did not always appear. Local accepted-action feedback was added.
- Music-note opening, song hearts and incoming left-feed high-five/milestone replies, ultimately reported working by the owner after successive control-lookup corrections.
- Selected 120-minute normal lock timer verified directly on its first activation after removing the former hardware-test gate. This verifies timer assignment, not a two-hour endurance run.
- Owner reports successful ride use. This is not a complete, instrumented video/audio/metrics endurance certification of every Social Mode revision.
- After a sweaty ride, touch remained unresponsive after OFF; drying and a brief physical sleep/wake restored it. The cause was not conclusively isolated. Preview14 adds an OFF reminder about a completely dry screen and hands.

## Automated and build checks

Release assembly, unit tests and APK signature verification passed for preview14.
Tests cover physical-key hold/release/repeat/interruption behavior, social-control
policy and lock-duration rules, including immediate configured duration and the
separate optional 30-second test. The published APK uses the existing distribution
certificate; CI development builds use a different certificate.

## Still needs broader validation

- Extended use of the newest Social Mode build during a complete sweaty ride,
  including continuous playback/audio/metrics and accidental-touch resistance.
- Every configured 30/60/90/120/180/240/360-minute timeout in real elapsed time.
- Every notification, screen-off, computer-stop and restart/reboot recovery path.
- Optional three-finger eight-second escape under heavy sweat; OFF by default.
- All short-volume-press edge cases and key routing across Android variants.
- Other Peloton models, firmware, resolutions and workout-app versions.

The normal lock uses the selected duration immediately. The only 30-second lock
is the optional isolated **Test Touch Guard** screen. Report the exact versions,
mode, action and app-defined diagnostic when filing an issue. Do not include other
riders' private information, account data or signing material.
