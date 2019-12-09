import opcode.AmplifiersLoop
import opcode.RealtimeOpcodeComputer
import org.junit.Assert
import org.junit.Test

class OpcodeComputerTests {

    @Test
    fun `amplifiers loop test case 1`() {
        val software = "3,26,1001,26,-4,26,3,27,1002,27,2,27,1,27,26,27,4,27,1001,28,-1,28,1005,28,6,99,0,0,5"
            .split(',')
            .map { it.toLong() }
        val amplifiersLoop = AmplifiersLoop(software, listOf(9, 8, 7, 6, 5))

        val output = amplifiersLoop.run()

        Assert.assertEquals(listOf(139_629_729L), output)
    }

    @Test
    fun `amplifiers loop test case 2`() {
        val software =
            "3,52,1001,52,-5,52,3,53,1,52,56,54,1007,54,5,55,1005,55,26,1001,54,-5,54,1105,1,12,1,53,54,53,1008,54,0,55,1001,55,1,55,2,53,55,53,4,53,1001,56,-1,56,1005,56,6,99,0,0,0,0,10"
                .split(',')
                .map { it.toLong() }
        val amplifiersLoop = AmplifiersLoop(software, listOf(9, 7, 8, 5, 6))

        val output = amplifiersLoop.run()

        Assert.assertEquals(listOf(18_216L), output)
    }

    @Test
    fun `quine test`() {
        val software =
            "109,1,204,-1,1001,100,1,100,1008,100,16,101,1006,101,0,99"
                .split(',')
                .map { it.toLong() }
        val computer = RealtimeOpcodeComputer(software)

        computer.start()

        Assert.assertEquals(software, computer.outputStream.toList())
    }

    @Test
    fun `16 digits output test`() {
        val software =
            "1102,34915192,34915192,7,4,7,99,0"
                .split(',')
                .map { it.toLong() }
        val computer = RealtimeOpcodeComputer(software)

        computer.start()

        Assert.assertEquals(16, computer.outputStream.first().toString().length)
    }
}