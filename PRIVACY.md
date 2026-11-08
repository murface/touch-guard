# Touch Guard privacy — 0.2.0-preview2

Touch Guard is one app with one Android accessibility service and two modes selected inside its settings.

**Full Screen Lock** blocks all screen touches without inspecting screen content.

**Lock with Social Access** reads Peloton control IDs, positions, and high-five notification wording locally when handling a protected gesture. It uses these to identify supported avatar high-fives, incoming replies, leaderboard scrolling, and song controls. Incoming replies remain unverified on the bike. Unsupported screens or controls remain blocked. Android grants screen-content access to the single service for both modes; the app only calls its control-identification code when Social mode is selected or active.

No internet permission, uploads, analytics, ads, accounts, or stored screen content. Only app settings are saved. Transient target identities and a hash of notification wording are held in memory during a gesture to recheck that its target has not changed.

The service handles the volume-button shortcut and draws the transparent blocking overlay. Android's separate accessibility shortcut is not needed. Neither mode edits Peloton's app or firmware.
