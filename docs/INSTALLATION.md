# Install or update Touch Guard with OpenPelo

Current signed beta: **v0.2.0-preview14**. API 30 / Android 11 or newer is required to install; actual behavior has only been checked on one Original Bike with Android 11. Social controls are matched to Peloton workout app 2.8.3457 (versionCode 2083457). Bike+, Tread, Row, older tablets and other workout app versions are unverified.

## 1. Connect and install

1. Keep the bike idle, outside a class, with protection OFF.
2. Get OpenPelo from its [official releases](https://github.com/doudar/Openpelo/releases), then follow its [connection guide](https://github.com/doudar/Openpelo#connect-a-device). Confirm it shows Connected.
3. Download **TouchGuard-0.2.0-preview14.apk** and **SHA256SUMS.txt** from the [Touch Guard release](https://github.com/murface/touch-guard/releases/tag/v0.2.0-preview14). The optional source ZIP is for developers; it is not the installer.
4. Optional integrity check: on macOS run `shasum -a 256 TouchGuard-0.2.0-preview14.apk`; on Windows PowerShell use `Get-FileHash .\TouchGuard-0.2.0-preview14.apk -Algorithm SHA256`. Compare the result with SHA256SUMS.txt.
5. In OpenPelo, choose **Install local APK** and select the APK. Launch **Touch Guard** from its Installed App Manager.

The OpenPelo catalog suggestion is pending maintainer inclusion. If Touch Guard is absent there, use the local APK method above.

## 2. Enable one service

In Touch Guard, select **Enable Touch Guard**, then open **Touch Guard** in Android Accessibility.

| Android setting | Choose |
| --- | --- |
| Top **Use Touch Guard** switch | **ON**; read and accept Android's permission prompt. |
| Separate **Touch Guard shortcut** option | **OFF**. Touch Guard handles its own volume shortcut. |

There is **one app and one service**, with both modes inside Touch Guard's settings. Do not enable a second Social Access service. If an obsolete “Touch Guard Social Access” entry from an early test remains, leave it disabled; the current release no longer uses it. Avoid assigning Android/TalkBack's own shortcut to the same volume gesture.

The permission allows key filtering, drawing the blocking overlay and local control identification. Full Lock does not inspect content; Social Mode identifies known controls and high-five wording locally. It does not save or upload screen content. [Privacy details](../PRIVACY.md).

## 3. Choose mode and duration

- **Full Lock:** blocks every touchscreen touch while protection is ON.
- **Social Mode:** allows supported avatar high-fives, incoming high-five/milestone hand buttons, leaderboard page scrolling, music-card opening and song hearts. Pause, metrics, time-bar and unrelated controls stay blocked. An unsupported workout is fully blocked until recognized; there are no permanently open touch rectangles.

Set **Maximum lock duration** longer than the ride. Default: **2 hours**. Options: 30 minutes, 1 hour, 90 minutes, 2, 3, 4 or 6 hours. This duration applies immediately to normal activation after installation, restart or shortcut changes. **No mandatory ON→OFF verification sequence is required.**

**Test Touch Guard** is optional. It opens an isolated screen whose test lock expires after **30 seconds**. This does not limit a normal workout lock.

## 4. Use during a ride

1. Select and start the class with protection OFF.
2. Hold **Volume Down alone continuously for five seconds**, then release completely. The ON popup names your mode and says “Sweat away and go get the PR 💪 🙌”. There is no persistent countdown banner.
3. To unlock, hold Volume Down again for five seconds, then release. Look for **Touch Guard is OFF**.
4. After unlocking, the screen may need to be completely dry, with dry hands, before touch works normally.

A brief Volume Down press changes volume on release; holding it for the shortcut replaces normal held-key volume adjustment. Both-volume-buttons mode is optional in settings and can conflict with Android/TalkBack shortcuts. Use the default single-button setting unless deliberately changing it.

In Social Mode, tap a rider's circular avatar for a high-five. Use the visible hand button on a left-feed notification to reply; tap the music note to open the song card, then its heart. Scrolling moves the leaderboard one page per swipe. “High five sent!” is local feedback after the control accepts the action; Peloton's normal animation may differ and delivery is not independently confirmed.

## Updating

Unlock and leave the bike idle, then install the new signed release over the old one using **Install local APK**. Keep the existing installation; settings are preserved on a compatible signed update. Reopen Touch Guard and confirm its single accessibility service is enabled. The lock starts OFF after a service restart.

If Android reports an incompatible signing certificate, you may have installed a development/CI build. Do not uninstall blindly: uninstalling clears Touch Guard's settings. Confirm which build you installed and use the matching signed update, or deliberately remove the old Touch Guard installation and set up the published release again. Never remove Peloton system apps.

## Recovery and troubleshooting

- **Touch still fails after OFF:** dry the display and hands completely. Briefly press the physical power button to sleep the screen, then briefly press it again to wake it. Do not hold it down. Screen-off also requests Touch Guard unlock. The owner recovered touch this way after a sweaty ride; the cause was not conclusively isolated.
- **Cannot unlock:** try the configured five-second volume shortcut and release fully. Briefly sleep/wake the display. The ongoing notification also has an unlock action if accessible. The configured maximum duration remains another timeout path.
- **Connected-computer recovery:** OpenPelo can stop package `com.touchguard.peloton`. The optional Mac `Unlock-Touch-Guard.command` helper uses OpenPelo's bundled ADB to stop only Touch Guard on connected tablets matching the tested model. Review the script before running it; it does not dry the screen or fix hardware touch problems. Other computers can use their ADB connection to run `adb shell am force-stop com.touchguard.peloton`, selecting the intended bike if multiple devices are connected.
- **A normal lock ends after 30 seconds:** confirm you installed preview14 and used the hardware shortcut, rather than starting the optional Test Touch Guard screen. Old preview releases contained a 30-second gate; it has been removed. Check **Last lock event** in settings for the actual shutoff reason.
- **Social controls are blocked:** verify Social Mode is selected and a supported workout is visible. Unlock, then check **Last Social check** in settings. Report the app/Peloton versions and the app-defined diagnostic; avoid posting screenshots containing other riders' information.
- **Extra Android shortcuts or spoken prompts:** leave the lower Android Touch Guard shortcut OFF and check conflicting TalkBack/volume-button shortcuts.

The optional three-finger eight-second escape is experimental and **OFF by default**. Sweat can resemble multiple fingers; do not treat this as the primary unlock method.

## Disable or uninstall

Unlock first. Turn the top **Use Touch Guard** switch OFF in Android Accessibility, then uninstall `com.touchguard.peloton` through OpenPelo or Android app settings if desired. This clears Touch Guard settings. No rooting, firmware reset, device-management changes or Peloton app removal is needed.
