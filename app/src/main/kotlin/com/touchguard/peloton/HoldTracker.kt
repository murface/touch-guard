package com.touchguard.peloton

/** Pure state machine: repeats never restart the five-second continuous hold. */
class HoldTracker(private val duration: Long = 5000L) {
    var up = false; private set
    var down = false; private set
    var started = -1L; private set
    private var fired = false
    var bothRequired = true; private set
    fun setBothRequired(value: Boolean) {
        if (bothRequired != value) { bothRequired = value; requireRelease() }
    }
    fun event(isUpKey: Boolean, pressed: Boolean, repeat: Boolean, now: Long) {
        if (pressed && repeat) return
        if (isUpKey) up = pressed else down = pressed
        if (!up && !down) fired = false
        if (!bothRequired && up && down) fired = true
        val active = if (bothRequired) up && down else down && !up
        if (active && !fired) { if (started < 0L) started = now } else started = -1L
    }
    fun complete(now: Long) = started >= 0L && (if(bothRequired) up && down else down && !up) && now - started >= duration
    fun remaining(now: Long) = if (started < 0L) duration else (duration - (now - started)).coerceAtLeast(0L)
    fun takeComplete(now: Long): Boolean {
        if (!complete(now)) return false
        fired = true
        started = -1L
        return true
    }
    // A state change cannot fire again until BOTH physical buttons have been released.
    fun requireRelease() { fired = up || down; started = -1L }
    fun reset() { up = false; down = false; started = -1L; fired = false }
}
