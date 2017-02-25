package learning.hazelcast

import com.hazelcast.core.Message
import com.hazelcast.core.MessageListener

class TopicListener : MessageListener<String> {
    override fun onMessage(message: Message<String>) {
        println("Received: ${message.messageObject}")
    }
}
