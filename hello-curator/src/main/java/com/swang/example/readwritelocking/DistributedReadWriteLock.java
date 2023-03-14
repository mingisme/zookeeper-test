package com.swang.example.readwritelocking;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.framework.recipes.locks.InterProcessReadWriteLock;

import java.util.concurrent.TimeUnit;

public class DistributedReadWriteLock {
    private final CuratorFramework client;
    private final InterProcessReadWriteLock lock;
    private final InterProcessMutex readLock;
    private final InterProcessMutex writeLock;

    public DistributedReadWriteLock(CuratorFramework client, String lockPath) {
        this.client = client;
        lock = new InterProcessReadWriteLock(client, lockPath);
        readLock = lock.readLock();
        writeLock = lock.writeLock();
    }

    public boolean acquireReadLock(long time, TimeUnit unit) throws Exception {
        return readLock.acquire(time, unit);
    }

    public void releaseReadLock() throws Exception {
        readLock.release();
    }

    public boolean acquireWriteLock(long time, TimeUnit unit) throws Exception {
        return writeLock.acquire(time, unit);
    }

    public void releaseWriteLock() throws Exception {
        writeLock.release();
    }
}
