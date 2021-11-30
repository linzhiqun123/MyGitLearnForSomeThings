package com.clpm.quartz.util;

import lombok.Data;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

/**
 * @Author 86178
 * @create 2021/11/30 7:15
 */
public class JavaProperties {
    // 包名
    private final String pkg;
    // 类名
    private final String entityName;
    // 属性集合  需要改写 equals hash 保证名字可不重复 类型可重复
    private final Set<Field> fields = new LinkedHashSet<>();
    // 导入类的不重复集合
    private final Set<String> imports = new LinkedHashSet<>();



    public JavaProperties(String entityName, String pkg) {
        this.entityName = entityName;
        this.pkg = pkg;
    }

    public void addField(Class<?> type, String fieldName) {
        // 处理 java.lang
        final String pattern = "java.lang";
        String fieldType = type.getName();
        if (!fieldType.startsWith(pattern)) {
            // 处理导包
            imports.add(fieldType);
        }
        Field field = new Field();
        // 处理成员属性的格式
        int i = fieldType.lastIndexOf(".");
        field.setFieldType(fieldType.substring(i + 1));
        field.setFieldName(fieldName);
        fields.add(field);
    }

    public String getPkg() {
        return pkg;
    }


    public String getEntityName() {
        return entityName;
    }


    public Set<Field> getFields() {
        return fields;
    }

    public Set<String> getImports() {
        return imports;
    }


    /**
     * 成员属性封装对象.
     */
    public static class Field {
        // 成员属性类型
        private String fieldType;
        // 成员属性名称
        private String fieldName;

        public String getFieldType() {
            return fieldType;
        }

        public void setFieldType(String fieldType) {
            this.fieldType = fieldType;
        }

        public String getFieldName() {
            return fieldName;
        }

        public void setFieldName(String fieldName) {
            this.fieldName = fieldName;
        }

        /**
         * 一个类的成员属性 一个名称只能出现一次
         * 我们可以通过覆写equals hash 方法 然后放入Set
         *
         * @param o 另一个成员属性
         * @return 比较结果
         */
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Field field = (Field) o;
            return Objects.equals(fieldName, field.fieldName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(fieldType, fieldName);
        }
    }

}
