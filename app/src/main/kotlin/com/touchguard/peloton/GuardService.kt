package com.touchguard.peloton

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.app.*
import android.content.*
import android.graphics.*
import android.os.*
import android.util.Log
import android.view.*
import android.view.accessibility.AccessibilityEvent
import android.widget.Toast
import android.media.AudioManager
import kotlin.math.ceil

class GuardService : AccessibilityService() {
    companion object {
        var instance: GuardService? = null; private set
        const val TAG = "TouchGuard"
    }
    private val handler = Handler(Looper.getMainLooper())
    private val hold = HoldTracker()
    // Keep each key's DOWN/repeat/UP routing consistent across a toggle.
    private val consumed = mutableMapOf<Int,Boolean>()
    private var overlay: GuardView? = null
    private var wm: WindowManager? = null
    private var until = 0L
    private var test = false
    private var registered = false
    private var verified = false
    private var statusToast: Toast? = null
    private var singleDownAt = -1L
    private var singleToggled = false
    val useBothKeys get() = getSharedPreferences("guard",MODE_PRIVATE).getBoolean("both_keys",false)
    private val shortcutLabel get() = if(useBothKeys) "both volume keys" else "Volume Down"
    var lastMessage = "Ready"; private set
    var blockedTouches = 0; private set
    val locked get() = overlay != null
    val hardwareVerified get() = verified
    private val screenOff = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) { unlock("Screen turned off") }
    }
    private val tick = object : Runnable {
        override fun run() {
            if (!locked) return
            val now = SystemClock.uptimeMillis()
            if (now >= until) { unlock("Automatic safety timeout"); return }
            overlay?.invalidate()
            handler.postDelayed(this, 250)
        }
    }
    private val holdFinished = Runnable {
        if (hold.takeComplete(SystemClock.uptimeMillis())) {
            singleToggled = true
            if (locked) {
                if (test) verified = true
                unlock("$shortcutLabel held for five seconds")
            } else {
                val minutes = getSharedPreferences("guard",MODE_PRIVATE).getInt("ride_minutes",60)
                // No Activity is launched: the workout keeps foreground focus.
                begin(!verified,minutes)
            }
        }
    }
    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
        serviceInfo = serviceInfo.apply { flags = flags or AccessibilityServiceInfo.FLAG_REQUEST_FILTER_KEY_EVENTS }
        wm = getSystemService(WindowManager::class.java)
        hold.setBothRequired(useBothKeys)
        registerReceiver(screenOff, IntentFilter(Intent.ACTION_SCREEN_OFF))
        registered = true
        getSystemService(NotificationManager::class.java).createNotificationChannel(
            NotificationChannel("guard", "Touch Guard status", NotificationManager.IMPORTANCE_LOW))
        Log.i(TAG, "Accessibility connected; unlocked")
    }
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {} // Never inspect content.
    override fun onInterrupt() { unlock("Accessibility interrupted") }
    override fun onUnbind(intent: Intent?): Boolean { unlock("Accessibility disabled"); return super.onUnbind(intent) }
    override fun onDestroy() {
        unlock("Service stopped")
        if (registered) unregisterReceiver(screenOff)
        handler.removeCallbacksAndMessages(null)
        instance = null
        super.onDestroy()
    }
    fun begin(testMode: Boolean, minutes: Int = 60): Boolean {
        if (locked) return false
        if (!testMode && !verified) { lastMessage = "Complete a volume-button unlock test first"; return false }
        if (!(getSystemService(PowerManager::class.java).isInteractive)) return false
        test = testMode
        blockedTouches = 0
        hold.requireRelease()
        until = SystemClock.uptimeMillis() + if (testMode) 30000L else minutes.coerceIn(1,120) * 60000L
        val view = GuardView()
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
            PixelFormat.TRANSLUCENT)
        params.gravity = Gravity.TOP or Gravity.LEFT
        params.setFitInsetsTypes(0)
        params.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS
        params.setTitle("Touch Guard blocker")
        try {
            wm!!.addView(view, params)
            overlay = view
            showNotification()
            handler.post(tick)
            lastMessage = if (test) "30-second test active" else "Locked"
            announce("Touch Guard is ON\nSweat away and get that PR!")
            Log.i(TAG, "LOCK mode=${if(test) "test" else "ride"}")
            return true
        } catch (e: Exception) {
            overlay = view
            unlock("Unable to add overlay: ${e.javaClass.simpleName}")
            return false
        }
    }
    fun unlock(reason: String) {
        handler.removeCallbacks(tick)
        handler.removeCallbacks(holdFinished)
        val old = overlay
        overlay = null
        if (old != null) {
            try { wm?.removeViewImmediate(old) } catch (e: Exception) { Log.w(TAG, "Overlay removal", e) }
            Log.i(TAG, "UNLOCK reason=$reason blockedTouches=$blockedTouches")
            announce("Touch Guard is OFF\nYou're back in control.")
        }
        hold.requireRelease()
        getSystemService(NotificationManager::class.java).cancel(1)
        lastMessage = reason
    }
    private fun announce(message: String) {
        statusToast?.cancel()
        statusToast = Toast.makeText(this,message,Toast.LENGTH_LONG).also { it.show() }
    }
    fun configureShortcut(both: Boolean) {
        if (locked || useBothKeys == both) return
        getSharedPreferences("guard",MODE_PRIVATE).edit().putBoolean("both_keys",both).apply()
        handler.removeCallbacks(holdFinished)
        hold.setBothRequired(both)
        singleDownAt = -1L
        verified = false
        lastMessage = "Shortcut changed; complete a 30-second safety test"
    }
    override fun onKeyEvent(event: KeyEvent): Boolean {
        val key = event.keyCode
        if (key != KeyEvent.KEYCODE_VOLUME_UP && key != KeyEvent.KEYCODE_VOLUME_DOWN) return false
        val down = event.action == KeyEvent.ACTION_DOWN
        if (event.action != KeyEvent.ACTION_DOWN && event.action != KeyEvent.ACTION_UP) return false
        val single = !useBothKeys && key == KeyEvent.KEYCODE_VOLUME_DOWN
        if (down && event.repeatCount == 0) {
            consumed[key] = single || (locked && useBothKeys)
            if (single) { singleDownAt = SystemClock.uptimeMillis(); singleToggled = false }
        }
        val consume = consumed[key] ?: false
        if (!down) consumed.remove(key)
        if (event.isCanceled || !getSystemService(PowerManager::class.java).isInteractive) {
            hold.reset()
            singleDownAt = -1L
            handler.removeCallbacks(holdFinished)
            return consume
        }
        if (single && !down) {
            if (consume && singleDownAt >= 0 && !singleToggled && SystemClock.uptimeMillis()-singleDownAt < 5000L) {
                getSystemService(AudioManager::class.java).adjustStreamVolume(AudioManager.STREAM_MUSIC,AudioManager.ADJUST_LOWER,AudioManager.FLAG_SHOW_UI)
            }
            singleDownAt = -1L
        }
        if (!useBothKeys && down && ((key == KeyEvent.KEYCODE_VOLUME_UP && hold.down) || (single && hold.up))) singleToggled = true
        hold.event(key == KeyEvent.KEYCODE_VOLUME_UP, down, event.repeatCount > 0, SystemClock.uptimeMillis())
        handler.removeCallbacks(holdFinished)
        if (hold.started >= 0) handler.postDelayed(holdFinished, hold.remaining(SystemClock.uptimeMillis()))
        if (event.repeatCount == 0) Log.i(TAG, "VOLUME key=$key action=${event.action} both=${hold.up && hold.down}")
        return consume
    }
    private fun showNotification() {
        val open = PendingIntent.getActivity(this, 1, Intent(this, MainActivity::class.java), PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        val stop = PendingIntent.getBroadcast(this, 2, Intent(this, UnlockReceiver::class.java), PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        val icon = resources.getIdentifier("ic_guard_status","drawable",packageName)
        val n = Notification.Builder(this, "guard").setSmallIcon(icon)
            .setContentTitle("Touch Guard is ON — Sweat away and get that PR!")
            .setContentText("Touches blocked. Hold $shortcutLabel 5 seconds to turn OFF.")
            .setOngoing(true).setContentIntent(open)
            .addAction(Notification.Action.Builder(null, "Unlock", stop).build()).build()
        getSystemService(NotificationManager::class.java).notify(1,n)
    }
    private inner class GuardView : View(this@GuardService) {
        private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        private var escapeAt = -1L
        private val escape = Runnable {
            if (escapeAt >= 0L && SystemClock.uptimeMillis() - escapeAt >= 8000L) unlock("Three-finger emergency hold")
        }
        init { setBackgroundColor(Color.TRANSPARENT); importantForAccessibility = IMPORTANT_FOR_ACCESSIBILITY_NO }
        override fun onDraw(canvas: Canvas) {
            val density = resources.displayMetrics.density
            paint.color = 0xB0000000.toInt()
            canvas.drawRoundRect(12f*density,12f*density,420f*density,72f*density,8f*density,8f*density,paint)
            paint.color = Color.WHITE; paint.textSize = 15f*density
            val seconds = ceil((until - SystemClock.uptimeMillis()).coerceAtLeast(0L)/1000.0).toInt()
            canvas.drawText("TOUCH GUARD · ${if(test) "TEST" else "LOCKED"} · ${seconds/60}:${(seconds%60).toString().padStart(2,'0')}",24f*density,36f*density,paint)
            paint.textSize = 12f*density
            canvas.drawText("Unlock: $shortcutLabel 5s · or three fingers 8s",24f*density,57f*density,paint)
        }
        override fun onTouchEvent(event: MotionEvent): Boolean {
            if (event.actionMasked == MotionEvent.ACTION_DOWN) blockedTouches++
            val count = when(event.actionMasked) {
                MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> 0
                MotionEvent.ACTION_POINTER_UP -> event.pointerCount - 1
                else -> event.pointerCount
            }
            if (count >= 3 && escapeAt < 0L) {
                escapeAt = SystemClock.uptimeMillis(); handler.postDelayed(escape,8000)
            } else if (count < 3) {
                escapeAt = -1L; handler.removeCallbacks(escape)
            }
            return true
        }
        override fun onGenericMotionEvent(event: MotionEvent) = true
        override fun onDetachedFromWindow() { handler.removeCallbacks(escape); super.onDetachedFromWindow() }
    }
}

class UnlockReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) { GuardService.instance?.unlock("Notification unlock") }
}
