data class XY(val x: Int, val y: Int) {
    val neighbors: Collection<XY>
        get() = listOf(
            XY(x - 1, y),
            XY(x + 1, y),
            XY(x, y - 1),
            XY(x, y + 1)
        )

    fun move(direction: Direction): XY {
        return when (direction) {
            Direction.NORTH -> XY(x, y - 1)
            Direction.SOUTH -> XY(x, y + 1)
            Direction.WEST -> XY(x - 1, y)
            Direction.EAST -> XY(x + 1, y)
        }
    }
}

enum class Direction(val value: Int) {
    NORTH(1), SOUTH(2), WEST(3), EAST(4);

    companion object {
        private val map = values().associateBy(Direction::value)
        fun fromInt(int: Int) = map.getValue(int)
    }
}

fun <E> Map<XY, E>.visualize(picker: (XY, E?) -> Char) {
    for (y in ((keys.minBy { it.y }!!.y)..((keys.maxBy { it.y }!!.y)))) {
        for (x in ((keys.minBy { it.x }!!.x)..((keys.maxBy { it.x }!!.x)))) {
            val pixel = this[XY(x, y)]
            print(
                picker(XY(x, y), pixel)
            )
        }
        println()
    }
}