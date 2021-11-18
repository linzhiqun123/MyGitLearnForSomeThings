package com.clpm.quartz.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.LockSupport;

//通过监听和CountDownLatch方式实现分布式锁
@Slf4j
public class ZookeeperDistributedLock {

    private ZooKeeper zkClient;
    ThreadLocal<String> threadLocal = new ThreadLocal<>();
    ThreadLocal<String> curThreadLocal = new ThreadLocal<>();


    public ZookeeperDistributedLock() {
        String connectString = "127.0.0.1:2181,127.0.0.1:2182,127.0.0.1:2183";
        int sessionTimeout = 2000;
        try {
            zkClient = new ZooKeeper(connectString, sessionTimeout, new Watcher() {
                @Override
                public void process(WatchedEvent watchedEvent) {

                    try {
                        List<String> myDistributedLock = zkClient.getChildren("/MyDistributedLock", true);
//                       log.info("节点为"+myDistributedLock.stream().collect(Collectors.joining(",")));
                    } catch (KeeperException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(zkClient==null){
             log.error("zkClient创建失败");
        }

        //创建本类下的节点
        try{
            Stat exists = zkClient.exists("/MyDistributedLock", false);
            if(exists==null){
                //持久的节点
                zkClient.create("/MyDistributedLock", null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    //锁方法
    public  synchronized   boolean  TryLock() throws KeeperException, InterruptedException {

        //创建有编号的临时节点  --Bug原因,定义为了全局变量,导致currentNode在多个线程时被改写,应该只定义为局部变量,用ThreadLocal进行保存
       String   currentNode = zkClient.create("/MyDistributedLock" + "/seq_", null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);

       curThreadLocal.set(currentNode);

        List<String> zkClientChildren = zkClient.getChildren("/MyDistributedLock", false);


        if(zkClientChildren.size()==1)
            return true;

        Collections.sort(zkClientChildren);
        //判断是否是顺序第一节点
        String substring = currentNode.substring("/MyDistributedLock/".length());
        int indexOf = zkClientChildren.indexOf(substring);
        if(indexOf==0){
            return true;
        }else {
            //线程变量保存需要监听的上一个节点的名字
            threadLocal.set(zkClientChildren.get(indexOf-1));
            log.info(Thread.currentThread().getName()+"创建节点 进入阻塞"+curThreadLocal.get());
            return false;
        }
    }


    //锁住 返回是否能正常获取锁
    public void Lock()  {
        //监听上一个节点是否正常删除
        Thread currentThread = Thread.currentThread();
        try{

            if(!TryLock()){
                log.info(currentThread+"获取锁失败,进入阻塞"+curThreadLocal.get()+"监听上一个节点"+threadLocal.get());
                zkClient.getData("/MyDistributedLock/"+threadLocal.get(), watchedEvent ->{
                    log.info("阻塞被唤醒");
                    LockSupport.unpark(currentThread);
                } , null);
                LockSupport.park();
                log.info(currentThread+"成功获取分布式锁");
//            countDownLatch=new CountDownLatch(1);
//            if(zkClient.exists(threadLocal.get(), new Watcher() {
//                @Override
//                public void process(WatchedEvent watchedEvent) {
//                    //打印下它的监听类型
//                    System.out.println(watchedEvent.getType());
//                    //释放锁 进入下一步的逻辑操作
//                    countDownLatch.countDown();
//                }
//            })!=null){
//                 //阻塞等待
//                countDownLatch.await();
//            }
            }else{
                log.info(currentThread+"成功获取分布式锁");
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //释放锁操作
    public void releaseLock()  {
          //删除临时节点
        try{
            zkClient.delete(curThreadLocal.get(),-1);
            log.info(Thread.currentThread()+"释放了分布式锁");
        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            //线程变量移除避免内存溢出
            curThreadLocal.remove();
            threadLocal.remove();
        }
    }

    public static void main(String[] args) throws InterruptedException {


    }
}
