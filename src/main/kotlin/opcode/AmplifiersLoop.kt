package opcode

class AmplifiersLoop(private val controllerSoftware: List<Long>, private val phaseSettings: List<Int>) {
    private val amplifiers = List(phaseSettings.size) { index ->
        RealtimeOpcodeComputer(controllerSoftware).also { it.name = "computer N°$index" }
    }

    private val inputStream = mutableListOf<Long>()

    init {
        wireInputOutputLoop()
    }

    private fun wireInputOutputLoop() {
        var previousAmplifierOutput = inputStream
        amplifiers.forEachIndexed { index, amplifier ->
            previousAmplifierOutput.add(phaseSettings[index].toLong())
            amplifier.inputStream = previousAmplifierOutput
            previousAmplifierOutput = amplifier.outputStream
        }
        amplifiers.last().outputStream = inputStream
    }

    fun run(): List<Long> {
        // start signal
        inputStream.add(0L)

        var haltedComputersCount = 0

        amplifiers.forEach { amplifier ->
            Thread {
                amplifier.start()
                synchronized(haltedComputersCount) {
                    haltedComputersCount++
                }
            }.start()
        }

        while (haltedComputersCount < amplifiers.size) {
            // wait
            Thread.sleep(10)
        }

        return amplifiers.last().outputStream.toList()
    }
}
