package learning.uniqueid

import org.apache.commons.codec.digest.DigestUtils
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.*
import java.util.UUID



class SecureRandomUniqueIDGenerator : UniqueIDGenerator {

    override fun name() = "SecureRandom (SHA1PRNG)"

    override fun nextId(): String {
        val randomNum = Integer(SecureRandom.getInstanceStrong().nextInt()).toString()

        val ts = System.currentTimeMillis().toString()
        val rand = UUID.randomUUID().toString()
        return DigestUtils.sha1Hex(randomNum.toByteArray())
    }

    private val HEX_CHARS = "0123456789ABCDEF".toCharArray()

    private fun ByteArray.toHex() : String{
        val result = StringBuffer()

        forEach {
            val octet = it.toInt()
            val firstIndex = (octet and 0xF0).ushr(4)
            val secondIndex = octet and 0x0F
            result.append(HEX_CHARS[firstIndex])
            result.append(HEX_CHARS[secondIndex])
        }

        return result.toString()
    }
}
