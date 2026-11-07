# Privacy

Touch Guard runs locally on the Android device. It has no internet permission and does not transmit data. It contains no ads, analytics, user accounts, remote services, camera access, microphone access, location access, or storage-scanning features.

The accessibility service receives volume-key events so it can detect the selected five-second shortcut. Events for other keys are not used. In single-button mode it replaces a short Volume Down press with one media-volume adjustment on release. The service requests window-state event notifications but ignores their content. It does not request permission to retrieve screen content, inspect the accessibility view hierarchy, record the screen, or collect workout metrics.

The app saves the selected hardware shortcut and maximum lock duration in local preferences. The current lock state, touch count, and hardware-test result are held in memory. Android may retain diagnostic log messages identifying lock/unlock reasons, volume-key event codes, and a count of blocked touches. These logs do not contain screen text, workout metrics, account names, or pairing codes, and the app does not send them anywhere.

Removing the app removes its local preferences. Android controls system-log retention. GitHub, OpenPelo, Android, and Peloton have their own privacy practices; this statement describes Touch Guard only. If you submit a bug report, review its contents yourself before posting it publicly.
