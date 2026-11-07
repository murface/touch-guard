package com.touchguard.peloton

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.widget.*

/** Isolated test target: prove touch suppression without touching Peloton controls. */
class ProbeActivity : Activity() {
    private var touches=0
    private lateinit var counter: TextView
    override fun onCreate(state: Bundle?) {
        super.onCreate(state)
        val layout=LinearLayout(this).apply { orientation=LinearLayout.VERTICAL; setPadding(40,40,40,40) }
        counter=TextView(this).apply { textSize=25f; text="Underlying touches: 0" }
        layout.addView(counter)
        layout.addView(TextView(this).apply { textSize=20f; text="Tap this screen before and during a test. This counter must stop while locked and resume after unlock. The test expires in 30 seconds." })
        layout.addView(Button(this).apply {
            text="Start 30-second overlay test here"
            setOnClickListener { GuardService.instance?.begin(true) }
        })
        setContentView(layout)
    }
    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if(event.actionMasked == MotionEvent.ACTION_DOWN) {
            touches++; counter.text="Underlying touches: $touches"; Log.i("TouchGuardProbe","Underlying touch $touches")
        }
        return super.dispatchTouchEvent(event)
    }
}
