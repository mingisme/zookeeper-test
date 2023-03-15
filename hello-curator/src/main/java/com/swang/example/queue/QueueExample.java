package com.swang.example.queue;

import org.apache.curator.test.TestingServer;

import java.util.concurrent.TimeUnit;

public class QueueExample {

    public static void main(String[] args) throws Exception {
        final TestingServer server = new TestingServer();
        String path = "/examples/test_queue";
        int queueSize = 10;
        MyQueueConsumer myQueueConsumer = new MyQueueConsumer(server.getConnectString(), queueSize, path, 1);
        myQueueConsumer.start();

        MyQueueProducer myQueueProducer = new MyQueueProducer(server.getConnectString(), queueSize, path, 10);
        myQueueProducer.start();
    }
}
