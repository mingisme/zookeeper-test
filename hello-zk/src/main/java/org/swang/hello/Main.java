package org.swang.hello;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {

        String hostPort = "localhost:2181";
        String zpath = "/";
        List<String> zooChildren = new ArrayList<String>();
        ZooKeeper zk = new ZooKeeper(hostPort, 2000, null);
        if (zk != null) {
            zooChildren = zk.getChildren(zpath, false);
            System.out.println("Zonodes of '/':");

            for(String child : zooChildren){
                System.out.println(child);
            }
        }


    }
}