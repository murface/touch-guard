# Install the beta with OpenPelo

Test only while the bike is idle. Keep a working hardware/timeout escape available before attempting a workout test. The currently tested configuration is the Original Bike tablet PLTN-RB1V0-2 on Android 11. Other devices are untested; Android below 11 is unsupported.

1. Download OpenPelo from its [official releases](https://github.com/doudar/Openpelo/releases) and follow its [device connection guide](https://github.com/doudar/Openpelo#connect-a-device).
2. Enable Developer Options and wireless debugging on the bike. Pair using the temporary IP address, pairing port/code, and separate connection port shown by Android. Never reuse someone else's code or hardcode temporary ports. USB debugging is an alternative.
3. Confirm the selected device is your bike. Download `TouchGuard-0.2.0-preview2.apk` and `SHA256SUMS.txt` from this project's GitHub release.
4. On a Mac, `shasum -a 256 TouchGuard-0.2.0-preview2.apk` should match the published checksum. Choose **Install local APK** in OpenPelo and select the file.
5. Launch **Touch Guard** through OpenPelo's Installed App Manager. Choose **Enable Touch Guard** in the app, then enable the single Touch Guard service in Android Accessibility and read/accept the Android permission dialog.
6. If you remain on Android's plain accessibility permission page, tap its **Settings** row to open the illustrated Touch Guard menu. The Android shortcut toggle on that page is separate from the app's own Volume Down shortcut and should stay OFF.
7. Hold **Volume Down alone for five seconds**, then release. The first lock lasts at most 30 seconds. Hold Volume Down again for five seconds and release to unlock. Watch for the ON and OFF popups. If it fails, wait for the automatic timeout and do not proceed to a longer lock.
8. Test the computer escape while idle. Three-finger escape is experimental and OFF by default; enable deliberately only if you want to test it. Verify the physical power button can turn off the display and release the overlay. Only then try a short workout test and check playback, audio, metrics, pause behavior, and recovery.
9. Choose Full Screen Lock or Lock with Social Access inside Touch Guard settings. Social mode is an experimental 30-second test; supported actions and incoming replies still need workout verification. Full Screen Lock supports longer durations: set a maximum duration longer than your class. With Peloton in front, use the hardware shortcut to turn protection on without opening the Touch Guard menu. After a service restart or shortcut change, complete another 30-second test first.
10. Disable wireless debugging when finished unless you intentionally need it again. Touch Guard's shortcut does not require a connected computer after installation and permission setup.

## Remove or disable

Unlock first. Turn off **Use Touch Guard** in Android Accessibility to disable the service. You can uninstall `com.touchguard.peloton` through OpenPelo or Android's app settings. Do not remove Peloton system apps, reset firmware, root the tablet, or disable device management.

## Troubleshooting

- **TalkBack appears:** use the default Volume Down-only shortcut. The optional two-button shortcut shares Android's accessibility gesture.
- **Volume Down does not continuously lower volume:** this is expected when its five-second shortcut is enabled. Short presses lower media volume one step on release. Disable the service or select the two-button mode to restore normal long-press behavior while unlocked.
- **Protection turned off after 30 seconds:** the hardware test has not been completed for this service session. Unlock a test with the selected shortcut first.
- **Signature mismatch:** do not uninstall blindly. Source/debug builds may use different signing identities. Public APK updates use the project's signing identity; keep an existing working installation until you have a recovery plan.
- **No response from the shortcut:** check the Touch Guard service is enabled, fully release all volume keys, and retest while idle. The installed app's accessibility switch is not itself the protection state.

## Updating to the one-entry preview

Version 0.2.0-preview2 uses one **Touch Guard** accessibility entry for both modes. The old preview1 Social Access entry is removed. Android may ask you to turn Touch Guard off and on and accept its updated permission while idle. The permission includes local control access used only for Social mode; Full Screen Lock does not inspect content. No screen information is stored or uploaded.

Social preview remains limited to 30 seconds even after a successful hardware-unlock test. Only the inspected workout app versionCode 2083457 is supported. If Social controls are unavailable, Full Screen Lock is used.
