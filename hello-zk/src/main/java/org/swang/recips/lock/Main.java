package org.swang.recips.lock;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {

        ExecutorService executorService = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 10; i++) {

            executorService.submit(() -> {
                CountDownLatch cdl = new CountDownLatch(1);

                long id = Thread.currentThread().getId();
                ZooKeeper zooKeeper = null;
                try {
                    zooKeeper = new ZooKeeper("localhost:2181", 3000, null);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                WriteLock writeLock = new WriteLock(zooKeeper, "/dl", null, new LockListener() {
                    @Override
                    public void lockAcquired() {
                        System.out.println("lock acquired for: " + id);
                        cdl.countDown();
                    }

                    @Override
                    public void lockReleased() {
                        System.out.println("lock released for: " + id);
                    }
                });


                try {
                    boolean lock = writeLock.lock();
                    if (!lock) {
                        cdl.await();
                    }
                } catch (KeeperException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }

                System.out.println("do my job for: " + id);
                try {
                    Thread.sleep(new Random().nextInt(3000));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                writeLock.unlock();
            });
        }
    }
}
