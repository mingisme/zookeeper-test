package org.swang.cluster;

import org.apache.zookeeper.*;

import java.io.IOException;
import java.util.List;

import static org.apache.zookeeper.ZooDefs.Ids.OPEN_ACL_UNSAFE;

public class ClusterMonitor {

    public static final String PATH = "/members";

    public ClusterMonitor() throws IOException, InterruptedException, KeeperException {


        Watcher connectionWatcher = new Watcher() {
            public void process(WatchedEvent watchedEvent) {
                if (watchedEvent.getType() == Event.EventType.None && watchedEvent.getState() == Event.KeeperState.SyncConnected) {
                    System.out.println("Event received: " + watchedEvent);
                }
            }
        };

        final ZooKeeper zk = new ZooKeeper("localhost:2181", 2000, connectionWatcher);
        if (zk != null && zk.exists(PATH, false) == null) {
            zk.create(PATH, "members".getBytes(), OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }

        Watcher childrenWatcher = new Watcher() {
            public void process(WatchedEvent watchedEvent) {
                if (watchedEvent.getType() == Event.EventType.NodeChildrenChanged) {
                    try {
                        List<String> children = zk.getChildren(PATH, this);
                        System.out.println("Members: " + children);
                    } catch (KeeperException e) {
                        throw new RuntimeException(e);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        };

        List<String> children = zk.getChildren(PATH, childrenWatcher);
        System.out.println("Members: " + children);
    }


    public void run() throws IOException {
        System.in.read();
    }

    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
        ClusterMonitor clusterMonitor = new ClusterMonitor();
        clusterMonitor.run();
    }
}
