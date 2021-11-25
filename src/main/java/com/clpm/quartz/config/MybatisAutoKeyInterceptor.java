package com.clpm.quartz.config;

import com.clpm.quartz.util.SnowIdUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.reflections.ReflectionUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Properties;
import java.util.Set;

@Slf4j
@Component
@Intercepts(@Signature(type = Executor.class, method = "update",
        args = {MappedStatement.class, Object.class})
)
public class MybatisAutoKeyInterceptor implements Interceptor {
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object[] args = invocation.getArgs();

        Method method = invocation.getMethod();
        //MappedStatement
        MappedStatement mappedStatement = (MappedStatement) args[0];
        //实体本类
        Object entity = args[1];

        String StatementType = mappedStatement.getSqlCommandType().name();

        if(StatementType.equals("INSERT")){
            Set<Field> fieldSet = ReflectionUtils.getAllFields(
                    entity.getClass(),
                    file -> (file != null && file.getAnnotation(AutoId.class) != null)
            );

            for (Field field : fieldSet) {
                process(entity,field,field.getAnnotation(AutoId.class));
            }
        }
        return invocation.proceed();
    }

    private void process(Object obj,Field field, AutoId annotation) {
        //设置idKey主键
            if(annotation.value().equals(AutoId.IdType.SNOWFLAKE)){
                try {
                    field.setAccessible(true);
                    String uniqueLongHex = SnowIdUtils.uniqueLongHex();
                    log.info("新设置的雪花算法Key为{}",uniqueLongHex);
                    field.set(obj, uniqueLongHex);
                    field.setAccessible(false);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
    }

    @Override
    public Object plugin(Object o) {
        return Plugin.wrap(o, this);
    }

    @Override
    public void setProperties(Properties properties) {

    }
}
