package com.clpm.quartz.util;

import org.springframework.amqp.rabbit.core.RabbitMessageOperations;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;

/**
 * @Author 86178
 * @create 2021/12/22 22:20
 */
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
            rabbitTemplate.convertAndSend("helloExchange","/",stringObjectHashMap);
        }catch (Exception e) {
            isSuccess = "false";
        }
       return isSuccess;
    }




}
