package opcode

import Direction
import XY
import java.lang.Thread.sleep

class RepairDroid(software: List<Long>) {
    private val computer = RealtimeOpcodeComputer(software, isVerbose = false).also { Thread { it.start() }.start() }
    val map = mutableMapOf<XY, Pixel>().also { it[XY(0, 0)] = Pixel.EMPTY }
    var droidPosition = XY(0, 0)
        private set

    fun move(direction: Direction) {
        val targetPosition = droidPosition.move(direction)
        when (map[targetPosition]) {
            // Weird behavior when checking from map[targetPosition] (the robot outputs cache):
            // This cache is sometimes surprisingly inconsistent.
            // Re-asking the robot for the same XY from the same XY is not guaranteed to be always the same.
            else -> {
                synchronized(computer.inputStream) {
                    computer.inputStream.add(direction.value.toLong())
                }
                while (computer.outputStream.isEmpty()) sleep(1)

                when (computer.outputStream.removeAt(0)) {
                    0L -> {
                        map[targetPosition] = Pixel.WALL
                    }
                    1L -> {
                        map[targetPosition] = Pixel.EMPTY
                        droidPosition = targetPosition
                    }
                    2L -> {
                        map[targetPosition] = Pixel.GOAL
                        droidPosition = targetPosition
                    }
                }
            }
        }
    }

    fun visualize() = map.visualize()

    private fun Map<XY, Pixel>.visualize() {
        for (y in ((keys.minBy { it.y }!!.y)..((keys.maxBy { it.y }!!.y)))) {
            for (x in ((keys.minBy { it.x }!!.x)..((keys.maxBy { it.x }!!.x)))) {
                val pixel = getOrDefault(XY(x, y), Pixel.UNKNOWN)
                print(
                    when (XY(x, y)) {
                        droidPosition -> "*"
                        XY(0, 0) -> "O"
                        else -> {
                            when (pixel) {
                                Pixel.UNKNOWN -> " "
                                Pixel.EMPTY -> "░"
                                Pixel.WALL -> "▓"
                                Pixel.GOAL -> "X"
                            }
                        }
                    }
                )
            }
            println()
        }
    }

    enum class Pixel {
        UNKNOWN, EMPTY, WALL, GOAL
    }
}
