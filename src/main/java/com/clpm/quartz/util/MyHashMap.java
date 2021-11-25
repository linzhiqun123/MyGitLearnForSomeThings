package com.clpm.quartz.util;

import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.security.PublicKey;
import java.util.*;
import java.util.stream.Collectors;

//自定义一个HashMap,实现基础的功能
// TODO 待完成
@Slf4j
public class MyHashMap<K,V> implements Cloneable, Serializable {

    private static final long serialVersionUID = 362498820763181265L;

    public static void main(String[] args) {

        MyHashMap<String, String> selfHashMap = new MyHashMap<>();

        selfHashMap.put("Test1","Test1");
        selfHashMap.put("Test2","Test2");
        selfHashMap.put("Test3","Test3");
        selfHashMap.put("Test4","Test4");
        selfHashMap.put("Test5","Test5");
        selfHashMap.put("Test6","Test6");
        selfHashMap.put("Test7","Test7");
        selfHashMap.put("Test8","Test8");

        selfHashMap.remove("Test5");
        selfHashMap.remove("Test8");
        System.out.println(selfHashMap.get("Test3").toString());
        boolean contains = selfHashMap.contains("Test5");
        boolean contains1 = selfHashMap.contains("Test3");
        System.out.println("contains"+contains1+contains);

        Collection<String> strings = selfHashMap.values();

        HashMap<String, String> stringStringHashMap = new HashMap<String, String>();
        //方法2 获取所有的key
        Set<String> stringSet = stringStringHashMap.keySet();
       //方法三 put入一个新的map
      //        stringStringHashMap.putAll();
    }


    //数组的
    private int size;
    //初始长度
    static final int DEFAULT_INITIAL_CAPACITY = 1 << 1; // aka 设为2来测试使用
   //扩容因子
    static final float DEFAULT_LOAD_FACTOR = 0.75f;

    int threshold;

    private Node<K,V>[] table;

    public MyHashMap() {
        size=0;
        threshold = DEFAULT_INITIAL_CAPACITY;
    }
     //定义Hash保存的Node节点
    static class Node<K,V> {
        final int hash;
        final K key;
        V value;
        Node<K,V> next;
        Node<K,V> pre;

        Node(int hash, K key, V value, Node<K,V> next,Node<K,V>pre) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
            this.pre = pre;
        }

        public final K getKey()        { return key; }
        public final V getValue()      { return value; }
        public final String toString() { return key + "=" + value; }

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
        int num = (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
        return  num%DEFAULT_INITIAL_CAPACITY;
    }

    //添加方法
    public void put(String Key,String Value){
        //计算Key值
        int hash = hash(Key);
        //P当现在的Node节点,e为创建的节点
         Node<K,V> p;
         //初始化扩容
        if((table)==null)
            resize();
        //如果计算后的数组上的Node链表为空时候,新建一个Node
        if((p=table[hash])==null){
            table[hash]=new Node(hash, Key, Value, null,null);
            size++;
        }else{
            //判断首节点是否Key相等,是的话直接覆盖
            if(p.hash==hash && (p.key.equals(Key) || p.key==Key)){
                //如果Key存在 则覆盖掉
                Node<K, V> next = p.next;
                table[hash]=new Node(hash, Key, Value, next,null);
            }else if(p.next==null){
                p.next=new Node(hash, Key, Value, null,p);
                table[hash]=p;
            } else {
                //遍历数组查看是否存在Key相等的值 首节点的下一节点不为空时
                for (;;){
                    if(p.hash==hash && (p.key==Key || p.key.equals(Key))){
                        Node<K,V> e=new Node(hash, Key, Value, p.next,p.pre);
                        p.pre.next=e;
                        break;
                    }
                    if(p.next!=null){
                        p= p.next;
                    }else{
                        p.next=new Node(hash,Key,Value,null,p);
                        break;
                    }
                }
                afterNodeInsertion(hash, p);
            }
        }
        //判断是否扩容 先忽略了

    }



    private void afterNodeInsertion(int hash, Node<K, V> p) {
        if(p!=null){
         //遍历出首节点设置到数组中
            do {
                 p=p.pre;
                 if(p.pre==null){
                    table[hash]=p;
                     break;
                 }
            }while (true);
        }
    }

    private void resize() {
        table= (Node<K,V>[])new Node[DEFAULT_INITIAL_CAPACITY];
    }

    //get方法  do--While循环
    public Object get(String Key){
        //先计算下Hash值
        int hash = hash(Key);

        Node<K, V> firNode = table[hash];

        if((firNode=table[hash])!=null){
             //首节点先判断
            if(firNode.hash==hash && (firNode.key==Key || firNode.key.equals(Key) )){
                return firNode.value;
            }
            if(firNode.next!=null){
                do {
                    if(firNode.hash==hash && (firNode.key==Key || firNode.key.equals(Key) ))
                    {
                          return firNode.value;
                    }
                }while ((firNode=firNode.next)!=null);
            }
        }
        return null;
    }


    //删除键
    public void remove(String Key){

        int hash = hash(Key);

        Node<K, V> kvNode,next;
        if((kvNode=table[hash])!=null){

            //判断是否是首节点
            if(kvNode.hash==hash && (kvNode.key==Key || kvNode.getKey().equals(Key))){
                next = kvNode.next;
                if(next!=null){
                    table[hash]=next;
                }
                return;
            }

            if((next=kvNode.next)!=null){
                do {
                    if(next.hash==hash && (next.getKey()==Key || next.getKey().equals(Key))){
                        Node<K, V> node = next.next;
                        if(node!=null){
                            node.pre=kvNode;
                        }
                        kvNode.next=node;
                        table[hash]=kvNode;
                        break;
                    }
                    kvNode=next;
                }while ((next=next.next)!=null);
            }
            afterNodeInsertion(hash,kvNode);
        }
    }

    //判断是否存在的方法
    public boolean contains(String Key){

        int hash = hash(Key);

        Node<K, V> kvNode = table[hash];

        if(kvNode==null)
            return false;

        for(;;){

            if(kvNode.hash==hash &&(kvNode.getKey()==Key || kvNode.getKey().equals(Key))){
                return true;
            }
            kvNode = kvNode.next;
            if(kvNode==null){
                return false;
            }
        }
    }

      public   Collection<String> values(){

          Collection<String> arrayList = new ArrayList<>();

          Node kvNode=null;
          for (int i = 0; i < DEFAULT_INITIAL_CAPACITY; i++) {

              if ((kvNode=table[i])!=null){

                  do{
                      arrayList.add((String) kvNode.value);

                  }while ((kvNode=kvNode.next)!=null);

              }


          }

          return arrayList;
      }

      //获取所有的Key值
      public Set<String> keySet(){

        Node kvNode=null;

          Set<String> str = new HashSet<String>();

          for (int i = 0; i < DEFAULT_INITIAL_CAPACITY; i++) {

              if(kvNode!=null){
                  do {

                     str.add((String) kvNode.getKey());

                  }while ((kvNode=kvNode.next)!=null);
              }
          }
         return str;
      }



}
