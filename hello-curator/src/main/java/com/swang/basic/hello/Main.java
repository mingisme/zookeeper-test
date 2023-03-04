package com.swang.basic.hello;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        String zookeeperConnectionString = "127.0.0.1:2181";
        CuratorFramework client = CuratorFrameworkFactory.newClient(zookeeperConnectionString, retryPolicy);
        client.start();

        if (client.checkExists().forPath("/hello-curator") == null) {
            String value = client.create().forPath("/hello-curator", "hello curator".getBytes());
            System.out.println(value);
        }else{
            byte[] bytes = client.getData().forPath("/hello-curator");
            System.out.println("value of /hello-curator: " + new String(bytes));
        }

        List<String> children = client.getChildren().forPath("/");
        System.out.println(children);
    }
}