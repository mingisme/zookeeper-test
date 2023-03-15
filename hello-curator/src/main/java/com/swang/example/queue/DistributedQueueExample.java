package com.swang.example.queue;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.queue.DistributedQueue;
import org.apache.curator.framework.recipes.queue.QueueBuilder;
import org.apache.curator.framework.recipes.queue.QueueConsumer;
import org.apache.curator.framework.recipes.queue.QueueSerializer;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.test.TestingServer;

public class DistributedQueueExample {

    public static void main(String[] args) throws Exception {

        final TestingServer server = new TestingServer();
        CuratorFramework client = CuratorFrameworkFactory.newClient(server.getConnectString(), new ExponentialBackoffRetry(1000, 3));
        client.start();

        QueueConsumer<String> consumer = new QueueConsumer<String>() {
            @Override
            public void stateChanged(CuratorFramework curatorFramework, ConnectionState connectionState) {

            }

            @Override
            public void consumeMessage(String message) throws Exception {
                System.out.println("Received message: " + message);
            }

        };

        DistributedQueue<String> queue = QueueBuilder.builder(client, consumer, new QueueSerializer<String>() {
            @Override
            public byte[] serialize(String item) {
                return item.getBytes();
            }
            @Override
            public String deserialize(byte[] bytes) {
                return new String(bytes);
            }
        }, "/example/queue").buildQueue();

        queue.start();

        queue.put("Hello");
        queue.put("World");

        Thread.sleep(1000);

        queue.close();
        client.close();
    }

}
