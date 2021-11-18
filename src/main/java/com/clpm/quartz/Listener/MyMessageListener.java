package com.clpm.quartz.Listener;

import com.google.common.collect.ImmutableList;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class MyMessageListener extends KeyExpirationEventMessageListener {


    @Autowired
    RedisConfigProperties  redisConfigProperties;

    @Autowired
    RedissonClient redissonClient;

    @Autowired
    RedisTemplate redisTemplate;

    public MyMessageListener(RedisMessageListenerContainer listenerContainer) {
        super(listenerContainer);
    }
    //加锁 读和取和设置过期的操作 不是原子操作
    public boolean isABoolean(String body){
        RLock isABoolean = redissonClient.getLock("isABoolean");
        StringBuilder append = new StringBuilder(body).append(":_expire");
        isABoolean.lock();
        try {

            Object andSet = redisTemplate.opsForValue().getAndSet(append.toString(), append.toString());
            if(andSet!=null && andSet.equals(append.toString())){
                return true;
            }

            redisTemplate.expire(append.toString(),1,TimeUnit.DAYS);

        }finally {
            isABoolean.unlock();
        }

        return false;
    }


    private boolean isInvokedSecond(String body){
        String secon="1";
        body="expire_"+body;

        //判断是否已经执行过监听方法 第一次监听时候返回null并设置value值,第二次读取时候将返回值
        //当相等时候返回value 不相等时返回0   比较和返回为原子性操作
        String luaScript = "if redis.call(\"get\",KEYS[1]) == ARGV[1] then\n" +
                "    return 1\n" +
                "else\n" +
                "    return 0\n" +
                "end";

        RLock messageListenerLock = redissonClient.getLock("MessageListenerLock");
        messageListenerLock.lock(1, TimeUnit.SECONDS);
        try{
            RedisScript<String> redisScript = new DefaultRedisScript<String>(luaScript, String.class);
            Object execute = redisTemplate.execute(redisScript, ImmutableList.of(body), body, secon);
            //第一次执行则走下面的方法
            if(execute!=null && execute.toString().equals("0")){
                return true;
            }
        }finally {
                messageListenerLock.unlock();
        }
        return false;
    }


    @Override
    public void onMessage(Message message, byte[] bytes) {
//        RedisSerializer<?> serializer = redisTemplate.getValueSerializer();
//        String channel = String.valueOf(serializer.deserialize(message.getChannel()));
//        String body = String.valueOf(serializer.deserialize(message.getBody()));
        if (!isABoolean(message.toString())) {
            return;
        }
        redisTemplate.opsForValue().increment(new StringBuilder(message.toString()).append("_expire").toString());
        log.info("redis配置文件是否生效{}",redisConfigProperties.getDatabase());
        if(isABoolean(message.toString())){
            return;
        }
        System.out.println("This is master 20211115");

        System.out.println(message);
    }
}
