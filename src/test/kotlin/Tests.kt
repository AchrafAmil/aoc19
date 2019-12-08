import days.compute
import days.massIncludingNecessaryFuel
import org.junit.Assert
import org.junit.Test

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
    fun `day 2`() {
        Assert.assertEquals("30,1,1,4,2,5,6,0,99", compute("1,1,1,4,99,5,6,0,99"))
        Assert.assertEquals("2,4,4,5,99,9801", compute("2,4,4,5,99,0"))
    }

    @Test
    fun `day 5 0 jump-to tests`() {
        val input =
            "3,12,6,12,15,1,13,14,13,4,13,99,-1,0,1,9"
        val ints = input.split(',')
            .map { it.toInt() }

        val result0 = compute(ints, listOf(0))
        val result1 = compute(ints, listOf(42))

        Assert.assertEquals(listOf(0), result0)
        Assert.assertEquals(listOf(1), result1)
    }

    @Test
    fun `day 5 smaller equal or greater than 8`() {
        val smaller = day5Test(1)
        val equal = day5Test(8)
        val greater = day5Test(10)

        Assert.assertEquals(listOf(999), smaller)
        Assert.assertEquals(listOf(1000), equal)
        Assert.assertEquals(listOf(1001), greater)
    }

    private fun day5Test(args: Int): List<Int> {
        val input =
            "3,21,1008,21,8,20,1005,20,22,107,8,21,20,1006,20,31,1106,0,36,98,0,0,1002,21,125,20,4,20,1105,1,46,104,999,1105,1,46,1101,1000,1,20,4,20,1105,1,46,98,99"
        val ints = input.split(',')
            .map { it.toInt() }

        return compute(ints, listOf(args))
    }
}