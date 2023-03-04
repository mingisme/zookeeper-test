package com.swang.basic.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.x.async.AsyncCuratorFramework;
import org.apache.curator.x.async.modeled.JacksonModelSerializer;
import org.apache.curator.x.async.modeled.ModelSpec;
import org.apache.curator.x.async.modeled.ModeledFramework;
import org.apache.curator.x.async.modeled.ZPath;

public class Main {
    public static void main(String[] args) throws Exception {

        ModelSpec<HostConfig> mySpec = ModelSpec.builder(
                        ZPath.parseWithIds("/config/dev"),
                        JacksonModelSerializer.build(HostConfig.class))
                .build();

        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        String zookeeperConnectionString = "127.0.0.1:2181";
        CuratorFramework client = CuratorFrameworkFactory.newClient(zookeeperConnectionString, retryPolicy);
        client.start();
        AsyncCuratorFramework async = AsyncCuratorFramework.wrap(client);

        ModeledFramework<HostConfig> modeledClient
                = ModeledFramework.wrap(async, mySpec);
        try {
            client.create().idempotent().creatingParentsIfNeeded().forPath("/config/dev");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            //e.printStackTrace();
        }
        modeledClient.set(new HostConfig("host-name", 8080));

        modeledClient.read().whenComplete((v, e) -> {
            if (e != null) {
                e.printStackTrace();
            } else {
                System.out.println(v);
                ObjectMapper objectMapper = new ObjectMapper();
                try {
                    System.out.println("config: " + objectMapper.writeValueAsString(v));
                } catch (JsonProcessingException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        System.in.read();


    }
}
