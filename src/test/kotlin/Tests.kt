import days.compute
import days.massIncludingNecessaryFuel
import org.junit.Test
import org.junit.Assert

class Tests {
    @Test
    fun `recursive fuel mass`() {
        Assert.assertEquals(966, massIncludingNecessaryFuel(1969))
    }

    @Test
    fun `recursive fuel mass 2`() {
        Assert.assertEquals(50346, massIncludingNecessaryFuel(100756))
    }

    @Test
    fun `recursive fuel mass 3`() {
        Assert.assertEquals(2, massIncludingNecessaryFuel(14))
    }

    @Test
    fun `opcode 1`() {
        Assert.assertEquals("30,1,1,4,2,5,6,0,99", compute("1,1,1,4,99,5,6,0,99"))
        Assert.assertEquals("2,4,4,5,99,9801", compute("2,4,4,5,99,0"))
    }
}