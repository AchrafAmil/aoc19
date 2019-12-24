package days

import Direction
import XY
import mergeReduce
import opcode.RealtimeOpcodeComputer
import visualize
import java.lang.Thread.sleep

private fun main() {
    val software = INPUT
        .split(',')
        .map { it.toLong() }

    //println(day17Part1(software))
    println(day17Part2(software))
}

fun day17Part2(software: List<Long>): Long {

    // get full path then compress by hand (in a text editor, less than 2 minutes) ¯\_(ツ)_/¯
    //val outputWithXy = getOutputWithXy(software)
    //val fullPath = getMainPath(outputWithXy)

    // compression result
    val robotInstructions = """
        A,B,B,A,C,A,C,A,C,B
        L,6,R,12,R,8
        R,8,R,12,L,12
        R,12,L,12,L,4,L,4
        y
        
    """.trimIndent()

    val crackedSoftware = software.toMutableList().also { it[0] = 2 }
    val computer = RealtimeOpcodeComputer(crackedSoftware, false)
    computer.inputStream.addAll(robotInstructions.map { it.toLong() })

    Thread {
        // camera live stream
        var index = 0
        while (true) {
            synchronized(computer.outputStream) {
                while (computer.outputStream.size > index) {
                    print(computer.outputStream[index++].toChar())
                }
            }
            sleep(2)
        }
    }.start()

    computer.start()

    println(computer.outputStream.map { it.toChar() }.joinToString(""))

    return computer.outputStream.last()
}

fun getMainPath(map: Map<XY, Char>): List<String> {
    val path = mutableListOf<String>()
    var robotPosition = map.entries.find { it.value in "^<>v" }!!.key
    var robotDirection = when (map[robotPosition]) {
        '^' -> Direction.NORTH
        '<' -> Direction.WEST
        '>' -> Direction.EAST
        'v' -> Direction.SOUTH
        else -> throw IllegalStateException()
    }

    val directionToStart = Direction.values().find { direction ->
        val neighbor = robotPosition.move(direction)
        map[neighbor] == '#'
    }!!
    var turning = when (directionToStart) {
        robotDirection.turnRight() -> 'R'
        robotDirection.turnLeft() -> 'L'
        else -> throw IllegalStateException()
    }

    path.add("$turning")
    robotDirection = directionToStart

    var next = robotPosition.move(directionToStart)
    var movesCount = 0

    while (map[next] == '#') {
        movesCount++
        robotPosition = next
        next = robotPosition.move(directionToStart)
    }

    path.add("$movesCount")

    while (robotPosition.neighbors.count { map[it] == '#' } > 1) {
        turning = when ('#') {
            map[robotPosition.move(robotDirection.turnRight())] -> 'R'
            map[robotPosition.move(robotDirection.turnLeft())] -> 'L'
            else -> throw IllegalStateException()
        }

        path.add("$turning")
        robotDirection = when (turning) {
            'R' -> robotDirection.turnRight()
            'L' -> robotDirection.turnLeft()
            else -> throw IllegalStateException()
        }

        next = robotPosition.move(robotDirection)
        movesCount = 0
        while (map[next] == '#') {
            movesCount++
            robotPosition = next
            next = robotPosition.move(robotDirection)
        }

        path.add("$movesCount")
    }

    return path
}

fun day17Part1(software: List<Long>): Int {
    val outputWithXy = getOutputWithXy(software)

    outputWithXy.visualize { _, char -> char ?: ' ' }
    println()

    val intersections = outputWithXy
        .filter { (xy, char) -> char == '#' && xy.neighbors.count { outputWithXy[it] == '#' } == 4 }

    outputWithXy.mergeReduce(intersections) { _, _ -> 'O' }
        .visualize { _, char -> char ?: ' ' }

    return intersections.keys.sumBy { (x, y) -> x * y }
}

private fun getOutputWithXy(software: List<Long>): Map<XY, Char> {
    val computer = RealtimeOpcodeComputer(software, false)

    computer.start()
    val output = computer.outputStream
        .map { it.toChar() }
        .joinToString("")

    val outputWithXy = output
        .split('\n')
        .mapIndexed { y, line ->
            line.mapIndexed { x, char ->
                XY(x, y) to char
            }
        }
        .flatten()
        .toMap()
    return outputWithXy
}


