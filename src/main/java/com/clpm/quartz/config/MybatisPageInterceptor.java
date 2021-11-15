package com.clpm.quartz.config;

import cn.hutool.core.util.ObjectUtil;
import com.clpm.quartz.pojo.Page;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Stream;

@Component
@Intercepts(@Signature(type = StatementHandler.class, method = "prepare",
        args = {Connection.class, Integer.class})
)
public class MybatisPageInterceptor implements Interceptor {
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();

        BoundSql boundSql = statementHandler.getBoundSql();
        Object parameterObject = boundSql.getParameterObject();

        Page page = null;

        if (parameterObject instanceof Page) {
            page = (Page) parameterObject;

        } else if (parameterObject instanceof Map) {
            page = (Page) ((Map) parameterObject).values()
                    .stream().filter(item -> item instanceof Page).findFirst()
                    .orElse(null);
        }

        if(ObjectUtil.isNotNull(page)){

            Method method = invocation.getMethod();
            MetaObject metaObject = MetaObject.forObject(statementHandler, SystemMetaObject.DEFAULT_OBJECT_FACTORY, SystemMetaObject.DEFAULT_OBJECT_WRAPPER_FACTORY, new DefaultReflectorFactory());
            //先拦截到RoutingStatementHandler，里面有个StatementHandler类型的delegate变量，其实现类是BaseStatementHandler，然后就到BaseStatementHandler的成员变量mappedStatement
            MappedStatement mappedStatement = (MappedStatement) metaObject.getValue("delegate.mappedStatement");

            String mappedStatementId = mappedStatement.getId();
            //类路径
            String mName = mappedStatement.getId().substring(mappedStatement.getId().lastIndexOf(".") + 1, mappedStatement.getId().length());
            //方法名
            String substring = mappedStatementId.substring(0, mappedStatementId.lastIndexOf("."));
            Class<?> forName = Class.forName(substring);
            Method[] allDeclaredMethods = ReflectionUtils.getAllDeclaredMethods(forName);

            Method queryMethod = Stream.of(allDeclaredMethods).filter(
                    item -> item.getName().equals(mName)
            ).findFirst().orElse(null);

            if(queryMethod!=null && queryMethod.isAnnotationPresent(InterceptAnnotation.class)){

                InterceptAnnotation annotation = queryMethod.getAnnotation(InterceptAnnotation.class);

                if(annotation.flag()){
                    String format = String.format("%s limit %s offset %s", boundSql.getSql(), page.getIndex(), page.getSize());
                    SystemMetaObject.forObject(boundSql).setValue("sql",format);
                }
            }
        }
        return invocation.proceed();
    }

    @Override
    public Object plugin(Object o) {
        return Plugin.wrap(o, this);
    }

    @Override
    public void setProperties(Properties properties) {

    }
}
