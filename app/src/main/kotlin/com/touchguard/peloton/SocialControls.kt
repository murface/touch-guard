package com.touchguard.peloton

import android.accessibilityservice.AccessibilityService
import android.graphics.Rect
import android.os.SystemClock
import android.view.ViewConfiguration
import android.view.accessibility.AccessibilityNodeInfo
import kotlin.math.abs

/** Local control identification, called only by the one Touch Guard service in Social mode. */
class SocialControls(private val service:AccessibilityService) {
    data class Target(val id:String,val rect:Rect,val window:Int,val source:Int,val messageHash:Int)
    data class Gesture(val x:Int,val y:Int,val at:Long,val click:Target?,val scroll:Target?)
    private fun workoutRoot():AccessibilityNodeInfo? {
        val root=service.rootInActiveWindow ?: return null
        val version=try { service.packageManager.getPackageInfo(SocialPolicy.PACKAGE,0).longVersionCode } catch(_:Exception){-1L}
        if(root.packageName?.toString()!=SocialPolicy.PACKAGE || version!=SocialPolicy.VERSION){root.recycle();return null}
        for(id in listOf("main_inclass_fragment_container","timeline_container","primary_metrics_bar")) {
            val nodes=root.findAccessibilityNodeInfosByViewId(SocialPolicy.PREFIX+id)
            val found=nodes.any { it.isVisibleToUser };nodes.forEach { it.recycle() }
            if(!found){root.recycle();return null}
        }
        return root
    }
    fun supportedWorkout():Boolean { val r=workoutRoot() ?: return false;r.recycle();return true }
    private fun ancestors(node:AccessibilityNodeInfo):Set<String> {
        val result=mutableSetOf<String>();var parent=node.parent;var count=0
        while(parent!=null && count++<40){parent.viewIdResourceName?.let { result.add(it) };val next=parent.parent;parent.recycle();parent=next}
        parent?.recycle();return result
    }
    private fun feedLabel(node:AccessibilityNodeInfo):String {
        var parent=node.parent;var count=0
        while(parent!=null && count++<40) {
            if(parent.viewIdResourceName==SocialPolicy.PREFIX+"feed_item_root") {
                val labels=parent.findAccessibilityNodeInfosByViewId(SocialPolicy.PREFIX+"feed_item_label")
                val label=labels.firstOrNull()?.text?.toString() ?: ""
                labels.forEach { it.recycle() };parent.recycle();return label
            }
            val next=parent.parent;parent.recycle();parent=next
        }
        parent?.recycle();return ""
    }
    private fun descriptor(n:AccessibilityNodeInfo,scroll:Boolean):Target? {
        if(!n.isVisibleToUser || !n.isEnabled || (if(scroll) !n.isScrollable else !n.isClickable))return null
        val id=n.viewIdResourceName ?: return null
        val parents=ancestors(n)
        val label=if(id==SocialPolicy.FEED_REPLY)feedLabel(n) else ""
        if(if(scroll) !SocialPolicy.mayScroll(id,parents) else !SocialPolicy.mayClick(id,parents,label))return null
        val rect=Rect();n.getBoundsInScreen(rect)
        if(rect.width()<12 || rect.height()<12)return null
        return Target(id,rect,n.windowId,n.hashCode(),label.hashCode())
    }
    fun capture(x:Int,y:Int):Gesture? {
        val root=workoutRoot() ?: return null
        try {
            var click:Target?=null
            for(id in SocialPolicy.clickIds) {
                val nodes=root.findAccessibilityNodeInfosByViewId(id)
                for(n in nodes) { val d=descriptor(n,false);if(click==null && d!=null && d.rect.contains(x,y))click=d;n.recycle() }
                if(click!=null)break
            }
            var scroll:Target?=null
            val nodes=root.findAccessibilityNodeInfosByViewId(SocialPolicy.LEADERBOARD)
            for(n in nodes){val d=descriptor(n,true);if(scroll==null && d!=null && d.rect.contains(x,y))scroll=d;n.recycle()}
            return Gesture(x,y,SystemClock.uptimeMillis(),click,scroll)
        } finally {root.recycle()}
    }
    private fun perform(target:Target,x:Int,y:Int,scroll:Boolean,action:Int):Boolean {
        val root=workoutRoot() ?: return false
        try {
            val nodes=root.findAccessibilityNodeInfosByViewId(target.id)
            var done=false
            for(n in nodes) {
                val now=descriptor(n,scroll)
                if(!done && now!=null && now==target && now.rect.contains(x,y))done=n.performAction(action)
                n.recycle()
            }
            return done
        } finally {root.recycle()}
    }
    fun finish(gesture:Gesture,x:Int,y:Int):Boolean {
        val elapsed=SystemClock.uptimeMillis()-gesture.at
        if(elapsed !in 40L..1800L)return false
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
        return false
    }
}
