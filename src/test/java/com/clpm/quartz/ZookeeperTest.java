package com.clpm.quartz;

import com.clpm.quartz.Util.DistributedLock;
import com.clpm.quartz.Util.ZookeeperDistributedLock;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.apache.zookeeper.CreateMode.EPHEMERAL;

@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
public class ZookeeperTest {

    private static String connectString = "127.0.0.1:2181,127.0.0.1:2182,127.0.0.1:2183";
    private static int sessionTimeout = 2000;
    private ZooKeeper zkClient = null;
    private String parentNode = "/servers";

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
    public void doTestZookeeperLock() throws InterruptedException {



        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(3);

        ZookeeperDistributedLock distributedLock = new ZookeeperDistributedLock();

        for (int i = 0; i < 5; i++) {
            fixedThreadPool.submit(()->{
                distributedLock.Lock();
                try {
                    //休眠2s
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    distributedLock.releaseLock();
                }
            });

        }

        //防止线程结束
        TimeUnit.SECONDS.sleep(12);

    }

}
