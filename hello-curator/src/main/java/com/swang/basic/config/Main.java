package com.swang.basic.config;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.x.async.AsyncCuratorFramework;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) throws Exception {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        String zookeeperConnectionString = "127.0.0.1:2181";
        CuratorFramework client = CuratorFrameworkFactory.newClient(zookeeperConnectionString, retryPolicy);
        client.start();

        AsyncCuratorFramework async = AsyncCuratorFramework.wrap(client);

        String s1 = "/singlesetting1";
        async.create().forPath(s1);
        async.setData().forPath(s1, "hello".getBytes());
        async.getData().forPath(s1).thenAccept(s-> System.out.println("config value is:" + new String(s)));

        System.in.read();
    }
}
