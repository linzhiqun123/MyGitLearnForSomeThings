package com.clpm.quartz.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author 86178
 * @create 2021/12/29 6:58
 */
@Component
public class TtlMqConfig {


    private static final String X_EXCHANGE="X_EXCHANGE";

    private static final String Y_EXCHANGE="Y_EXCHANGE";

    private static final String QUEUE_A="QUEUE_A";

    private static final String QUEUE_B="QUEUE_B";

    private static final String QUEUE_C="QUEUE_C";


    private static final String QUEUE_D="QUEUE_D";


    @Bean("xExchange")
    public DirectExchange xExchange(){
        return new DirectExchange(X_EXCHANGE);
    }

    @Bean("yExchange")
    public DirectExchange yExchange(){
        return new DirectExchange(Y_EXCHANGE);
    }

    @Bean("queueA")
    public Queue queueA(){
        Map<String,Object> arguments = new HashMap<>(3);
        // 绑定该队列到私信交换机
        arguments.put("x-dead-letter-exchange",Y_EXCHANGE);
        arguments.put("x-dead-letter-routing-key","YD");
        arguments.put("x-message-ttl",10000);//10s
        return QueueBuilder.durable(QUEUE_A).withArguments(arguments).build();
    }


    @Bean("queueB")
    public Queue queueB(){
        Map<String,Object> arguments = new HashMap<>(3);
        // 绑定该队列到私信交换机
        arguments.put("x-dead-letter-exchange",Y_EXCHANGE);
        arguments.put("x-dead-letter-routing-key","YD");
        arguments.put("x-message-ttl",40000);//10s
        return QueueBuilder.durable(QUEUE_B).withArguments(arguments).build();
    }


    @Bean("queueC")
    public Queue queueC(){
        Map<String,Object> arguments = new HashMap<>(3);
        // 绑定该队列到私信交换机
        arguments.put("x-dead-letter-exchange",Y_EXCHANGE);
        arguments.put("x-dead-letter-routing-key","YD");
        return QueueBuilder.durable(QUEUE_C).withArguments(arguments).build();
    }

    @Bean("queueD")
    public Queue queueD(){
        return QueueBuilder.durable(QUEUE_D).build();
    }


    @Bean
    public Binding orderBindingA(@Qualifier("queueA")Queue queueA,
                                @Qualifier("xExchange") DirectExchange directExchangeA) {
        return BindingBuilder.bind(queueA)
                .to(directExchangeA)
                .with("XA");
    }

    @Bean
    public Binding orderBindingB(@Qualifier("queueB")Queue queueB,
                                @Qualifier("xExchange") DirectExchange directExchangeB) {
        return BindingBuilder.bind(queueB)
                .to(directExchangeB)
                .with("XB");
    }


    @Bean
    public Binding orderBindingC(@Qualifier("queueC")Queue queueC,
                                 @Qualifier("xExchange") DirectExchange directExchangeC) {
        return BindingBuilder.bind(queueC)
                .to(directExchangeC)
                .with("XC");
    }

    @Bean
    public Binding orderBindingD(@Qualifier("queueD")Queue queueD,
                                 @Qualifier("yExchange") DirectExchange directExchangeD) {
        return BindingBuilder.bind(queueD)
                .to(directExchangeD)
                .with("YD");
    }

}
