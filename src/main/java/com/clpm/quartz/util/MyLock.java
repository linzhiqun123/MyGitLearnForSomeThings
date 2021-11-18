package com.clpm.quartz.util;

import org.apache.zookeeper.KeeperException;

public interface MyLock {

    public boolean tryLock() throws KeeperException, InterruptedException;

    public void Lock() throws KeeperException, InterruptedException;

    public void releaseLock();
}
