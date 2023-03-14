/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.swang.example.readwritelocking;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.leader.CancelLeadershipException;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;

import java.util.concurrent.TimeUnit;

public class ExampleClientThatLocks {
    private final DistributedReadWriteLock lock;
    private final FakeLimitedResource resource;
    private final String clientName;

    public ExampleClientThatLocks(CuratorFramework client, String lockPath, FakeLimitedResource resource, String clientName) {
        this.resource = resource;
        this.clientName = clientName;
        lock = new DistributedReadWriteLock(client, lockPath);
    }

    public void doRead(long time, TimeUnit unit) throws Exception {
        if (!lock.acquireReadLock(time, unit)) {
            throw new IllegalStateException(clientName + " could not acquire the read lock");
        }
        try {
            System.out.println(clientName + " has the read lock");
            resource.use(true);
        } finally {
            System.out.println(clientName + " releasing the read lock");
            lock.releaseReadLock(); // always release the lock in a finally block
        }
    }

    public void doWrite(long time, TimeUnit unit) throws Exception {
        if (!lock.acquireWriteLock(time, unit)) {
            throw new IllegalStateException(clientName + " could not acquire the write lock");
        }
        try {
            System.out.println(clientName + " has the write lock");
            resource.use(false);
        } finally {
            System.out.println(clientName + " releasing the write lock");
            lock.releaseWriteLock(); // always release the lock in a finally block
        }
    }

}
