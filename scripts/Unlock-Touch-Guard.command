#!/bin/bash
# Stops only Touch Guard on connected tablets matching the inspected Peloton model.
# Uses OpenPelo's own ADB. No IP address or temporary port is stored.
set -euo pipefail
ADB="$HOME/Library/Application Support/com.doudar.openpelo/platform-tools/adb"
if [ ! -x "$ADB" ]; then
  echo "OpenPelo's connection tool was not found. Open OpenPelo once, then retry."
  exit 1
fi
found=0
while IFS= read -r transport; do
  [ -z "$transport" ] && continue
  "$ADB" -t "$transport" shell am force-stop com.touchguard.peloton
  found=1
done < <("$ADB" devices -l | awk '/ device / && /model:PLTN_RB1VO_2/ { for(i=1;i<=NF;i++) if($i ~ /^transport_id:/) { sub(/^transport_id:/,"",$i); print $i } }')
if [ "$found" -eq 0 ]; then
  echo "No connected Peloton found. Reconnect it in OpenPelo and retry."
  exit 1
fi
echo "Touch Guard stopped. If touch is still unresponsive, dry the screen and your hands completely and briefly sleep/wake the display. Reopen Touch Guard to use it again."
