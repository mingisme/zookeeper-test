package com.swang.example.queue;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.queue.DistributedQueue;
import org.apache.curator.framework.recipes.queue.QueueBuilder;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class MyQueueProducer {

    private int queueSize;
    private CuratorFramework client;
    private String path;
    private ExecutorService executorService;
    private List<DistributedQueue> queues = new ArrayList<>();

    public MyQueueProducer(String connectString, int queueSize, String path, int concurrentSize) {
        for (int i = 0; i < concurrentSize; i++) {
            CuratorFramework client = CuratorFrameworkFactory.newClient(connectString, new ExponentialBackoffRetry(1000, 3));
            client.start();
            DistributedQueue<String> queue = QueueBuilder.builder(client, null, new MyQueueSerializer(), path).maxItems(queueSize).buildQueue();
            queues.add(queue);
        }
        executorService = Executors.newFixedThreadPool(concurrentSize);
    }

    public void start() throws Exception {

        for(DistributedQueue queue :queues){
            try {
                queue.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < queues.size(); j++) {
                int loop = i;
                int queueNo = j;
                executorService.submit(() -> {
                    try {
                        queues.get(queueNo).put("message-" + loop + "-queue-" + queueNo);
                        System.out.println("put message: " +"message-" + loop + "-queue-" + queueNo);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        }
    }

}
