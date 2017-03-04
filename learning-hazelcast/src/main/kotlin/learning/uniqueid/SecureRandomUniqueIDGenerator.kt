package learning.uniqueid

import org.apache.commons.codec.digest.DigestUtils
import java.security.SecureRandom

class SecureRandomUniqueIDGenerator : UniqueIDGenerator {

    override fun name() = "SecureRandom (SHA1PRNG)"

    override fun nextId(): String {
        val randomNum = Integer(SecureRandom.getInstanceStrong().nextInt()).toString()

        return DigestUtils.sha1Hex(randomNum.toByteArray())
    }
}
