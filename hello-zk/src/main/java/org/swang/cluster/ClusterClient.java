package org.swang.cluster;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.lang.management.ManagementFactory;

import static org.apache.zookeeper.ZooDefs.Ids.OPEN_ACL_UNSAFE;

public class ClusterClient {

    public ClusterClient() throws IOException, InterruptedException, KeeperException {

        String name = ManagementFactory.getRuntimeMXBean().getName();
        String[] split = name.split("@");
        String pid = split[0];

        ZooKeeper zk = new ZooKeeper("localhost:2181", 2000, null);

        if (zk != null) {
            zk.create("/members/" + pid, pid.getBytes(), OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        }
    }

    public void run() throws IOException {
        System.in.read();
    }

    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
        ClusterClient clusterClient = new ClusterClient();
        clusterClient.run();
    }
}
