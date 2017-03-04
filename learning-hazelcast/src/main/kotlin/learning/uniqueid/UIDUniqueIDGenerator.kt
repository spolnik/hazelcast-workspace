package learning.uniqueid

import java.rmi.server.UID

class UIDUniqueIDGenerator : UniqueIDGenerator {

    override fun nextId() = UID().toString()

    override fun name() = "UID"
}
