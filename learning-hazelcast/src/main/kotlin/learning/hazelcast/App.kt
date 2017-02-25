package learning.hazelcast

import com.hazelcast.core.Hazelcast
import com.hazelcast.core.IMap
import java.util.concurrent.BlockingQueue


fun main(args: Array<String>) {
    val hz = Hazelcast.newHazelcastInstance()
    val queue = hz.getQueue<String>("q")
    queue.add("element")

    println("q.size: ${queue.size}")

    val capitals: IMap<String, String> = hz.getMap("capitals")
    capitals.apply {
        put("GB", "London")
        put("FR", "Paris")
        put("US", "Washington DC")
        put("AU", "Canberra")
    }

    println("Known capital cities: ${capitals.size}")
    println("Capital city of GB: ${capitals["GB"]}")

    val arrivals: BlockingQueue<String> = hz.getQueue("arrivals")

    arrivals.put("New arrival from: ${Thread.currentThread().id}")

    while (true) {
        val arrival = arrivals.take()

        println("New arrival from: $arrival")
    }
}
