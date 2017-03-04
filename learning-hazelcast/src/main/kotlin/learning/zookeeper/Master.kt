package learning.zookeeper

import org.apache.zookeeper.*
import org.apache.zookeeper.data.Stat
import java.util.*
import java.util.concurrent.TimeUnit

class Master(val hostPort: String) : Watcher {

    private lateinit var zk: ZooKeeper

    private val serverId = Integer.toHexString(Random().nextInt())
    var isLeader = false

    fun startZK() {
        zk = ZooKeeper(hostPort, 15000, this)
    }

    fun checkMaster(): Boolean {
        while (true) {
            try {
                val stat = Stat()
                val data = zk.getData("/master", false, stat)
                isLeader = String(data) == serverId
                return true
            } catch (e: KeeperException.NoNodeException) {
                // no master, so try create again
                return false
            } catch (e: KeeperException.ConnectionLossException) {
                TimeUnit.MILLISECONDS.sleep(10)
            }
        }
    }

    fun runForMaster() {
        while(true) {
            try {
                zk.create(
                        "/master",
                        serverId.toByteArray(),
                        ZooDefs.Ids.OPEN_ACL_UNSAFE,
                        CreateMode.EPHEMERAL
                )

                isLeader = true
                break
            } catch(e: KeeperException.NodeExistsException) {
                isLeader = false
                break
            } catch (e: KeeperException.ConnectionLossException) {
                TimeUnit.MILLISECONDS.sleep(10)
            }

            if (checkMaster())
                break
        }
    }

    fun stopZK() {
        zk.close()
    }

    override fun process(event: WatchedEvent) {
        println(event)
    }
}

fun main(args: Array<String>) {
    val master = Master("127.0.0.1:2181")

    master.startZK()
    master.runForMaster()

    if (master.isLeader) {
        println("I'm the leader")
        TimeUnit.SECONDS.sleep(60)
    } else {
        println("Someone else is the leader")
    }

    master.stopZK()
}
