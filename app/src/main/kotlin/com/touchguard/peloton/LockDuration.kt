package com.touchguard.peloton

/** A short test is opt-in only; normal locks always use the selected ride duration. */
object LockDuration {
    fun millis(minutes:Int=120,explicitTest:Boolean=false):Long =
        if(explicitTest) 30_000L else minutes.coerceIn(1,360)*60_000L
}
