package com.touchguard.peloton

/** Only semantic actions are allowed. There is deliberately no coordinate injection fallback. */
object SocialPolicy {
    const val PACKAGE="com.peloton.activity"
    const val VERSION=2083457L
    const val PREFIX="$PACKAGE:id/"
    const val HIGH_FIVE=PREFIX+"leaderboard_card_high_five_touch_area"
    const val FEED_REPLY=PREFIX+"feed_item_anim_view"
    const val MUSIC_OPEN=PREFIX+"inclass_music_collapsed_icon_delegate"
    const val MUSIC_HEART=PREFIX+"inclass_music_like_button"
    const val MUSIC_HEART_AREA=PREFIX+"inclass_music_like_button_container"
    const val LEADERBOARD=PREFIX+"leaderboard_recycler"
    val clickIds=listOf(HIGH_FIVE,FEED_REPLY,MUSIC_HEART_AREA,MUSIC_OPEN)
    fun mayClick(id:String,ancestors:Set<String>,actionLabel:String=""):Boolean = when(id) {
        HIGH_FIVE -> PREFIX+"leaderboard_recycler" in ancestors && PREFIX+"leaderboard_row" in ancestors
        FEED_REPLY -> PREFIX+"feed_item_root" in ancestors && PREFIX+"inclass_feed_recyclerview" in ancestors &&
            isHighFiveAction(actionLabel)
        MUSIC_OPEN,MUSIC_HEART_AREA -> PREFIX+"inclass_music_container" in ancestors
        else -> false
    }
    fun isHighFiveAction(label:String)=label.trim().lowercase() in setOf("high five","high five back")
    fun mayScroll(id:String,ancestors:Set<String>)=id==LEADERBOARD && PREFIX+"leaderboard_container" in ancestors
}
