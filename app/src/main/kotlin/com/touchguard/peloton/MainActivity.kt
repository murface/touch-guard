package com.touchguard.peloton

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.RippleDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*

class MainActivity : Activity() {
    private val handler=Handler(Looper.getMainLooper())
    private val ink=Color.rgb(233,244,247)
    private val muted=Color.rgb(153,176,190)
    private val mint=Color.rgb(56,223,195)
    private val pageColor=Color.rgb(8,21,34)
    private lateinit var status: TextView
    private lateinit var detail: TextView
    private lateinit var permission: Button
    private lateinit var protect: Button
    private lateinit var shortcut: Spinner
    private var lastSummary=""
    private val prefs get()=getSharedPreferences("guard",MODE_PRIVATE)
    private val refresh=object:Runnable {
        override fun run() {
            val s=GuardService.instance
            val summary="${s != null}/${s?.locked}/${s?.hardwareVerified}/${s?.lastMessage}/${s?.blockedTouches}"
            if(summary != lastSummary) {
                lastSummary=summary
                status.text=if(s?.locked == true) "Protection is ON" else "Protection is OFF"
                status.setTextColor(if(s?.locked == true) mint else ink)
                detail.text=when {
                    s == null -> "Enable the accessibility service to use touch protection and the hardware shortcut."
                    s.locked -> "Sweat away and get that PR! Your screen is protected."
                    !s.hardwareVerified -> "Service enabled. First, try a 30-second safety test and unlock with your chosen shortcut."
                    else -> "Ready for your next ride. Hold your shortcut for five seconds to turn protection on."
                }
                permission.text=if(s == null) "Enable Touch Guard" else "Accessibility service enabled  ·  Manage"
                protect.text=if(s?.hardwareVerified == true) "Turn protection ON" else "Try a 30-second safety test"
                protect.isEnabled=s != null
                protect.alpha=if(s == null) 0.45f else 1f
            }
            handler.postDelayed(this,500)
        }
    }
    private fun dp(value:Int)=(value*resources.displayMetrics.density).toInt()
    private fun rounded(color:Int,radius:Int=18)=GradientDrawable().apply { setColor(color); cornerRadius=dp(radius).toFloat() }
    private fun label(value:String,size:Float=16f,color:Int=ink,bold:Boolean=false)=TextView(this).apply {
        text=value; textSize=size; setTextColor(color); setLineSpacing(dp(3).toFloat(),1f)
        if(bold) typeface=Typeface.create("sans-serif-medium",Typeface.NORMAL)
    }
    private fun LinearLayout.gap(height:Int) { addView(View(this@MainActivity),LinearLayout.LayoutParams(1,dp(height))) }
    private fun LinearLayout.copy(value:String,size:Float=15f,color:Int=muted,bold:Boolean=false)=label(value,size,color,bold).also { addView(it) }
    private fun action(value:String,primary:Boolean=false,callback:()->Unit)=Button(this).apply {
        text=value; textSize=15f; isAllCaps=false
        typeface=Typeface.create("sans-serif-medium",Typeface.NORMAL)
        setTextColor(if(primary) pageColor else ink)
        background=RippleDrawable(ColorStateList.valueOf(0x2238DFC3),rounded(if(primary) mint else Color.rgb(28,50,65),12),null)
        minHeight=dp(52); setPadding(dp(18),dp(12),dp(18),dp(12))
        layoutParams=LinearLayout.LayoutParams(-1,-2)
        setOnClickListener { callback() }
    }
    private fun selector(items:Array<String>,selected:Int,onSelect:(Int)->Unit)=Spinner(this).apply {
        background=rounded(Color.rgb(28,50,65),12)
        setPadding(dp(8),dp(3),dp(8),dp(3))
        minimumHeight=dp(54)
        adapter=object:ArrayAdapter<String>(this@MainActivity,android.R.layout.simple_spinner_item,items) {
            override fun getView(position:Int,convertView:View?,parent:ViewGroup):View = label(items[position],16f).apply { setPadding(dp(12),dp(12),dp(12),dp(12)) }
            override fun getDropDownView(position:Int,convertView:View?,parent:ViewGroup):View = label(items[position],16f).apply {
                setPadding(dp(20),dp(18),dp(20),dp(18)); setBackgroundColor(Color.rgb(28,50,65))
            }
        }
        setSelection(selected)
        onItemSelectedListener=object:AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent:AdapterView<*>?,view:View?,position:Int,id:Long) { onSelect(position) }
            override fun onNothingSelected(parent:AdapterView<*>?) {}
        }
    }
    override fun onCreate(state:Bundle?) {
        super.onCreate(state)
        window.statusBarColor=pageColor; window.navigationBarColor=pageColor
        window.decorView.systemUiVisibility=0
        val scroll=ScrollView(this).apply { setBackgroundColor(pageColor); isFillViewport=true; clipToPadding=false }
        val frame=LinearLayout(this).apply { orientation=LinearLayout.VERTICAL; gravity=Gravity.CENTER_HORIZONTAL; setPadding(dp(28),dp(28),dp(28),dp(28)) }
        val column=LinearLayout(this).apply { orientation=LinearLayout.VERTICAL }
        val width=(resources.displayMetrics.widthPixels-dp(56)).coerceAtMost(dp(880))
        frame.addView(column,LinearLayout.LayoutParams(width,-2)); scroll.addView(frame)
        val header=LinearLayout(this).apply { gravity=Gravity.CENTER_VERTICAL }
        header.addView(ImageView(this).apply { setImageResource(applicationInfo.icon); contentDescription="Touch Guard pentagon badge" },LinearLayout.LayoutParams(dp(72),dp(72)))
        header.addView(LinearLayout(this).apply {
            orientation=LinearLayout.VERTICAL; setPadding(dp(18),0,0,0)
            copy("TOUCH GUARD",12f,mint,true)
            copy("Keep the ride rolling.",30f,ink,true)
            copy("A little protection. A lot fewer accidental pauses.",15f)
        },LinearLayout.LayoutParams(0,-2,1f))
        column.addView(header); column.gap(8)
        val hero=LinearLayout(this).apply { gravity=Gravity.CENTER_VERTICAL }
        hero.addView(LinearLayout(this).apply {
            orientation=LinearLayout.VERTICAL; setPadding(0,dp(14),dp(20),dp(14))
            copy("Sweat happens.\nPauses don’t have to.",32f,ink,true)
            gap(12)
            copy("Your workout. Your rhythm.\nYour next personal best.",16f)
            gap(16)
            copy("SWEAT AWAY & GET THAT PR!",12f,mint,true)
        },LinearLayout.LayoutParams(0,-2,1f))
        hero.addView(ImageView(this).apply {
            setImageResource(resources.getIdentifier("ride_hero","drawable",packageName))
            scaleType=ImageView.ScaleType.FIT_CENTER
            contentDescription="Cyclist riding with a mint pentagon shield protecting the touchscreen from sweat"
        },LinearLayout.LayoutParams(dp(340),dp(226)))
        column.addView(hero); column.gap(20)
        fun card(title:String,build:LinearLayout.()->Unit) {
            val panel=LinearLayout(this).apply {
                orientation=LinearLayout.VERTICAL; background=rounded(Color.rgb(17,35,49)); setPadding(dp(24),dp(22),dp(24),dp(22))
                copy(title,13f,mint,true); gap(12); build()
            }
            column.addView(panel,LinearLayout.LayoutParams(-1,-2)); column.gap(16)
        }
        card("YOUR PROTECTION") {
            status=copy("Protection is OFF",27f,ink,true)
            gap(6); detail=copy("")
            gap(20)
            protect=action("Try a 30-second safety test",true) {
                val s=GuardService.instance
                if(s == null) return@action
                arm(!s.hardwareVerified,prefs.getInt("ride_minutes",60))
            }; addView(protect)
            gap(12)
            addView(action("Unlock now") { GuardService.instance?.unlock("App unlock") })
        }
        card("YOUR SHORTCUT") {
            copy("Hold for five seconds. Release. Ride on.",18f,ink,true); gap(8)
            shortcut=selector(arrayOf("Volume Down  ·  hold 5 seconds","Both volume buttons  ·  hold 5 seconds"),if(prefs.getBoolean("both_keys",false)) 1 else 0) { position ->
                val both=position == 1
                val s=GuardService.instance
                if(s != null) s.configureShortcut(both) else if(prefs.getBoolean("both_keys",false) != both) prefs.edit().putBoolean("both_keys",both).apply()
            }
            addView(shortcut); gap(10)
            copy("Use the same shortcut to turn protection ON and OFF, even with Peloton open. Release the button or buttons fully between holds.")
            gap(8)
            copy("With Volume Down selected, a short press lowers media volume one step when released. A five-second hold toggles protection without changing volume. Both-button mode may conflict with Android’s TalkBack shortcut.",13f)
            gap(20); copy("Maximum lock duration",16f,ink,true); gap(8)
            val choices=intArrayOf(30,60,90,120)
            addView(selector(arrayOf("30 minutes","60 minutes","90 minutes","120 minutes"),choices.indexOf(prefs.getInt("ride_minutes",60)).coerceAtLeast(0)) {
                prefs.edit().putInt("ride_minutes",choices[it]).apply()
            })
            gap(8); copy("Choose longer than your class. Protection switches off automatically when this time runs out.",13f)
        }
        card("SETUP & SAFETY") {
            copy("Service enabled ≠ protection on",18f,ink,true); gap(6)
            copy("The accessibility service makes the shortcut available. Touches are blocked only when the ON banner is visible.")
            gap(16)
            permission=action("Enable Touch Guard") { startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)) }; addView(permission)
            gap(12)
            addView(action("Open the safe touch-test screen") { startActivity(Intent(this@MainActivity,ProbeActivity::class.java)) })
            gap(12)
            copy("Each time the service starts, or you change shortcuts, the first lock is a 30-second test. Unlock with your shortcut to enable longer locks.",13f)
            gap(12)
            copy("Need an escape?",16f,ink,true); gap(6)
            copy("Hold three fingers on the screen for eight seconds, or turn the display off with the physical power button. A Mac emergency-unlock tool is also available. Protection always starts OFF after a restart.",13f)
        }
        card("WHY WE MADE THIS") {
            copy("Sweat belongs in your workout. Not on the pause button.",21f,ink,true); gap(10)
            copy("A sweaty screen can register touches you never meant to make and pause a class mid-effort. Touch Guard was built to catch those accidental taps while keeping the workout visible.")
            gap(12)
            copy("A clear touch-blocking layer. A physical-button shortcut. No rooting or changes to the Peloton workout app.")
            gap(12)
            copy("No internet permission. No screen-content reading. No workout-data collection.",13f,mint)
            gap(12)
            copy("Independent community project. Not affiliated with Peloton. Tested touch blocking and volume-button unlocking on an Original Bike with Android 11; workout playback testing is still in progress.",12f)
        }
        column.copy("TOUCH GUARD  ·  BETA 0.1.2",12f,muted,true).gravity=Gravity.CENTER
        setContentView(scroll)
    }
    private fun arm(test:Boolean,minutes:Int) {
        val s=GuardService.instance ?: return
        moveTaskToBack(true)
        handler.postDelayed({ s.begin(test,minutes) },800)
    }
    override fun onResume() { super.onResume(); lastSummary=""; handler.post(refresh) }
    override fun onPause() { handler.removeCallbacks(refresh); super.onPause() }
}
