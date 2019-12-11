import days.*
import opcode.OpcodeComputer
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

    @Suppress("DEPRECATION")
    @Test
    fun `day 5 0 jump-to tests`() {
        val input =
            "3,12,6,12,15,1,13,14,13,4,13,99,-1,0,1,9"
        val ints = input.split(',')
            .map { it.toInt() }

        val result0 = OpcodeComputer(ints).compute(listOf(0))
        val result1 = OpcodeComputer(ints).compute(listOf(42))

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

    @Suppress("DEPRECATION")
    private fun day5Test(args: Int): List<Int> {
        val input =
            "3,21,1008,21,8,20,1005,20,22,107,8,21,20,1006,20,31,1106,0,36,98,0,0,1002,21,125,20,4,20,1105,1,46,104,999,1105,1,46,1101,1000,1,20,4,20,1105,1,46,98,99"
        val ints = input.split(',')
            .map { it.toInt() }

        return OpcodeComputer(ints).compute(listOf(args))
    }

    @Test
    fun `day 7 test output of specified phase settings`() {
        val input = "3,15,3,16,1002,16,10,16,1,16,15,15,4,15,99,0,0"
            .split(',')
            .map { it.toInt() }
        val phaseSettings = listOf(4, 3, 2, 1, 0)

        val output = fiveAmplifiers(input, phaseSettings)

        Assert.assertEquals(43210, output)
    }

    @Test
    fun `day 7 find highest score's phase settings`() {
        val controllerSoftware = "3,15,3,16,1002,16,10,16,1,16,15,15,4,15,99,0,0"
            .split(',')
            .map { it.toInt() }

        val allPossiblePhaseSettings = allPossiblePhaseSettings(controllerSoftware)
        val output = allPossiblePhaseSettings.maxBy { it.value }?.value

        Assert.assertEquals(43210, output)
    }

    @Test
    fun `day 7 find highest score's phase settings 2`() {
        val controllerSoftware = "3,23,3,24,1002,24,10,24,1002,23,-1,23,101,5,23,23,1,24,23,23,4,23,99,0,0"
            .split(',')
            .map { it.toInt() }

        val allPossiblePhaseSettings = allPossiblePhaseSettings(controllerSoftware)
        val output = allPossiblePhaseSettings.maxBy { it.value }?.value

        Assert.assertEquals(54321, output)
    }

    @Test
    fun `day 10 destroy asteroids`() {
        val input =
            """
                .#..##.###...#######
                ##.############..##.
                .#.######.########.#
                .###.#######.####.#.
                #####.##.#.##.###.##
                ..#####..#.#########
                ####################
                #.####....###.#.#.##
                ##.#################
                #####.##.###..####..
                ..######..##.#######
                ####.##.####...##..#
                .#####..#.######.###
                ##...#.##########...
                #.##########.#######
                .####.#.###.###.#.##
                ....##.##.###..#####
                .#.#.###########.###
                #.#.#.#####.####.###
                ###.##.####.##.#..##
            """.trimIndent()
                .split('\n')
                .map { it.toCharArray() }
                .mapIndexed { y, chars ->
                    chars.mapIndexed { x, char -> XY(x, y) to char }
                }
                .flatten()
                .toMap()

        val result = day10Part2(input, XY(11, 13))

        Assert.assertEquals(XY(8, 2), result)
    }

    @Test
    fun `day 10 destroy asteroids large input`() {
        val input =
            """
                .............#..#.#......##........#..#
                .#...##....#........##.#......#......#.
                ..#.#.#...#...#...##.#...#.............
                .....##.................#.....##..#.#.#
                ......##...#.##......#..#.......#......
                ......#.....#....#.#..#..##....#.......
                ...................##.#..#.....#.....#.
                #.....#.##.....#...##....#####....#.#..
                ..#.#..........#..##.......#.#...#....#
                ...#.#..#...#......#..........###.#....
                ##..##...#.#.......##....#.#..#...##...
                ..........#.#....#.#.#......#.....#....
                ....#.........#..#..##..#.##........#..
                ........#......###..............#.#....
                ...##.#...#.#.#......#........#........
                ......##.#.....#.#.....#..#.....#.#....
                ..#....#.###..#...##.#..##............#
                ...##..#...#.##.#.#....#.#.....#...#..#
                ......#............#.##..#..#....##....
                .#.#.......#..#...###...........#.#.##.
                ........##........#.#...#.#......##....
                .#.#........#......#..........#....#...
                ...............#...#........##..#.#....
                .#......#....#.......#..#......#.......
                .....#...#.#...#...#..###......#.##....
                .#...#..##................##.#.........
                ..###...#.......#.##.#....#....#....#.#
                ...#..#.......###.............##.#.....
                #..##....###.......##........#..#...#.#
                .#......#...#...#.##......#..#.........
                #...#.....#......#..##.............#...
                ...###.........###.###.#.....###.#.#...
                #......#......#.#..#....#..#.....##.#..
                .##....#.....#...#.##..#.#..##.......#.
                ..#........#.......##.##....#......#...
                ##............#....#.#.....#...........
                ........###.............##...#........#
                #.........#.....#..##.#.#.#..#....#....
                ..............##.#.#.#...........#.....
            """.trimIndent()
                .split('\n')
                .map { it.toCharArray() }
                .mapIndexed { y, chars ->
                    chars.mapIndexed { x, char -> XY(x, y) to char }
                }
                .flatten()
                .toMap()

        val result = day10Part2(input, XY(26, 29))

        Assert.assertEquals(XY(14, 19), result)
    }

    @Test
    fun `day 10 destroy asteroids large input 2`() {
        val input =
            """
                .##.#.#....#.#.#..##..#.#.
                #.##.#..#.####.##....##.#.
                ###.##.##.#.#...#..###....
                ####.##..###.#.#...####..#
                ..#####..#.#.#..#######..#
                .###..##..###.####.#######
                .##..##.###..##.##.....###
                #..#..###..##.#...#..####.
                ....#.#...##.##....#.#..##
                ..#.#.###.####..##.###.#.#
                .#..##.#####.##.####..#.#.
                #..##.#.#.###.#..##.##....
                #.#.##.#.##.##......###.#.
                #####...###.####..#.##....
                .#####.#.#..#.##.#.#...###
                .#..#.##.#.#.##.#....###.#
                .......###.#....##.....###
                #..#####.#..#..##..##.#.##
                ##.#.###..######.###..#..#
                #.#....####.##.###....####
                ..#.#.#.########.....#.#.#
                .##.#.#..#...###.####..##.
                ##...###....#.##.##..#....
                ..##.##.##.#######..#...#.
                .###..#.#..#...###..###.#.
                #..#..#######..#.#..#..#.#
            """.trimIndent()
                .split('\n')
                .map { it.toCharArray() }
                .mapIndexed { y, chars ->
                    chars.mapIndexed { x, char -> XY(x, y) to char }
                }
                .flatten()
                .toMap()

        val result = day10Part2(input, XY(19, 14))

        Assert.assertEquals(XY(3, 5), result)
    }
}