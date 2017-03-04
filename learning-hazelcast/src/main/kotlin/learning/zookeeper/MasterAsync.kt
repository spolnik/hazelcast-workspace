package learning.zookeeper

import org.apache.zookeeper.*
import org.slf4j.LoggerFactory
import java.util.*
import java.util.concurrent.TimeUnit

class MasterAsync(val hostPort: String) : Watcher {

    private val LOG = LoggerFactory.getLogger(this.javaClass)
    private lateinit var zk: ZooKeeper

    private val serverId = Integer.toHexString(Random().nextInt())
    var isLeader = false


    fun startZK() {
        zk = ZooKeeper(hostPort, 15000, this)
    }

    fun masterCheckCallback(rc: Int) {
        when (KeeperException.Code.get(rc)) {
            KeeperException.Code.CONNECTIONLOSS -> {
                checkMaster()
            }
            KeeperException.Code.NONODE -> {
                runForMaster()
            }
            else -> {
                return
            }
        }
    }


    fun checkMaster() {
        zk.getData("/masterAsync", false, {
            rc, path, ctx, data, stat ->
            masterCheckCallback(rc)
        }, null)
    }

    fun masterCreateCallback(rc: Int) {

        when (KeeperException.Code.get(rc)) {
            KeeperException.Code.CONNECTIONLOSS -> {
                checkMaster()
                return
            }
            KeeperException.Code.OK -> {
                isLeader = true
            }
            else -> {
                isLeader = false
            }
        }
        println("I'm ${if (isLeader) "" else "not"} the leader")
    }

    fun runForMaster() {
        zk.create(
                "/master",
                serverId.toByteArray(),
                ZooDefs.Ids.OPEN_ACL_UNSAFE,
                CreateMode.EPHEMERAL,
                { rc, path, ctx, name -> masterCreateCallback(rc) },
                null
        )
    }

    fun stopZK() {
        zk.close()
    }

    override fun process(event: WatchedEvent) {
        println(event)
    }

    fun bootstrap() {
        createParent("/workers", ByteArray(0))
        createParent("/assign", ByteArray(0))
        createParent("/tasks", ByteArray(0))
        createParent("/status", ByteArray(0))
    }

    fun createParent(path: String, data: ByteArray) {
        zk.create(path,
                data,
                ZooDefs.Ids.OPEN_ACL_UNSAFE,
                CreateMode.PERSISTENT,
                { rc, path, ctx, name -> createParentCallback(rc, path, ctx) },
                data
        )
    }

    fun createParentCallback(rc: Int, path: String, ctx: Any) {
        when (KeeperException.Code.get(rc)) {
            KeeperException.Code.CONNECTIONLOSS -> createParent(path, ctx as ByteArray)
            KeeperException.Code.OK -> LOG.info("Parent created")

            KeeperException.Code.NODEEXISTS -> LOG.warn("Parent already registered: $path")

            else ->
                LOG.error("Something went wrong: ",
                        KeeperException.create(KeeperException.Code.get(rc), path))
        }
    }
}

fun main(args: Array<String>) {
    val master = MasterAsync("127.0.0.1:2181")

    master.startZK()
    master.bootstrap()
    master.runForMaster()

    if (master.isLeader) {
        println("I'm the leader")
        TimeUnit.SECONDS.sleep(60)
    } else {
        println("Someone else is the leader")
    }

    master.stopZK()
}
