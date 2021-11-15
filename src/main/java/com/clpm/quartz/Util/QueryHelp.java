package com.clpm.quartz.Util;

import cn.hutool.core.util.ObjectUtil;
import com.clpm.quartz.Jpa.Query;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ReflectionUtils;

import javax.persistence.criteria.*;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Zheng Jie
 * @date 2019-6-4 14:59:48
 */
@Slf4j
@SuppressWarnings({"unchecked","all"})
public class QueryHelp {

    private static SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static <R, Q> Predicate getPredicate(Root<R> root, Q query, CriteriaBuilder criteriaBuilder) throws IllegalAccessException {
        List<Predicate> list = new ArrayList<>();

        Field[] declaredFields = query.getClass().getDeclaredFields();

        for (Field declaredField : declaredFields) {
           //标注注解
            if (declaredField.isAnnotationPresent(Query.class)) {
                //只做简单部分的构造
                Query declaredFieldAnnotation = declaredField.getAnnotation(Query.class);
                String propName = declaredFieldAnnotation.propName();
                String fieldName = declaredField.getName();
                Query.Type type = declaredFieldAnnotation.type();
                boolean accessible = declaredField.isAccessible();
                declaredField.setAccessible(true);
                Object obj = declaredField.get(query);
                switch (type){
                    case EQUAL:
//                        Predicate pre = cb.equal(root.get("username"), "王五");
                        Path<Object> objectPath = root.get(fieldName);
                        Predicate predicate = criteriaBuilder.equal(objectPath, obj.toString());
                        list.add(predicate);
                        break;
                    case GREATER_THAN:

                        break;
                    case LESS_THAN:

                        break;
                    case LESS_THAN_NQ:

                        break;
                    case  IN:

                        break;

                    case LEFT_LIKE:
                        Predicate like = criteriaBuilder.like(root.get(fieldName), "%"+obj.toString()+"%");
                        list.add(like);
                        break;
                    case IS_NULL:

                        break;
                    case BETWEEN:

                        break;
                    default:break;
                }
                declaredField.setAccessible(accessible);
            }
        }
        int size = list.size();
        return criteriaBuilder.and(list.toArray(new Predicate[size]));
    }

    @SuppressWarnings("unchecked")
    private static <T, R> Expression<T> getExpression(String attributeName, Join join, Root<R> root) {
        if (ObjectUtil.isNotEmpty(join)) {
            return join.get(attributeName);
        } else {
            return root.get(attributeName);
        }
    }

    private static boolean isBlank(final CharSequence cs) {
        int strLen;
        if (cs == null || (strLen = cs.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    private static List<Field> getAllFields(Class clazz, List<Field> fields) {
        if (clazz != null) {
            fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
            getAllFields(clazz.getSuperclass(), fields);
        }
        return fields;
    }
}
