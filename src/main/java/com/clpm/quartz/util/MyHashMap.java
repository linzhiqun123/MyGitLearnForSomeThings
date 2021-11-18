package com.clpm.quartz.util;

import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

//自定义一个HashMap,实现基础的功能
// TODO 待完成
@Slf4j
public class MyHashMap<K,V> implements Cloneable, Serializable {

    private static final long serialVersionUID = 362498820763181265L;

    //数组的
    private int size;
    //初始长度
    static final int DEFAULT_INITIAL_CAPACITY = 1 << 2; // aka 设为2来测试使用
   //扩容因子
    static final float DEFAULT_LOAD_FACTOR = 0.75f;

    int threshold;

    transient Node<K,V>[] table;

    public MyHashMap() {
        size=0;
        threshold = DEFAULT_INITIAL_CAPACITY;
    }
     //定义Hash保存的Node节点
    static class Node<K,V> implements Map.Entry<K,V> {
        final int hash;
        final K key;
        V value;
        Node<K,V> next;

        Node(int hash, K key, V value, Node<K,V> next) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }

        public final K getKey()        { return key; }
        public final V getValue()      { return value; }
        public final String toString() { return key + "=" + value; }

        public final int hashCode() {
            return Objects.hashCode(key) ^ Objects.hashCode(value);
        }

        public final V setValue(V newValue) {
            V oldValue = value;
            value = newValue;
            return oldValue;
        }

        public final boolean equals(Object o) {
            if (o == this)
                return true;
            if (o instanceof Map.Entry) {
                Map.Entry<?,?> e = (Map.Entry<?,?>)o;
                if (Objects.equals(key, e.getKey()) &&
                        Objects.equals(value, e.getValue()))
                    return true;
            }
            return false;
        }
    }

    //计算Hash值
    static final int hash(Object key) {
        int h;
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
    }

    //添加方法
    public void put(String Key,String Value){
        //计算Key值
        int hash = (hash(Key)%DEFAULT_INITIAL_CAPACITY);
        //P当现在的Node节点,e为创建的节点
         Node<K,V> p;Node<K,V> e;
         //初始化扩容
        if((table)==null)
            resize();
        //如果计算后的数组上的Node链表为空时候,新建一个Node
        if((p=table[hash])==null){
            table[hash]=new Node(hash, Key, Value, null);
            size++;
        }else{
            //判断首节点是否Key相等,是的话直接覆盖
            if(p.hash==hash && (p.key.equals(Key) || p.key==Key)){
                //如果Key存在 则覆盖掉
                Node<K, V> next = p.next;
                table[hash]=new Node(hash, Key, Value, next);
            }else if(p.next==null){
                p.next=new Node(hash, Key, Value, null);
                table[hash]=p;
            } else {
                //遍历数组查看是否存在Key相等的值
                e=p.next;
                for (int j=0;;j++){
                    if(e.hash==hash && (e.key==Key || e.key.equals(Key))){
                        //TODO e前面的节点保存到了p中了
                        Node<K, V> next = e.next;
                        p.next=new Node(hash, Key, Value, next);
                          break;
                    }
                    if(e.next==null){
                       e.next= new Node(hash, Key, Value, null);
                        table[hash]=e;
                       break;
                    }else {
                        p=e;
                        e=e.next;
                    }
                }
            }
        }
        //判断是否扩容 先忽略了
        //打印下是否正确
        log.info("添加后的table数组为{}和{}",table[0],table[1]);

    }

    private void resize() {
        table= (Node<K,V>[])new Node[DEFAULT_INITIAL_CAPACITY];
    }


}
