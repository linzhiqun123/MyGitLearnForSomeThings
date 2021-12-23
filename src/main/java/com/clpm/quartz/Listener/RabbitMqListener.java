package com.clpm.quartz.Listener;

import com.alibaba.fastjson.JSON;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.amqp.core.Message;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Author 86178
 * @create 2021/12/22 22:53
 */
@Slf4j
@Component
public class RabbitMqListener {

    @Value("server.port")
    public String port;

    @RabbitListener(queues = "helloExchange.gulimall-product")
    public void MQListener(Message obj, Channel channel) throws IOException {
        try {
            log.info(obj.toString());
            String encoded = new String(obj.getBody());
            log.info("它的Class类为{},本服务的端口为{}",encoded,port);
            TimeUnit.SECONDS.sleep(30);
            log.info("业务数据处理完成,进行手动签收消息");
            channel.basicAck(obj.getMessageProperties().getDeliveryTag(), false);
        } catch (InterruptedException | IOException e) {
            log.error("接受mq消费出错,进行重新入队处理");
            channel.basicReject(obj.getMessageProperties().getDeliveryTag(),true);
        }
    }
}
