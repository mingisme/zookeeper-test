package org.swang.recips.election;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    public static void main(String[] args) throws IOException {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 10; i++) {
            executorService.submit(() -> {
                long id = Thread.currentThread().getId();

                ZooKeeper zk = null;
                String rootNodeName = "/ld";
                try {
                    zk = new ZooKeeper("localhost:2181", 3000, null);
                    if (zk.exists(rootNodeName, false) == null) {
                        zk.create(rootNodeName,  new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                } catch (KeeperException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
                LeaderElectionSupport leaderElectionSupport = getLeaderElectionSupport(rootNodeName, zk, id);
                leaderElectionSupport.start();
            });
        }


    }

    private static LeaderElectionSupport getLeaderElectionSupport(String rootNodeName, ZooKeeper zk, long id) {
        LeaderElectionSupport leaderElectionSupport = new LeaderElectionSupport();
        leaderElectionSupport.setRootNodeName(rootNodeName);
        leaderElectionSupport.setZooKeeper(zk);
        leaderElectionSupport.setHostName("host-" + id);
        leaderElectionSupport.addListener(eventType -> {
            if (eventType == LeaderElectionSupport.EventType.ELECTED_COMPLETE) {
                System.out.println("ID: " + id + " I am the leader");
                try {
                    Thread.sleep(new Random().nextInt(3000));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                leaderElectionSupport.stop();
            } else if (eventType == LeaderElectionSupport.EventType.READY_COMPLETE) {
                System.out.println("ID: " + id + " I am the follower");
            }
        });
        return leaderElectionSupport;
    }
}
