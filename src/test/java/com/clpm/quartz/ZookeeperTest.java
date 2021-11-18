package com.clpm.quartz;

import com.clpm.quartz.util.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.*;
import java.util.logging.SimpleFormatter;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
public class ZookeeperTest {

    private static String connectString = "127.0.0.1:2181,127.0.0.1:2182,127.0.0.1:2183";
    private static int sessionTimeout = 2000;
    private ZooKeeper zkClient = null;
    private String parentNode = "/servers";

    @Autowired
    RedisTemplate redisTemplate;

//    @Before
    public void init() throws IOException {
        zkClient = new ZooKeeper(connectString, sessionTimeout, new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                System.out.println(watchedEvent.getType() + "--" + watchedEvent.getPath());
                // 再次启动监听
                try {
                    List<String> children = zkClient.getChildren("/MyDistributedLock", true);
                    for (String child : children) {
                        System.out.println(child);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    @Test
    public void doInit() throws KeeperException, InterruptedException {
//        List<String> children = zkClient.getChildren("/", true);
//        System.out.println(children);
        String nodeCreated = zkClient.create(parentNode, "SpringBootTest".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        System.out.println(nodeCreated);
   }


    @Test
    //客户端节点创建
    public void doCreate() throws KeeperException, InterruptedException {
        // 参数 1：要创建的节点的路径； 参数 2：节点数据 ； 参数 3：节点权限 ；参数 4：节点的类型
//        Stat stat = zkClient.exists("/roots", false);
//        System.out.println(stat == null ? "not exist" : "exist");
//
//        String nodeCreated = zkClient.create("/EPHEMERAL", "Test1".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, EPHEMERAL);

        //创建本类下的节点

        zkClient.create("/MyDistributedLock" + "/seq_", null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        zkClient.create("/MyDistributedLock" + "/seq_", null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        zkClient.create("/MyDistributedLock" + "/seq_", null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        zkClient.create("/MyDistributedLock" + "/seq_", null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);


//        System.out.println(nodeCreated);

//        String nodeCreated1 = zkClient.create("/EPHEMERAL", "Test2".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, EPHEMERAL);

//        List<String> zkClientChildren = zkClient.getChildren("/", true);
//        for (String zkClientChild : zkClientChildren) {
//            System.out.println(zkClientChild);
//        }
    }

    @Test
    public void registerService() throws KeeperException, InterruptedException {
        String hostname="hostname";
        try{
            System.out.println(zkClient);
            String create = zkClient.create(parentNode+"/servers" , hostname.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,
                    CreateMode.EPHEMERAL_SEQUENTIAL);
            System.out.println(hostname + " is online " + create);
            List<String> zkClientChildren = zkClient.getChildren(parentNode, true);
            System.out.println(zkClientChildren.size());
        }catch(Exception e) {
         log.error(e.getMessage());
        }
    }

    @Test
    public void TestRedis(){
        redisTemplate.opsForValue().set("Test","Test");

        System.out.println(redisTemplate.opsForValue().get("Test"));

    }


    @Test
    public void doTestZookeeperLock() throws InterruptedException {



        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(3);
//        MyDistributeLock distributedLock = new MyDistributeLock();
//        ZookeeperDistributedLock distributedLock = new ZookeeperDistributedLock();

        for (int i = 0; i < 5; i++) {
            RedisLock distributedLock = new RedisLock("MyLock",redisTemplate);
            int finalI = i;
            fixedThreadPool.submit(()->{
                distributedLock.Lock();
                try {
                    //休眠2s
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    distributedLock.releaseLock();
                }
            });
        }

        //防止线程结束
        TimeUnit.SECONDS.sleep(40);

    }
    //Redis分布式原理是lua脚本加自旋锁
    //Zookeeper分布式锁是根据节点和线程的挂起和唤醒




    //测试集合
    @Test
    public void TestCollection() throws ExecutionException, InterruptedException {

//        List<String> stringArrayList = new ArrayList<>();
//        stringArrayList.add("Maga");
//
//        stringArrayList.add(1,"六神");
//
//        System.out.println(stringArrayList.size());
//
//        System.out.println(stringArrayList.stream().collect(Collectors.joining(",")));
//
//
//        /**
//         *   复制数组
//         * @param src 源数组
//         * @param srcPos 源数组中的起始位置
//         * @param dest 目标数组
//         * @param destPos 目标数组中的起始位置
//         * @param length 要复制的数组元素的数量
//         */
////        public static native void arraycopy(Object src,  int  srcPos,
////        Object dest, int destPos,
////        int length);
//
//        int[] a = new int[10];
//        a[0] = 0;
//        a[1] = 1;
//        a[2] = 2;
//        a[3] = 3;
//        System.arraycopy(a, 2, a, 4, 3);
//        a[2]=99;
//        for (int i = 0; i < a.length; i++) {
//            System.out.print(a[i] + " ");
//        }

        //HashMap的组成是数组加上链表结构,链表是解决哈希冲突,超过8会转为红绿树,数组初始有16个,扩容时候翻倍
        //Node链表的遍历方法 扩容重新计算索引值,链表也要重新迁移，需要避免扩容操作,扩容操作数组长度翻倍
//        Node的结构为<K,V>为value,next,pre也为Node的结构,pre为Null为首节点,next为null为尾节点这样

//        MyHashMap<String, String> stringStringMyHashMap = new MyHashMap<>();

        ExecutorService newFixedThreadPool = Executors.newFixedThreadPool(3);
        /**
         *  corePoolSize : 核心线程数定义了最小可以同时运行的线程数量。
         * maximumPoolSize : 当队列中存放的任务达到队列容量的时候，当前可以同时运行的线程数量变为最大线程数。
         * workQueue: 当新任务来的时候会先判断当前运行的线程数量是否达到核心线程数，如果达到的话，新任务就会被存放在队列中。
         * ThreadPoolExecutor 饱和策略定义:
         *
         * 如果当前同时运行的线程数量达到最大线程数量并且队列也已经被放满了任务时，ThreadPoolTaskExecutor 定义一些策略:
         *
         *
         */
        ArrayList<Integer> stringArrayList = new ArrayList<>();
        List<CompletableFuture<Integer>> completableFutureList=new Vector<>();
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(4, 10, 1L, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(100),
                new ThreadPoolExecutor.CallerRunsPolicy());

         SimpleDateFormat formater = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");

        long currentTimeMillis = System.currentTimeMillis();
        for (int i = 0; i < 10;i++){
            int finalI = i;
            completableFutureList.add(CompletableFuture.supplyAsync(() -> {
                System.out.println(Thread.currentThread().getName() + " Start. Time = " + formater.format(new Date()));
                try {
                    TimeUnit.SECONDS.sleep(finalI);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(Thread.currentThread().getName() + " End. Time = " + formater.format(new Date()));
                stringArrayList.add(finalI);
                return finalI;
            }, threadPoolExecutor)
            );
        }

        CompletableFuture.allOf(completableFutureList.stream().toArray(CompletableFuture[]::new)).join();

        System.out.println("结束时间为"+(System.currentTimeMillis()-currentTimeMillis));

            //关闭线程池
            threadPoolExecutor.shutdownNow();

        System.out.println(stringArrayList.stream().toArray(Integer[]::new));

        //Todo 实现下项目启动时候的翻译加载

    }
}
