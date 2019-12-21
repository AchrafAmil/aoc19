package days

import XY
import kotlin.math.atan2
import kotlin.math.pow
import kotlin.math.sqrt

private fun main() {
    val map = INPUT
        .split('\n')
        .map { it.toCharArray() }
        .mapIndexed { y, chars ->
            chars.mapIndexed { x, char -> XY(x, y) to char }
        }
        .flatten()
        .toMap()

    val day1result = day10Part1(map)
    println(day1result)
    println(day10Part2(map, day1result!!.first.key))
}

fun day10Part2(points: Map<XY, Char>, laser: XY): XY {
    val asteroidsWithLaserSphericalCoordinates = points
        .mapNotNull { (asteroid, char) ->
            if (char != '#' || asteroid == laser) {
                null
            } else {
                val diagonal = sqrt((asteroid.y - laser.y).toDouble().pow(2) + (asteroid.x - laser.x).toDouble().pow(2))
                val angle = atan2((asteroid.x - laser.x).toDouble(), (asteroid.y - laser.y).toDouble())
                asteroid to DistanceAngle(diagonal, angle)
            }
        }

    val groupedByAngle = asteroidsWithLaserSphericalCoordinates
        .groupBy { it.second.angle }
        .toSortedMap(compareByDescending { it })

    val destroyedAsteroids = mutableListOf<Pair<XY, DistanceAngle>>()

    while (destroyedAsteroids.size < asteroidsWithLaserSphericalCoordinates.size) {
        for ((_, asteroids) in groupedByAngle) {
            val toDestroy = asteroids
                .sortedBy { it.second.distance }
                .firstOrNull { !destroyedAsteroids.contains(it) }

            if (toDestroy != null) {
                destroyedAsteroids.add(toDestroy)
            }
        }
    }

    return destroyedAsteroids[199].first
}

fun day10Part1(points: Map<XY, Char>): Pair<Map.Entry<XY, Char>, Int>? {

    return points
        .filter { it.value == '#' }
        .map { it to visibleAsteroidsFor(points, it.key).size }.maxBy { it.second }
}

private fun visibleAsteroidsFor(points: Map<XY, Char>, laser: XY): List<Pair<XY, DistanceAngle>> {
    val asteroidsWithLaserSphericalCoordinates = points
        .mapNotNull { (asteroid, char) ->
            if (char == '#' && asteroid != laser) {
                val diagonal = sqrt((asteroid.y - laser.y).toDouble().pow(2) + (asteroid.x - laser.x).toDouble().pow(2))
                val angle = atan2((asteroid.x - laser.x).toDouble(), (asteroid.y - laser.y).toDouble())
                asteroid to DistanceAngle(diagonal, angle)
            } else {
                null
            }
        }

    val groupedByAngle = asteroidsWithLaserSphericalCoordinates
        .groupBy { it.second.angle }
        .toSortedMap(compareByDescending { it })

    return groupedByAngle
        .mapNotNull { it.value.minBy { it.second.distance } }
}

data class DistanceAngle(val distance: Double, val angle: Double)

private val INPUT =
    """
    .###.###.###.#####.#
    #####.##.###..###..#
    .#...####.###.######
    ######.###.####.####
    #####..###..########
    #.##.###########.#.#
    ##.###.######..#.#.#
    .#.##.###.#.####.###
    ##..#.#.##.#########
    ###.#######.###..##.
    ###.###.##.##..####.
    .##.####.##########.
    #######.##.###.#####
    #####.##..####.#####
    ##.#.#####.##.#.#..#
    ###########.#######.
    #.##..#####.#####..#
    #####..#####.###.###
    ####.#.############.
    ####.#.#.##########.
     """.trimIndent()
