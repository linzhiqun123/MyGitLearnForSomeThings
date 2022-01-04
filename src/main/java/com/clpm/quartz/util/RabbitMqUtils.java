package com.clpm.quartz.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitMessageOperations;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;

/**
 * @Author 86178
 * @create 2021/12/22 22:20
 */
@Slf4j
public class RabbitMqUtils {

    private static RabbitTemplate rabbitTemplate=null;

    static {
        if (rabbitTemplate==null) {
            System.out.println("rabbitTemplate初始化");
            rabbitTemplate=(RabbitTemplate)SpringUtils.context.getBean(RabbitTemplate.class);
        }
    }


    public static String SendMqMessage(){
        HashMap<String, Object> stringObjectHashMap = new HashMap<>();
        stringObjectHashMap.put("name","lzq");
        stringObjectHashMap.put("passWord","123");
        String isSuccess="true";
        try{
            log.info("当前系统时间为"+new Date());
            rabbitTemplate.convertAndSend("X_EXCHANGE","XC","测试自定义的演示队列",msg->{
                msg.getMessageProperties().setExpiration("12000");
                return msg;
            });
//            rabbitTemplate.convertAndSend("X_EXCHANGE","XA",stringObjectHashMap);
//            rabbitTemplate.convertAndSend("X_EXCHANGE","XB",stringObjectHashMap);
//            rabbitTemplate.convertAndSend("helloExchange","/",stringObjectHashMap);
        }catch (Exception e) {
            isSuccess = "false";
        }
       return isSuccess;
    }




}
