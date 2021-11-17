package com.clpm.quartz.Util;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.LockSupport;

//自定义一个分布式锁
@Slf4j
public class MyDistributeLock {

    private ZooKeeper zkClient;
    ThreadLocal<String> preThreadLocal = new ThreadLocal<>();
    ThreadLocal<String> curThreadLocal = new ThreadLocal<>();
    public  MyDistributeLock() {
        String connectString = "127.0.0.1:2181,127.0.0.1:2182,127.0.0.1:2183";
        int sessionTimeout = 2000;
        try {
            zkClient = new ZooKeeper(connectString, sessionTimeout, new Watcher() {
                @Override
                public void process(WatchedEvent watchedEvent) {

                    List<String> zkClientChildren = null;
                    try {
                        zkClientChildren = zkClient.getChildren("/MyLock", true);
                    } catch (KeeperException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

//                    for (String zkClientChild : zkClientChildren) {
//                        System.out.println(zkClientChild);
//                    }
                }
            });

            Stat exists = zkClient.exists("/MyLock", true);

            if(exists==null){
                zkClient.create("/MyLock",null,ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    public synchronized boolean tryLock()  {
        //临时的节点
        String   currentNode = null;
        try {
            currentNode = zkClient.create("/MyLock" + "/seq_", null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        curThreadLocal.set(currentNode);

        List<String> zkClientChildren = null;
        try {
            zkClientChildren = zkClient.getChildren("/MyLock", false);
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if(zkClientChildren.size()==1){
//            log.info(Thread.currentThread().getName()+"获取分布式锁成功,创建的节点为"+currentNode);
            return true;
        }

        Collections.sort(zkClientChildren);

        String substring = currentNode.substring("/MyLock/".length());

        int indexOf = zkClientChildren.indexOf(substring);

        if(indexOf==0){
//            log.info(Thread.currentThread().getName()+"获取分布式锁成功,创建的节点为"+currentNode);
            return true;
        }else {
            //上一个的节点
            String preNode = zkClientChildren.get(indexOf - 1);
            preThreadLocal.set(preNode);
            return false;
        }
    }

    public  void Lock()  {
        Thread currentThread = Thread.currentThread();
        if (!tryLock()) {
            //
            try{
                log.info(currentThread+"获取锁失败,进入阻塞"+curThreadLocal.get()+"监听上一个节点"+preThreadLocal.get());
                zkClient.getData("/MyLock/"+preThreadLocal.get(),watchedEvent -> {
                    //释放本线程的锁
                    log.info(currentThread.getName()+"进入解锁,当前线程{}",Thread.currentThread().getName());
                    LockSupport.unpark(currentThread);
                },null);
                //线程挂起
                LockSupport.park();
                log.info(Thread.currentThread().getName()+"阻塞线程获取分布式锁成功,创建的节点为{}",curThreadLocal.get());
            }catch (Exception e) {
                e.printStackTrace();
            }
                 }else {
            log.info(Thread.currentThread().getName()+"获取分布式锁成功,创建的节点为{}",curThreadLocal.get());
        }
    }

    public void releaseLock() {
        try {
            zkClient.delete(curThreadLocal.get(),-1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }finally {
            log.info(Thread.currentThread().getName()+"释放分布式锁成功,节点为{}",curThreadLocal.get());
            preThreadLocal.remove();
            curThreadLocal.remove();
        }
    }
}
