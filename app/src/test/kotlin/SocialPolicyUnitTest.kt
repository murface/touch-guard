import com.touchguard.peloton.SocialPolicy as P
import org.junit.Test

class SocialPolicyUnitTest {
    @Test fun permitsOnlyKnownActionsInTheirOwnContainers() {
        check(P.mayClick(P.HIGH_FIVE,setOf(P.PREFIX+"leaderboard_row",P.LEADERBOARD)))
        check(!P.mayClick(P.MUSIC_HEART,setOf(P.PREFIX+"inclass_music_container")))
        check(P.mayClick(P.MUSIC_HEART_AREA,setOf(P.PREFIX+"inclass_music_container")))
        check(P.mayClick(P.MUSIC_OPEN,setOf(P.PREFIX+"inclass_music_container")))
        check(P.mayScroll(P.LEADERBOARD,setOf(P.PREFIX+"leaderboard_container")))
        for(id in listOf("pause_button","pause_view","main_window","timeline_container","primary_metrics_bar","leaderboard_row_click_overlay","feed_scorecard_touch_area","feed_press_overlay","top_status_bar_back","filter_button"))
            check(!P.mayClick(P.PREFIX+id,setOf(P.PREFIX+"inclass_music_container",P.PREFIX+"leaderboard_row",P.LEADERBOARD)))
        check(!P.mayClick(P.HIGH_FIVE,emptySet()))
        check(!P.mayScroll(P.PREFIX+"inclass_feed_recyclerview",setOf(P.PREFIX+"leaderboard_container")))
    }
    @Test fun feedHighFivesUseTheActualButtonAction() {
        val parents=setOf(P.PREFIX+"feed_item_root",P.PREFIX+"inclass_feed_recyclerview")
        // The same dedicated button handles incoming high-fives and milestone congratulations.
        check(P.mayClick(P.FEED_REPLY,parents,"High five"))
        check(P.mayClick(P.FEED_REPLY,parents,"High five back"))
        check(!P.mayClick(P.FEED_REPLY,parents,"Open profile"))
        check(!P.mayClick(P.FEED_REPLY,parents,""))
        check(!P.mayClick(P.FEED_REPLY,emptySet(),"High five"))
        check(!P.mayClick(P.PREFIX+"feed_scorecard_touch_area",parents,"High five"))
        check(!P.mayClick(P.PREFIX+"feed_press_overlay",parents,"High five"))
    }
}
