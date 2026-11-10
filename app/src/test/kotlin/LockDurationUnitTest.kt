import com.touchguard.peloton.LockDuration
import org.junit.Assert.assertEquals
import org.junit.Test

class LockDurationUnitTest {
    @Test fun normalActivationHasTwoHourDefaultAndHonorsLongerRides() {
        assertEquals(7_200_000L,LockDuration.millis())
        assertEquals(1_800_000L,LockDuration.millis(30))
        assertEquals(10_800_000L,LockDuration.millis(180))
        assertEquals(21_600_000L,LockDuration.millis(360))
    }
    @Test fun onlyTheExplicitTestGetsThirtySeconds() {
        assertEquals(30_000L,LockDuration.millis(120,explicitTest=true))
        assertEquals(7_200_000L,LockDuration.millis(120,explicitTest=false))
    }
}
