# Touch Guard

**Sweat away and get that PR!**

Touch Guard is a small, independent Android app that catches accidental touchscreen input on an indoor bike. It was made because sweat on a Peloton screen can trigger controls and pause a class mid-effort.

![Touch Guard cycling illustration](app/src/main/res/drawable-nodpi/ride_hero.png)

## Public beta — limited device testing

The first tested device is an **Original Peloton Bike, tablet PLTN-RB1V0-2, Android 11 (API 30)**. Touch blocking, automatic test timeout, and five-second hardware toggling were tested on one bike. **Continuous workout video, audio, metrics, and completed-ride behavior have not yet been verified.** Bike+, Tread, other tablet generations, other firmware, and newer Android versions are untested. Android versions below 11 cannot install this build.

This is an early community beta, not a promise of compatibility with every Peloton. It is not affiliated with or endorsed by Peloton or OpenPelo.

## Download and install

Download the signed APK from [Releases](https://github.com/murface/touch-guard/releases). Install it through [OpenPelo](https://github.com/doudar/Openpelo), using its **Install local APK** option. No separate ADB installation is needed for OpenPelo users. Setup requires a computer and developer debugging access to the bike; this is not a one-tap Play Store installation.

Read the [installation guide](docs/INSTALLATION.md) before your first test. Start with the bike idle.

## How to use it

1. Open Touch Guard and enable its accessibility service. Android's plain permission screen has a **Settings** row that opens the illustrated app menu.
2. The default shortcut is **hold Volume Down alone for five seconds**. It turns protection ON and OFF without opening an Activity over the workout. Release the button fully between holds.
3. Each service session starts with a **30-second test lock**. Unlock that test with the hardware shortcut to enable longer locks. Changing shortcuts also requires a new test.
4. Choose a maximum lock duration of 30, 60, 90, or 120 minutes. Choose longer than the class. The next hardware activation uses that maximum duration.
5. Confirm the ON popup and the small persistent lock banner before relying on protection.

In default mode, a short Volume Down press lowers **media volume by one step when released**. A five-second hold toggles protection without lowering volume. Volume Up works normally. Holding Volume Down for repeated volume adjustment is replaced by the shortcut while the accessibility service is enabled. The optional two-volume-button shortcut can conflict with TalkBack; use single-button mode if you need that Android shortcut.

**Accessibility service enabled is not the same as protection ON.** The service makes the shortcut available; only an active lock banner means touches are being blocked.

## Getting out of a lock

- Hold the selected hardware shortcut for five seconds.
- Hold three fingers on the display for eight seconds.
- Turn the display off with its physical power button. Touch Guard removes the lock on screen-off; this can affect playback, so use it as an emergency escape.
- Let the maximum duration expire.
- From a connected computer, stop only Touch Guard: `adb -s DEVICE shell am force-stop com.touchguard.peloton`. Replace `DEVICE` with the target listed by `adb devices`.

A [Mac emergency-unlock helper](scripts/Unlock-Touch-Guard.command) uses OpenPelo's bundled ADB and matches the tested tablet model. It requires a live authorized connection and may need adjustment if OpenPelo changes its install path or your tablet reports another model. The three-finger, power-button, and computer escape paths are implemented but have not yet been fully exercised on the bike; the 30-second timeout and hardware unlock have been tested.

Protection starts OFF after the process restarts. No automatic workout detection or automatic re-locking is included. Disable the accessibility service to restore normal long-press volume behavior. Uninstall Touch Guard normally to remove it. Disable wireless debugging after installation/testing if you no longer need it.

## Privacy and permissions

No internet permission, ads, analytics, accounts, or workout-data collection. The accessibility service requests key filtering and uses a transparent accessibility overlay. It does not request screen-content retrieval. Window-state event notifications are ignored. Only shortcut and duration preferences are saved. See [Privacy](PRIVACY.md).

The overlay catches taps; it does not change Peloton's app, root the tablet, disable device management, or change firmware. It cannot guarantee uninterrupted workouts or block every possible system-level interaction on an untested device.

## Build from source

Requires **JDK 17**, Android SDK platform 35 and build-tools 35.0.0. The wrapper uses **Gradle 8.11.1**, Android Gradle Plugin 8.9.1, and Kotlin 2.1.20. Accept the Android SDK licenses yourself when installing the SDK.

```sh
./gradlew :app:assembleDebug :app:testDebugUnitTest
```

The APK is in `app/build/outputs/apk/debug/`. A source build has its own development signing identity and cannot update the published APK without uninstalling the differently signed copy first. Do not uninstall during a workout.

Release signing is optional and reads `TOUCHGUARD_KEYSTORE`, `TOUCHGUARD_STORE_PASSWORD`, `TOUCHGUARD_KEY_ALIAS`, and `TOUCHGUARD_KEY_PASSWORD` from the environment. No signing key or password is included in this repository. With those variables set, run `./gradlew :app:assembleRelease`.

## Contribute and report results

Please include your tablet model, Android version, Peloton OS version, shortcut used, whether taps were blocked, and whether video/audio/metrics continued. Do not post pairing codes, IP addresses, credentials, or private ride/account screenshots. See the [test matrix](docs/TESTING.md).

Code and original project artwork are distributed under the [MIT license](LICENSE). Kotlin runtime notices are in [third-party notices](THIRD_PARTY_NOTICES.md). The menu illustration was AI-generated; its prompt is recorded in `artwork-prompt.txt`.