private const val INPUT =
    "1,330,331,332,109,3028,1101,0,1182,16,1101,1447,0,24,101,0,0,570,1006,570,36,1001,571,0,0,1001,570,-1,570,1001,24,1,24,1106,0,18,1008,571,0,571,1001,16,1,16,1008,16,1447,570,1006,570,14,21101,0,58,0,1105,1,786,1006,332,62,99,21101,333,0,1,21101,0,73,0,1105,1,579,1102,1,0,572,1102,1,0,573,3,574,101,1,573,573,1007,574,65,570,1005,570,151,107,67,574,570,1005,570,151,1001,574,-64,574,1002,574,-1,574,1001,572,1,572,1007,572,11,570,1006,570,165,101,1182,572,127,1002,574,1,0,3,574,101,1,573,573,1008,574,10,570,1005,570,189,1008,574,44,570,1006,570,158,1105,1,81,21102,340,1,1,1105,1,177,21101,0,477,1,1105,1,177,21102,1,514,1,21101,0,176,0,1105,1,579,99,21102,184,1,0,1105,1,579,4,574,104,10,99,1007,573,22,570,1006,570,165,101,0,572,1182,21102,1,375,1,21101,211,0,0,1105,1,579,21101,1182,11,1,21102,222,1,0,1106,0,979,21102,388,1,1,21102,1,233,0,1105,1,579,21101,1182,22,1,21101,0,244,0,1105,1,979,21102,1,401,1,21102,255,1,0,1105,1,579,21101,1182,33,1,21101,266,0,0,1105,1,979,21102,414,1,1,21102,1,277,0,1105,1,579,3,575,1008,575,89,570,1008,575,121,575,1,575,570,575,3,574,1008,574,10,570,1006,570,291,104,10,21101,0,1182,1,21102,313,1,0,1106,0,622,1005,575,327,1102,1,1,575,21101,0,327,0,1105,1,786,4,438,99,0,1,1,6,77,97,105,110,58,10,33,10,69,120,112,101,99,116,101,100,32,102,117,110,99,116,105,111,110,32,110,97,109,101,32,98,117,116,32,103,111,116,58,32,0,12,70,117,110,99,116,105,111,110,32,65,58,10,12,70,117,110,99,116,105,111,110,32,66,58,10,12,70,117,110,99,116,105,111,110,32,67,58,10,23,67,111,110,116,105,110,117,111,117,115,32,118,105,100,101,111,32,102,101,101,100,63,10,0,37,10,69,120,112,101,99,116,101,100,32,82,44,32,76,44,32,111,114,32,100,105,115,116,97,110,99,101,32,98,117,116,32,103,111,116,58,32,36,10,69,120,112,101,99,116,101,100,32,99,111,109,109,97,32,111,114,32,110,101,119,108,105,110,101,32,98,117,116,32,103,111,116,58,32,43,10,68,101,102,105,110,105,116,105,111,110,115,32,109,97,121,32,98,101,32,97,116,32,109,111,115,116,32,50,48,32,99,104,97,114,97,99,116,101,114,115,33,10,94,62,118,60,0,1,0,-1,-1,0,1,0,0,0,0,0,0,1,48,18,0,109,4,2101,0,-3,586,21001,0,0,-1,22101,1,-3,-3,21101,0,0,-2,2208,-2,-1,570,1005,570,617,2201,-3,-2,609,4,0,21201,-2,1,-2,1105,1,597,109,-4,2105,1,0,109,5,1201,-4,0,629,21002,0,1,-2,22101,1,-4,-4,21102,1,0,-3,2208,-3,-2,570,1005,570,781,2201,-4,-3,652,21002,0,1,-1,1208,-1,-4,570,1005,570,709,1208,-1,-5,570,1005,570,734,1207,-1,0,570,1005,570,759,1206,-1,774,1001,578,562,684,1,0,576,576,1001,578,566,692,1,0,577,577,21102,1,702,0,1105,1,786,21201,-1,-1,-1,1106,0,676,1001,578,1,578,1008,578,4,570,1006,570,724,1001,578,-4,578,21102,731,1,0,1106,0,786,1106,0,774,1001,578,-1,578,1008,578,-1,570,1006,570,749,1001,578,4,578,21102,756,1,0,1106,0,786,1105,1,774,21202,-1,-11,1,22101,1182,1,1,21102,774,1,0,1106,0,622,21201,-3,1,-3,1106,0,640,109,-5,2106,0,0,109,7,1005,575,802,21002,576,1,-6,20102,1,577,-5,1105,1,814,21101,0,0,-1,21101,0,0,-5,21101,0,0,-6,20208,-6,576,-2,208,-5,577,570,22002,570,-2,-2,21202,-5,51,-3,22201,-6,-3,-3,22101,1447,-3,-3,1202,-3,1,843,1005,0,863,21202,-2,42,-4,22101,46,-4,-4,1206,-2,924,21102,1,1,-1,1105,1,924,1205,-2,873,21101,35,0,-4,1106,0,924,1202,-3,1,878,1008,0,1,570,1006,570,916,1001,374,1,374,2102,1,-3,895,1102,1,2,0,1201,-3,0,902,1001,438,0,438,2202,-6,-5,570,1,570,374,570,1,570,438,438,1001,578,558,922,20101,0,0,-4,1006,575,959,204,-4,22101,1,-6,-6,1208,-6,51,570,1006,570,814,104,10,22101,1,-5,-5,1208,-5,31,570,1006,570,810,104,10,1206,-1,974,99,1206,-1,974,1102,1,1,575,21101,973,0,0,1106,0,786,99,109,-7,2106,0,0,109,6,21102,0,1,-4,21101,0,0,-3,203,-2,22101,1,-3,-3,21208,-2,82,-1,1205,-1,1030,21208,-2,76,-1,1205,-1,1037,21207,-2,48,-1,1205,-1,1124,22107,57,-2,-1,1205,-1,1124,21201,-2,-48,-2,1106,0,1041,21102,1,-4,-2,1106,0,1041,21101,0,-5,-2,21201,-4,1,-4,21207,-4,11,-1,1206,-1,1138,2201,-5,-4,1059,2102,1,-2,0,203,-2,22101,1,-3,-3,21207,-2,48,-1,1205,-1,1107,22107,57,-2,-1,1205,-1,1107,21201,-2,-48,-2,2201,-5,-4,1090,20102,10,0,-1,22201,-2,-1,-2,2201,-5,-4,1103,1202,-2,1,0,1105,1,1060,21208,-2,10,-1,1205,-1,1162,21208,-2,44,-1,1206,-1,1131,1105,1,989,21102,1,439,1,1105,1,1150,21101,477,0,1,1106,0,1150,21102,1,514,1,21101,0,1149,0,1105,1,579,99,21101,1157,0,0,1105,1,579,204,-2,104,10,99,21207,-3,22,-1,1206,-1,1138,2102,1,-5,1176,1202,-4,1,0,109,-6,2106,0,0,14,5,46,1,3,1,36,13,1,1,36,1,9,1,1,1,1,1,32,13,1,7,30,1,3,1,7,1,3,1,1,1,1,1,30,1,3,1,7,1,3,1,1,1,1,1,21,10,3,1,7,1,3,1,1,1,1,1,21,1,7,6,7,1,3,1,1,1,1,1,21,1,7,1,12,1,3,1,1,1,1,1,21,1,7,1,4,13,1,1,1,1,21,1,7,1,4,1,7,1,5,1,1,1,21,1,7,1,4,1,1,13,1,1,21,1,7,1,4,1,1,1,5,1,7,1,21,1,7,1,4,1,1,1,5,1,5,13,7,13,4,1,1,1,5,1,5,1,1,1,9,1,7,1,3,1,12,1,1,1,5,9,9,1,7,1,3,1,12,1,1,1,11,1,11,1,7,1,3,1,12,1,1,1,1,9,1,1,11,1,7,1,3,7,6,1,1,1,1,1,7,1,1,1,11,1,7,1,16,1,1,13,11,1,7,1,16,1,3,1,7,1,13,1,7,1,16,5,7,1,13,1,7,1,28,1,13,1,7,1,28,1,13,1,7,1,28,1,13,1,7,1,28,1,13,9,28,1,50,1,50,1,50,13,22"