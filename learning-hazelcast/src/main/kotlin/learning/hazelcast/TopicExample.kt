package learning.hazelcast

import com.hazelcast.core.Hazelcast
import java.util.*
import java.util.concurrent.TimeUnit

fun main(args: Array<String>) {
    val hz = Hazelcast.newHazelcastInstance()
    val topic = hz.getTopic<String>("broadcast")
    topic.addMessageListener(TopicListener())

    while (true) {
        topic.publish(
                "${Date()} - ${hz.cluster.localMember} says hello"
        )

        TimeUnit.SECONDS.sleep(10)
    }
}
