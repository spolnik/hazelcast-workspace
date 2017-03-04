package learning.uniqueid

import junitparams.JUnitParamsRunner
import junitparams.Parameters
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import java.lang.System.nanoTime

@RunWith(JUnitParamsRunner::class)
class UniqueIDGeneratorSpec {

    @Test
    @Parameters(method = "generators")
    fun simple_check_of_two_unique_ids(idGenerator: UniqueIDGenerator, howMany: Int) {
        assertThat(idGenerator.nextId()).isNotEqualTo(
                idGenerator.nextId()
        )
    }

    @Test
    @Parameters(method = "generators")
    fun creates_many_unique_ids(idGenerator: UniqueIDGenerator, howMany: Int) {
        val set = hashSetOf(idGenerator.nextId())

        measureTime(idGenerator, howMany) {
            (1..howMany - 1).forEach {
                assertThat(set.add(idGenerator.nextId())).isTrue()
            }
        }

        assertThat(set).hasSize(howMany)
    }

    private fun generators() = arrayOf(
            arrayOf(UIDUniqueIDGenerator(), 1000000),
            arrayOf(AtomicIntegerUniqueIDGenerator(), 1000000),
            arrayOf(UUIDUniqueIDGenerator(), 1000000),
            // arrayOf(UUIDUniqueIDGenerator(), 10000000), -> it passes, but takes around 1 min
            arrayOf(SecureRandomUniqueIDGenerator(), 10000),
            arrayOf(TimeAndUUIDToSha1UniqueIDGenerator(), 1000000)
            // arrayOf(TimeAndUUIDToSha1UniqueIDGenerator(), 10000000) -> it passes, but takes around 2 min
    )

    private fun measureTime(idGenerator: UniqueIDGenerator, howMany: Int, block: () -> Unit) {
        val startTime = nanoTime()
        block()
        val endTime = nanoTime()

        val durationInMs = (endTime - startTime) / 1000000
        println("[$howMany] - ${idGenerator.name()} performance: $durationInMs ms")
    }
}
