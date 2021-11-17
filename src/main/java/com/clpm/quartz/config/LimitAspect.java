package com.clpm.quartz.config;

import com.google.common.collect.ImmutableList;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
@Slf4j
public class LimitAspect {
    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    RedissonClient redissonClient;
    //设置切点
    @Pointcut("@annotation(com.clpm.quartz.config.Limit)")
    public void pointcut() {
    }


    @Around("pointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
//                //这部分用脚本执行 查看是否过期和删除锁的操作
//                String script = "if redis.call(\"get\",KEYS[1]) == ARGV[1] then\n" +
//                "    return redis.call(\"del\",KEYS[1])\n" +
//                "else\n" +
//                "    return 0\n" +
//                "end";
//        stringRedisTemplate.execute(new DefaultRedisScript<Long>(script, Long.class), Arrays.asList("lock"), string);

        RLock limitLock = redissonClient.getLock("LimitLock");
        //2s后自动放锁
        limitLock.lock(2, TimeUnit.SECONDS);
        try{
            MethodSignature joinPointSignature = (MethodSignature) joinPoint.getSignature();
            Method method = joinPointSignature.getMethod();
            Limit methodAnnotation = method.getAnnotation(Limit.class);
            int count = methodAnnotation.count();
            String key = methodAnnotation.key();
            int period = methodAnnotation.period();
            String prefix = methodAnnotation.prefix();
            String luaScript = buildLuaScript();
            RedisScript<Number> redisScript = new DefaultRedisScript<>(luaScript, Number.class);
            //线程安全不可变的集合
            ImmutableList<String> stringImmutableList = ImmutableList.of(prefix + "_" + key);
            Object execute = redisTemplate.execute(redisScript, stringImmutableList, count, period);

            if(execute!=null && Integer.valueOf(execute.toString())<=count){
                log.info("访问的方法为{},Key值的有限期为{},单位时间内的访问次数为{}",method.getName(),period+"s",execute.toString());
                return joinPoint.proceed();
            }else{
                throw new Exception("访问异常,请稍后再试");
            }
        }catch (Exception e){
            log.error(e.getMessage());
        }finally {
            limitLock.unlock();
        }
           return null;
    }

    /**
     * 限流脚本
     * 当前key值所对应的value值转为number后大于所传入的参数时,则返回c
     * 当前key指加1,判断为1时，即为刚创建的key,expire过期时间为第二个参数
     *
     */
    private String buildLuaScript() {
        return "local c" +
                "\nc = redis.call('get',KEYS[1])" +
                "\nif c and tonumber(c) > tonumber(ARGV[1]) then" +
                "\nreturn c;" +
                "\nend" +
                "\nc = redis.call('incr',KEYS[1])" +
                "\nif tonumber(c) == 1 then" +
                "\nredis.call('expire',KEYS[1],ARGV[2])" +
                "\nend" +
                "\nreturn c;";
    }

}
