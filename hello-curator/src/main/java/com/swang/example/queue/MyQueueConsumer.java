package com.swang.example.queue;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.leader.CancelLeadershipException;
import org.apache.curator.framework.recipes.queue.DistributedQueue;
import org.apache.curator.framework.recipes.queue.QueueBuilder;
import org.apache.curator.framework.recipes.queue.QueueConsumer;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MyQueueConsumer {
    private int queueSize;
    private CuratorFramework client;
    private String path;
    private ExecutorService executorService;
    private List<DistributedQueue> queues = new ArrayList<>();

    public MyQueueConsumer(String connectString, int queueSize, String path, int concurrentSize) {

        QueueConsumer<String> consumer = new QueueConsumer<String>() {
            @Override
            public void stateChanged(CuratorFramework curatorFramework, ConnectionState newState) {
                if (client.getConnectionStateErrorPolicy().isErrorState(newState)) {
                    throw new CancelLeadershipException();
                }
            }

            @Override
            public void consumeMessage(String message) throws Exception {
                System.out.println("Received message: " + message);
                TimeUnit.SECONDS.sleep(2);
            }

        };

        for (int i = 0; i < concurrentSize; i++) {
            CuratorFramework client = CuratorFrameworkFactory.newClient(connectString, new ExponentialBackoffRetry(1000, 3));
            client.start();
            DistributedQueue<String> queue = QueueBuilder.builder(client, consumer, new MyQueueSerializer(), path).executor(Executors.newFixedThreadPool(1)).maxItems(queueSize).buildQueue();
            queues.add(queue);
        }

        executorService = Executors.newFixedThreadPool(concurrentSize);
    }

    public void start() throws Exception {
        for(DistributedQueue queue : queues){
//            executorService.submit(()->{
//                try {
                    queue.start();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            });
        }
    }
}
