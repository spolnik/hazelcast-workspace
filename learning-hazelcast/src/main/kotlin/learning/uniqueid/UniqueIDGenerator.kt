package learning.uniqueid

interface UniqueIDGenerator {
    fun nextId(): String
    fun name(): String
}
