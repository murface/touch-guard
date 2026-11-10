# Touch Guard

Keep sweat from tapping your Peloton screen. Touch Guard places a clear touch-blocking overlay over your ride, with a physical-button shortcut to turn it on or off.

**Current beta: [v0.2.0-preview14](https://github.com/murface/touch-guard/releases/tag/v0.2.0-preview14).** Tested on one Original Bike running Android 11. Social Mode supports the inspected Peloton workout app version 2.8.3457 (versionCode 2083457); other versions and bike models are unverified. Independent community project, not affiliated with Peloton or OpenPelo.

## Two modes, one app

Choose the mode in Touch Guard's settings:

| Mode | What you can touch |
| --- | --- |
| **Full Lock** | No touchscreen controls while locked. |
| **Social Mode** | Supported leaderboard avatars for high-fives, leaderboard page scrolling, incoming high-five/milestone hand buttons, the music-note card, and song hearts. Other touches remain blocked, including pause, metrics and the time bar. |

Social Mode identifies specific controls locally rather than opening a rectangle of the screen. Unsupported workout screens stay fully blocked until supported controls are recognized. Leaderboard swipes move one page at a time; high-fives show local feedback and may not show Peloton's usual animation. A confirmation means the control's action was accepted, not independent delivery verification.

## Everyday use

1. Install and enable the **single Touch Guard accessibility service**. Turn its top **Use Touch Guard** switch **ON**, and leave Android's separate **Touch Guard shortcut** **OFF**.
2. Choose **Full Lock** or **Social Mode**, and a maximum duration longer than your planned ride.
3. Open your class normally. Hold **Volume Down alone for five seconds**, then release completely, to turn protection ON. Repeat to turn it OFF.

The default maximum is **2 hours**, with 30, 60, 90, 120, 180, 240 or 360 minutes available. Normal activation uses this duration immediately. **There is no required first-lock test and no automatic 30-second preview.** The separate **Test Touch Guard** screen offers an optional 30-second test.

ON/OFF messages confirm status without a persistent corner banner. ON says “Sweat away and go get the PR 💪 🙌”. After turning protection OFF, **the screen may need to be completely dry, with dry hands, before touch controls work normally**.

[Detailed installation, updating and recovery](docs/INSTALLATION.md) · [Release notes](docs/RELEASE_NOTES.md) · [Testing and limits](docs/TESTING.md) · [Privacy](PRIVACY.md)

## What has been checked

On the owner's Original Bike: Full Lock and the hardware toggle; Social Mode leaderboard scrolling, outgoing avatar high-fives, incoming replies, music-card opening and song hearts; long-duration configuration without the former 30-second gate; and signed updates. The owner reports successful ride use. Extended testing of the newest Social Mode build, every escape path, other tablets and future Peloton updates remains outstanding. See the test matrix for the distinction between owner reports, device inspection and automated tests.

## Privacy and permissions

No Internet permission, uploads, ads, analytics or account login. Full Lock does not inspect screen content. Social Mode reads known Peloton control IDs, bounds and relevant high-five wording locally during protected gestures, without saving that screen content. One Android accessibility permission covers both modes. Neither mode changes Peloton's app, firmware or workout records.

## Build

JDK 17, Android SDK 35, build-tools 35.0.0, Gradle 8.11.1, AGP 8.9.1 and Kotlin 2.1.20:

```sh
./gradlew :app:assembleDebug :app:testDebugUnitTest
```

Development APKs from CI use a different signing certificate from the published release. For updates, use the signed release APK. Private signing keys are not included. Release signing uses the `TOUCHGUARD_KEYSTORE`, `TOUCHGUARD_STORE_PASSWORD`, `TOUCHGUARD_KEY_ALIAS` and `TOUCHGUARD_KEY_PASSWORD` environment variables defined in the build configuration.

MIT licensed. [Third-party notices](THIRD_PARTY_NOTICES.md).
