package com.swang.basic.watch;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.x.async.AsyncCuratorFramework;

public class Main {

    public static void main(String[] args) throws Exception {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        String zookeeperConnectionString = "127.0.0.1:2181";
        CuratorFramework client = CuratorFrameworkFactory.newClient(zookeeperConnectionString, retryPolicy);
        client.start();

        AsyncCuratorFramework async = AsyncCuratorFramework.wrap(client);

        String s = "/dev/env";
        client.create().creatingParentsIfNeeded().forPath(s);
        async.watched().getData().forPath(s).event().thenAccept(v->{
            try {
                System.out.println("value of env is: " + new String(client.getData().forPath(s)));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        async.setData().forPath(s,"product".getBytes());

        System.in.read();
    }
}
