package org.swang.watcher;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class DataUpdater{

   static String hostPort = "localhost:2181";
   static String zooDataPath = "/MyConfig";

   ZooKeeper zk;

    public DataUpdater() {
        try {
            zk = new ZooKeeper(hostPort, 2000, null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void run() throws InterruptedException, KeeperException {
        UUID uuid = UUID.randomUUID();
        byte[] bytes = uuid.toString().getBytes();
        while(true){
            zk.setData(zooDataPath, bytes, -1);
            TimeUnit.SECONDS.sleep(5);
            System.out.println("sleep");
        }
    }


    public static void main(String[] args) throws InterruptedException, KeeperException {
        DataUpdater dataUpdater = new DataUpdater();
        dataUpdater.run();
    }
}
