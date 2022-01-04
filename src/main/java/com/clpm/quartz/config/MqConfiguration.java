package com.clpm.quartz.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @Author 86178
 * @create 2021/12/23 21:57
 */
//避免因为mq宕机导致的消息丢失
@Component
@Slf4j
public class MqConfiguration implements RabbitTemplate.ConfirmCallback,RabbitTemplate.ReturnCallback {


    //注入到RabbitTemplate
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @PostConstruct
    public void initialize(){
        rabbitTemplate.setConfirmCallback(this);
        rabbitTemplate.setReturnCallback(this);
    }
    //生产者到达交换机
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        String id = correlationData != null ? correlationData.getId() : "";
        if (ack){ //成功接收
            log.info("成功接收id为：{}",id);
        }else {
            log.info("接收消息失败id为：{},原因：{}",id,cause);
        }
    }
    //交换机到达队列失败
    @Override
    public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
        log.info("交换机：{}，消息被退回：{},退回原因：{}",
                exchange,message.getBody().toString(),replyText);
    }
}
