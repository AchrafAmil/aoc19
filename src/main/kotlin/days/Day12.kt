package days

import lcm
import kotlin.math.abs
import kotlin.math.sign

private fun main() {
    println(day12Part2(INPUT))
}

fun day12Part2(input: List<Moon>): Long {
    return findPeriod(input)
}

fun findPeriod(moons: List<Moon>): Long {
    val xPeriod = findAxisPeriod(moons, XYZ::x)
    val yPeriod = findAxisPeriod(moons, XYZ::y)
    val zPeriod = findAxisPeriod(moons, XYZ::z)

    return lcm(xPeriod, yPeriod, zPeriod)
}

fun findAxisPeriod(moons: List<Moon>, axis: XYZ.() -> Int): Long {
    val initialAxisList = moons.map { it.position.axis() to it.velocity.axis() }
    var axisList = initialAxisList
    var stepsCount = 0L
    while (axisList != initialAxisList || stepsCount == 0L) {
        stepsCount++
        axisList = axisList
            .map { self ->
                var acceleration = 0
                axisList
                    .forEach { other ->
                        when {
                            self.first > other.first -> acceleration--
                            self.first < other.first -> acceleration++
                        }
                    }
                val newVelocity = self.second + acceleration
                val newPos = self.first + newVelocity
                newPos to newVelocity
            }
    }

    return stepsCount
}

fun day12Part1(input: List<Moon>): Int {
    val moons = simulateGravity(input, 1000)

    return moons.sumBy { it.position.energy * it.velocity.energy }
}

fun simulateGravity(initialState: List<Moon>, stepsCount: Int): List<Moon> {
    var moons = initialState
    repeat(stepsCount) {
        println("step $it")
        moons = moons
            .map { moon ->
                val delta = moons
                    .map { other ->
                        if (other != moon) {
                            moon.attractionTo(other.position)
                        } else {
                            XYZ(0, 0, 0)
                        }
                    }
                    .fold(XYZ(0, 0, 0)) { acc, delta -> acc + delta }

                val newVelocity = moon.velocity + delta
                Moon(moon.position + newVelocity, newVelocity)
            }
        println("$moons")
    }
    return moons
}

data class XYZ(val x: Int, val y: Int, val z: Int) {
    operator fun plus(other: XYZ) = XYZ(x + other.x, y + other.y, z + other.z)
    val energy
        get() = abs(x) + abs(y) + abs(z)
}

data class Moon(val position: XYZ, val velocity: XYZ) {
    fun attractionTo(point: XYZ): XYZ {
        return XYZ(
            (point.x - position.x).sign,
            (point.y - position.y).sign,
            (point.z - position.z).sign
        )
    }
}

private val INPUT = listOf(
    Moon(XYZ(3, -6, 6), XYZ(0, 0, 0)),
    Moon(XYZ(10, 7, -9), XYZ(0, 0, 0)),
    Moon(XYZ(-3, -7, 9), XYZ(0, 0, 0)),
    Moon(XYZ(-8, 0, 4), XYZ(0, 0, 0))
)
