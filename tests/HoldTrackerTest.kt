import com.touchguard.peloton.HoldTracker

fun main() {
    val t=HoldTracker()
    t.event(true,true,false,0); check(!t.complete(9000))
    t.event(false,true,false,9000); check(!t.complete(13999)); check(t.complete(14000))
    t.event(true,true,true,12000); check(t.complete(14000)) // auto-repeat cannot extend hold
    t.event(false,false,false,14001); check(!t.complete(30000))
    t.event(false,true,false,30000); check(!t.complete(34999)); check(t.complete(35000))
    t.reset(); check(!t.complete(100000)); check(!t.up && !t.down)
    // A release/re-press must require a new, full five-second overlap.
    t.event(false,true,false,0); t.event(true,true,false,100)
    t.event(true,false,false,4999); t.event(true,true,false,5000)
    check(!t.complete(9999)); check(t.complete(10000))
    // One continuous hold must toggle only once, despite repeats or a partial release.
    check(t.takeComplete(10000)); check(!t.takeComplete(20000))
    t.event(true,true,true,20000); check(!t.takeComplete(30000))
    t.event(true,false,false,30001); t.event(true,true,false,30002)
    check(!t.takeComplete(40000))
    t.event(true,false,false,40001); t.event(false,false,false,40002)
    t.event(true,true,false,50000); t.event(false,true,false,50001)
    check(!t.takeComplete(55000)); check(t.takeComplete(55001))
    // External timeout/emergency unlock must not turn ON again from held keys.
    t.requireRelease(); t.event(false,true,true,60000); check(!t.takeComplete(65000))
    t.event(true,false,false,65001); t.event(false,false,false,65002)
    t.requireRelease()
    t.event(false,true,false,70000); t.event(true,true,false,70001)
    check(t.takeComplete(75001))
    t.reset(); t.setBothRequired(false)
    t.event(false,true,false,0)
    check(!t.takeComplete(4999)); check(t.takeComplete(5000)); check(!t.takeComplete(20000))
    t.event(false,false,false,20001); t.event(false,true,false,20002)
    check(t.takeComplete(25002))
    t.reset(); t.event(true,true,false,0); check(!t.takeComplete(10000))
    t.event(false,true,false,10001); check(!t.takeComplete(20000)) // two-key TalkBack chord is not single-key shortcut
    t.event(true,false,false,20001); check(!t.takeComplete(30000))
    t.event(false,false,false,30001); t.event(false,true,false,30002)
    check(t.takeComplete(35002))
    println("HoldTracker: dual and single key timing, repeats, cancellation, release latch and TalkBack-chord separation passed")
}
