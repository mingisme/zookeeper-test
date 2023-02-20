package org.swang.watcher;

import org.apache.zookeeper.*;

import java.io.IOException;

import static org.apache.zookeeper.ZooDefs.Ids.OPEN_ACL_UNSAFE;

public class DataWatcher implements Watcher {

    public static final String PATH = "/MyConfig";
    private ZooKeeper zooKeeper;

    public DataWatcher() throws IOException, InterruptedException, KeeperException {
        zooKeeper = new ZooKeeper("localhost:2181", 2000, this);
        if(zooKeeper!=null){
            if(zooKeeper.exists(PATH,false)==null){
                zooKeeper.create(PATH,"".getBytes(), OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
        }
    }

    private void printData() throws InterruptedException, KeeperException {
        byte[] data = zooKeeper.getData(PATH, this, null);
        System.out.println("Current data @ ZK path: " + new String(data) + " " +PATH);
    }

    public void process(WatchedEvent watchedEvent) {
        System.out.println("Event: " + watchedEvent);
        if(watchedEvent.getType() == Event.EventType.NodeDataChanged){
            try {
                printData();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (KeeperException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
            DataWatcher dataWatcher = new DataWatcher();
            dataWatcher.printData();
            System.in.read();
    }
}
