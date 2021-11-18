package com.clpm.quartz.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
public class RedisLock {

    RedisTemplate redisTemplate;

    private String lockName;

    ThreadLocal<String> threadLocal=new ThreadLocal<String>();

    public String getLockName() {
        return lockName;
    }

    public RedisLock(String lockName,RedisTemplate redisTemplate) {
        this.lockName = lockName;
        this.redisTemplate = redisTemplate;
    }

    public synchronized boolean doExecute(String UUid){
        //原子操作
        Boolean ifAbsent = redisTemplate.opsForValue().setIfAbsent(this.getLockName(), UUid,3, TimeUnit.SECONDS);
        if(ifAbsent){
           return true;
        }

        Long expire = redisTemplate.opsForValue().getOperations().getExpire(this.getLockName());
        try {
            TimeUnit.SECONDS.sleep(expire+1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void Lock(){
        String toString = UUID.randomUUID().toString();
        threadLocal.set(toString);
        //自旋枷锁
        while (true) {
            //获取占坑
             if(this.doExecute(toString)){
                 //占锁成功的话
                 log.info(Thread.currentThread().getName()+"获取锁成功,锁值为{}",toString);
                 break;
             }
        }
    }

    public void releaseLock(){

        Object object = redisTemplate.opsForValue().get(this.getLockName());

        if(object!=null && object.toString().equals(threadLocal.get())){
            log.info(Thread.currentThread().getName()+"释放锁成功,锁值为{}",threadLocal.get());
            redisTemplate.delete(this.getLockName());
        }
    }

}
