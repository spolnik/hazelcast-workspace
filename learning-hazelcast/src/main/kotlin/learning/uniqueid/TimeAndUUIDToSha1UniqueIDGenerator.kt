package learning.uniqueid

import org.apache.commons.codec.digest.DigestUtils
import java.util.*

class TimeAndUUIDToSha1UniqueIDGenerator : UniqueIDGenerator {

    override fun nextId(): String {
        val ts = System.currentTimeMillis().toString()
        val rand = UUID.randomUUID().toString()
        return DigestUtils.sha1Hex("$ts$rand")
    }

    override fun name(): String {
        return "CurrentTime+UUID->SHA-1"
    }
}
