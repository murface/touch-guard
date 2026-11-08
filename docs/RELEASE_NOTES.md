# Touch Guard v0.2.0-preview2 — one app, two lock modes

Choose **Full Screen Lock** or **Lock with Social Access** inside Touch Guard settings. One Android accessibility entry handles both; the same five-second Volume Down shortcut turns protection ON/OFF.

- Full Screen Lock blocks every screen touch.
- Experimental Social Access recognizes leaderboard avatar high-fives, leaderboard page scrolling, music-card opening, and song hearts while other touches remain intercepted. Incoming replies remain unverified.
- Social preview expires after 30 seconds, even after hardware verification. Current control support is pinned to Peloton workout app versionCode 2083457.
- Three-finger escape is experimental and OFF by default.
- One permission includes local control IDs, bounds, and high-five wording access for Social gestures. Full Screen Lock does not inspect content. No screen storage/uploads or internet permission.

**Testing:** same-key installation and single-entry menu verified on one Original Bike / Android 11; build and shortcut/social-policy tests pass. Social interactions, continuous workout playback/audio/metrics, complete emergency recovery, and an hour of sweat remain unverified. This is a testing preview, not a fully verified workout fix.

Install the APK with OpenPelo while idle. Enable the single Touch Guard accessibility entry, leave Android's separate accessibility shortcut OFF, choose your mode in the app, and complete the 30-second hardware test. Updating may require re-enabling the combined permission. The previous two-entry private preview is replaced.

[Installation](https://github.com/murface/touch-guard/blob/v0.2.0-preview2/docs/INSTALLATION.md) · [Test matrix](https://github.com/murface/touch-guard/blob/v0.2.0-preview2/docs/TESTING.md) · [Privacy](https://github.com/murface/touch-guard/blob/v0.2.0-preview2/PRIVACY.md)

Check SHA256SUMS.txt before installing. The APK retains the project's signing identity. Source and emergency-unlock helper are included. Older v0.1.2 remains available unchanged. Independent project, not affiliated with Peloton or OpenPelo.
