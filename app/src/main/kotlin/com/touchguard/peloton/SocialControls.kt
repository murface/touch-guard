package com.touchguard.peloton

import android.accessibilityservice.AccessibilityService
import android.graphics.Rect
import android.util.Log
import android.view.ViewConfiguration
import android.view.accessibility.AccessibilityNodeInfo
import android.view.accessibility.AccessibilityWindowInfo
import kotlin.math.abs
import java.io.PrintWriter
import java.util.ArrayDeque

/** Local control identification, called only by the one Touch Guard service in Social mode. */
class SocialControls(private val service:AccessibilityService) {
    data class Target(val id:String,val rect:Rect,val window:Int,val source:Int,val messageHash:Int)
    data class Gesture(val x:Int,val y:Int,val at:Long,val click:Target?,val scroll:Target?,val matchSummary:String)
    private val history=ArrayDeque<String>()
    private var lastTouch="None"
    var lastResult="Not tested"; private set
    private fun result(reason:String) { history.addLast(reason);while(history.size>20)history.removeFirst();lastResult=reason;service.getSharedPreferences("guard",0).edit().putString("social_diagnostic",reason).apply();Log.i(GuardService.TAG,"Social: $reason") }
    private fun workoutRoot():AccessibilityNodeInfo? {
        val version=try { service.packageManager.getPackageInfo(SocialPolicy.PACKAGE,0).longVersionCode } catch(_:Exception){-1L}
        if(version!=SocialPolicy.VERSION){result("Unsupported workout app version");return null}
        // Touching our overlay can change Android's active accessibility window.
        // Choose the focused application window, never an overlay, toast, or a hidden app.
        val windows=service.windows
        var root:AccessibilityNodeInfo?=null
        try {
            for(window in windows) {
                if(window.type!=AccessibilityWindowInfo.TYPE_APPLICATION || !window.isFocused)continue
                val candidate=window.root ?: continue
                if(candidate.packageName?.toString()==SocialPolicy.PACKAGE && candidate.refresh()) { root=candidate;break }
                candidate.recycle()
            }
        } finally { windows.forEach { it.recycle() } }
        val workout=root ?: run {result("Focused workout window unavailable");return null}
        // The timeline and metrics can be folded away during a valid workout.
        // Require stable workout containers; each permitted action still needs its own IDs/ancestors.
        for(id in listOf("main_inclass_fragment_container","inclass_drawer")) {
            val nodes=workout.findAccessibilityNodeInfosByViewId(SocialPolicy.PREFIX+id)
            val found=nodes.any { it.isVisibleToUser };nodes.forEach { it.recycle() }
            if(!found){workout.recycle();result("Workout layout not recognized: $id");return null}
        }
        return workout
    }
    fun supportedWorkout():Boolean { val r=workoutRoot() ?: return false;r.recycle();return true }
    private fun feedLabel(node:AccessibilityNodeInfo):String {
        var parent=node.parent;var count=0
        while(parent!=null && count++<40) {
            if(parent.viewIdResourceName==SocialPolicy.PREFIX+"feed_item_root") {
                parent.refresh()
                val labels=parent.findAccessibilityNodeInfosByViewId(SocialPolicy.PREFIX+"feed_item_label")
                labels.forEach { it.refresh() }
                val label=labels.firstOrNull()?.text?.toString() ?: ""
                labels.forEach { it.recycle() };parent.recycle();return label
            }
            val next=parent.parent;parent.recycle();parent=next
        }
        parent?.recycle();return ""
    }
    private fun replyHitRect(node:AccessibilityNodeInfo):Rect? {
        var parent=node.parent;var count=0;var row:Rect?=null
        while(parent!=null && count++<40) {
            val id=parent.viewIdResourceName
            if(id==SocialPolicy.PREFIX+"feed_item_root") {
                parent.refresh();row=Rect().also { parent.getBoundsInScreen(it) }
            }
            if(id==SocialPolicy.PREFIX+"inclass_feed_recyclerview") {
                parent.refresh();val clip=Rect();parent.getBoundsInScreen(clip);parent.recycle()
                return row?.takeIf { it.intersect(clip) && !it.isEmpty }
            }
            val next=parent.parent;parent.recycle();parent=next
        }
        parent?.recycle();return null
    }
    private fun descriptor(n:AccessibilityNodeInfo,scroll:Boolean,parents:Set<String>):Target? {
        if(!n.refresh())return null
        if(!n.isVisibleToUser || !n.isEnabled || (if(scroll) !n.isScrollable else !n.isClickable))return null
        val id=n.viewIdResourceName ?: return null
        val label=if(id==SocialPolicy.FEED_REPLY)feedLabel(n) else ""
        if(if(scroll) !SocialPolicy.mayScroll(id,parents) else !SocialPolicy.mayClick(id,parents,n.contentDescription?.toString() ?: ""))return null
        val nativeRect=Rect();n.getBoundsInScreen(nativeRect)
        if(nativeRect.width()<12 || nativeRect.height()<12)return null
        // A whole verified incoming notification may invoke its actual reply button.
        // This never forwards raw touches or invokes the profile/background controls.
        val rect=if(id==SocialPolicy.FEED_REPLY) replyHitRect(n) ?: return null else nativeRect
        return Target(id,rect,n.windowId,n.hashCode(),label.hashCode())
    }
    /** Walk the same hierarchy that the read-only on-bike inspector successfully sees.
     * Carry ancestor IDs in the traversal rather than querying a separate parent chain. */
    private fun visitControls(root:AccessibilityNodeInfo,block:(AccessibilityNodeInfo,Target?)->Unit) {
        var visited=0
        fun walk(n:AccessibilityNodeInfo,parents:Set<String>,depth:Int) {
            if(depth>50 || ++visited>2000)return
            var id=n.viewIdResourceName
            if(id in setOf(SocialPolicy.PREFIX+"inclass_music_container",SocialPolicy.PREFIX+"inclass_feed_recyclerview",SocialPolicy.PREFIX+"feed_item_root",SocialPolicy.LEADERBOARD,SocialPolicy.PREFIX+"leaderboard_row")) {
                if(!n.refresh())return
                id=n.viewIdResourceName
            }
            if(id in SocialPolicy.clickIds || id==SocialPolicy.LEADERBOARD)
                block(n,descriptor(n,id==SocialPolicy.LEADERBOARD,parents))
            val path=if(id==null) parents else parents+id
            for(i in 0 until n.childCount) {
                val child=n.getChild(i) ?: continue
                try { walk(child,path,depth+1) } finally { child.recycle() }
            }
        }
        walk(root,emptySet(),0)
    }
    fun capture(x:Int,y:Int,downTime:Long):Gesture? {
        lastTouch="DOWN $x,$y"
        val root=workoutRoot() ?: return null
        try {
            var click:Target?=null;var scroll:Target?=null;var seen=0;var allowed=0
            visitControls(root) { _,target ->
                seen++
                if(target!=null) {
                    allowed++
                    if(target.rect.contains(x,y)) {
                        if(target.id==SocialPolicy.LEADERBOARD)scroll=target
                        else if(click==null || target.rect.width()*target.rect.height()<click!!.rect.width()*click!!.rect.height())click=target
                    }
                }
            }
            val summary="controls=$seen allowed=$allowed tap=${click!=null} scroll=${scroll!=null}"
            result("Touch match: $summary")
            return Gesture(x,y,downTime,click,scroll,summary)
        } finally { root.recycle() }
    }
    private fun perform(target:Target,x:Int,y:Int,scroll:Boolean,action:Int):Boolean {
        val root=workoutRoot() ?: return false
        try {
            var done=false
            result("Social target changed or unavailable at release")
            visitControls(root) { node,now ->
                if(!done && now!=null && now==target && now.rect.contains(x,y)) {
                    done=node.performAction(action)
                    val kind=when(target.id) {
                        SocialPolicy.HIGH_FIVE -> "Avatar high-five"
                        SocialPolicy.FEED_REPLY -> "Incoming high-five reply"
                        SocialPolicy.MUSIC_HEART_AREA -> "Song heart"
                        SocialPolicy.MUSIC_OPEN -> "Music card"
                        else -> "Leaderboard scroll"
                    }
                    result("$kind: "+if(done) "control accepted action" else "control rejected action")
                    if(done && !scroll) GuardService.instance?.socialActionAccepted(target.id)
                }
            }
            return done
        } finally { root.recycle() }
    }
    fun finish(gesture:Gesture,x:Int,y:Int,eventTime:Long):Boolean {
        lastTouch+="; UP $x,$y"
        val elapsed=eventTime-gesture.at
        if(elapsed !in 0L..1800L){result("Gesture blocked: duration");return false}
        val dx=x-gesture.x;val dy=y-gesture.y
        val slop=ViewConfiguration.get(service).scaledTouchSlop
        val scroll=gesture.scroll
        if(scroll!=null && abs(dy)>4*slop && abs(dy)>abs(dx)*1.5 && scroll.rect.contains(x,y)) {
            val action=if(dy<0)AccessibilityNodeInfo.ACTION_SCROLL_FORWARD else AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD
            return perform(scroll,x,y,true,action)
        }
        val click=gesture.click
        if(click!=null && elapsed<=650L && abs(dx)<=2*slop && abs(dy)<=2*slop && click.rect.contains(x,y))
            return perform(click,x,y,false,AccessibilityNodeInfo.ACTION_CLICK)
        result("No matching gesture: ${gesture.matchSummary}; duration=${elapsed}ms; movement=${abs(dx)},${abs(dy)}")
        return false
    }
    fun dumpDiagnostics(writer:PrintWriter) {
        writer.println("Last touch (memory only): $lastTouch")
        history.forEach { writer.println("Recent Social: $it") }
        if(GuardService.instance?.socialActive!=true)return
        val root=workoutRoot() ?: return
        try {
            visitControls(root) { node,target ->
                val nativeRect=Rect();node.getBoundsInScreen(nativeRect)
                writer.println("Control "+node.viewIdResourceName?.substringAfter(":id/")+" native="+nativeRect.toShortString()+" permitted="+(target!=null)+" hit="+target?.rect?.toShortString())
            }
        } finally { root.recycle() }
    }

}
