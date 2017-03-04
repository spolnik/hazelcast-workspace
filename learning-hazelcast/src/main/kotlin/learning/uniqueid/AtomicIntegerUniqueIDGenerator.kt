package learning.uniqueid

import java.util.concurrent.atomic.AtomicInteger

class AtomicIntegerUniqueIDGenerator : UniqueIDGenerator {

    override fun nextId() = idCounter.andIncrement.toString()

    override fun name() = "Atomic"

    companion object {
        private val idCounter = AtomicInteger()
    }
}
