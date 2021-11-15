package com.clpm.quartz.config;

import com.clpm.quartz.eunm.DbConsts;
import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.sql.Timestamp;

public class HibernateInteptor extends EmptyInterceptor {
    @Override
    public void onDelete(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
        super.onDelete(entity, id, state, propertyNames, types);
    }

    @Override
    public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState, String[] propertyNames, Type[] types) {
        return super.onFlushDirty(entity, id, currentState, previousState, propertyNames, types);
    }
    //Jpa创建时候的拦截器 自动注入部分字段
    @Override
    public boolean onSave(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
        for (int index = 0; index < propertyNames.length; index++) {
            String propertyName = propertyNames[index];
            if(propertyName.equals(DbConsts.CREATE_DATE_PROPERTY)){
                //更新下创建事件
                 state[index]=new Timestamp(System.currentTimeMillis());
            }else if(propertyName.equals(DbConsts.CREATE_BY_PROPERTY)){
                //更新下创建人
                 state[index]="lzq";
            }else if(propertyName.equals(DbConsts.UPDATE_DATE_PROPERTY)){
                //更新下更新时间
                 state[index]= new Timestamp(System.currentTimeMillis());
            }
        }
        return true;
    }
}
