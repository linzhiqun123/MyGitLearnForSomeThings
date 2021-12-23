package com.clpm.quartz;

import com.clpm.quartz.config.CodeTable;
import com.clpm.quartz.util.SpringUtils;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
@SpringBootApplication
@MapperScan("com.clpm.quartz.mapper")
public class QuartzApplication {


      //RabbitMq的MessageConverter
    @Bean
    public MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }


    //线程池
    @Bean
    public ThreadPoolExecutor threadPoolExecutor(){
        return new ThreadPoolExecutor(
                5,10,1L, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(10),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }

    public static void main(String[] args) {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(QuartzApplication.class, args);
        CodeTable codeTable = applicationContext.getBean(CodeTable.class);
        Map<String, String> stringStringMap = CodeTable.codeMap;

        ApplicationContext context = SpringUtils.context;
        CodeTable contextBean = context.getBean(CodeTable.class);

        Iterator<Map.Entry<String, String>> iterator = stringStringMap.entrySet().iterator();

        while (iterator.hasNext()){
            Map.Entry<String, String> stringStringEntry = iterator.next();
         log.info("CodeTable初始化完成,Key值为{},Value为{}",stringStringEntry.getKey(),stringStringEntry.getValue());
        }

    }

}
