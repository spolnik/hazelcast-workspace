package learning.uniqueid

import java.util.*

class UUIDUniqueIDGenerator : UniqueIDGenerator {

    override fun name() = "UUID"

    override fun nextId() = UUID.randomUUID().toString()
}
