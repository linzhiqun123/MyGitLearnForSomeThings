package com.clpm.quartz.util;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;

/**
 * @Author 86178
 * @create 2021/11/19 10:00
 */
public class SelfHashMap <K, V>{

    public static void main(String[] args) {

        SelfHashMap<String, String> selfHashMap = new SelfHashMap<>();

        selfHashMap.put("Test1","Test1");
        selfHashMap.put("Test2","Test2");
        selfHashMap.put("Test3","Test3");
        selfHashMap.put("Test4","Test4");
        selfHashMap.put("Test5","Test5");

        HashMap<Object, Object> objectObjectHashMap = new HashMap<>();
        objectObjectHashMap.get("ssad");


    }


    static final float DEFAULT_LOAD_FACTOR = 0.75f;

    //  {key:"zhangsan",value:"张三",hash:"-1432604556",next:null};
    private Node[] nodes;

    @Override
    public String toString() {
        return "MyHashMap{" +
                "nodes=" + Arrays.toString(nodes) +
                '}';
    }

    public SelfHashMap() {
        //System.out.println("新建了MyHashMap");
        Node[] nodes = new Node[2];
        this.nodes = nodes;
    }

    static class Node<K, V> implements Serializable {
        final int hash;
        final K key;
        V value;
        SelfHashMap.Node<K, V> next;


        public K getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }

        public void setValue(V value) {
            this.value = value;
        }

        public int getHash() {
            return hash;
        }

        public Node<K, V> getNext() {
            return next;
        }

        public void setNext(Node<K, V> next) {
            this.next = next;
        }

        Node() {
            hash = 0;
            key = null;
        }

        @Override
        public String toString() {
            return "Node{" +
                    "hash=" + hash +
                    ", key=" + key +
                    ", value=" + value +
                    ", next=" + next +
                    '}';
        }


        Node(K key, V value) {
            this.hash = key.hashCode();
            this.key = key;
            this.value = value;
            this.next = null;
        }
    }

    /**
     * 看看index下是否有数据,看看node中key是否相等,如果相等,就把旧value修改为新value
     *      * 如果不相等,就把把新的node放入数组中,然后把新的node的next修改为旧node的key
     *      *
     * @param key
     * @param value
     */
    public void put(K key, V value) {
        //TODO 在这里对map的nodes的length进行检测
        //如果长度大于
        //put(key,value){
        //hash(key)
        //index=hash&(n-1)
        //}
        int remainder = Math.abs(key.hashCode()) % nodes.length;
        //如果余数为0,就是没有余数,  就==10
        //如果 !=0,就对余数做-1操作 ,对应上余数为1,放在0号索引上
//        System.out.println("     remainder   :    " + remainder);
//        System.out.println("数据放在" + (remainder - 1) + "索引");
        //System.out.println("key:" + key + ",value:" + value);
        Node node = new Node(key, value);
        if (null != nodes && nodes.length > 0) {

            if (remainder != 0) {
                int remainderCurrent = remainder - 1;
                // 看看index下是否有数据,如果没有,直接赋值
                if (null == nodes[remainderCurrent]) {
                    nodes[remainderCurrent] = node;
                }
                //如果有,看看node中key是否相等
                else {
                    Object keyFromOld = nodes[remainderCurrent].getKey();
                    if (keyFromOld.equals(key)) {
                        //如果相等,就把旧value修改为新value
                        Node oldNode = nodes[remainderCurrent];
                        oldNode.setValue(node.getValue());
                        nodes[remainderCurrent] = oldNode;
                    } else {
                        //如果不相等,就把新的node放入数组中,然后把新的node的next修改为旧node的key
                        Node oldNode = nodes[remainderCurrent];
                        node.setNext(oldNode);
                        nodes[remainderCurrent] = node;

                    }
                }
            } else {
                nodes[nodes.length - 1] = node;
            }
        }

    }

    /**
     * 通过输入的key算出hash值,得到余数,这个余数与存储链表的数组的索引值有关系,
     *
     * @param keyFromInput
     * @return
     */
    public V get(K keyFromInput) {
        if (keyFromInput != null) {
            if (null != nodes && nodes.length > 0) {
                int remainder = Math.abs(keyFromInput.hashCode()) % nodes.length;
                //获取当前索引
                System.out.println(keyFromInput + "余数 :    " + remainder);

                if (remainder != 0) {
                    int remainderCurrent = remainder - 1;
                    return getValue(keyFromInput, remainderCurrent);
                } else {
                    //获取数据
                    return getValue(keyFromInput, nodes.length - 1);
                }
            }
        }
        return null;
    }

    //抽取重复的代码
    //通过key和索引获取获取value

    /**
     * 如果输入的 key和存储的key相等,返回value
     * 否则就查看是否存储在next,如果存在next,就取出next,next就是node
     * 查看node的key和输入的key是否相等,相等就返回value
     * 否则就查看是否存储在next,如果存在next,就取出next,next就是node
     *
     * @param keyFromInput
     * @param index
     * @return
     */
    public V getValue(K keyFromInput, int index) {

        //现在的取值逻辑:
        //如果输入的key和存储的key相等,返回value
        Node node = nodes[index];
        return reGetValue(keyFromInput, node);
    }

    /**
     * 递归获取value:如果输入的key和取出的key相同,那么就返回value,如果不相同,比较输入的key和next中的key
     *
     * @param keyFromInput
     * @param node
     * @return
     */
    public V reGetValue(K keyFromInput, Node node) {
        if (null != node) {
            Object value = node.getValue();
            Object fromMap = node.getKey();
            Node next = node.getNext();
            if (null != fromMap) {
                K keyFromMap = (K) fromMap;
                if (keyFromInput.equals(keyFromMap)) {
                    if (null != value) {
                        V valueFromMap = (V) (value);
                        return valueFromMap;
                    }
                } else {
                    return reGetValue(keyFromInput, next);
                }
            }
        }

        return null;
    }

    public V remove(K keyFromInput) {
        if (keyFromInput == null) {
            return null;
        }


        V v = get(keyFromInput);

        int remainder = keyFromInput.hashCode() % nodes.length;
        if (null != nodes && nodes.length > 0) {
            //获取当前索引
            System.out.println(keyFromInput + "余数 :    " + remainder);

            if (remainder != 0) {
                int remainderCurrent = remainder - 1;
                return removeByIndex(keyFromInput, remainderCurrent);
            } else {
                //获取数据
                return removeByIndex(keyFromInput, nodes.length - 1);
            }
        }

        return v;
    }

    public V removeByIndex(K keyFromInput, int index) {

        //现在的取值逻辑:
        //如果输入的key和存储的key相等,返回value
        if (null == nodes[index]) {

            return null;
        } else {
            //索引下有数据
            if (null != nodes[index].getKey()) {
                //如果key相等
                if (nodes[index].getKey().equals(keyFromInput)) {
                    //查看下面是否有next
                    if (null == nodes[index].getNext()) {
                        nodes[index] = null;
                        return (V) (nodes[index].getValue());
                    } else {
                        //直接让数组中的数据修改为要删除数据的next
                        Node next = nodes[index].getNext();
                        V value = (V) nodes[index].getValue();
                        nodes[index] = next;
                        return value;
                    }
                } else {
                    //如果数组中key不匹配,从节点里面找
                    //这个节点是数组节点的next
                    if (null == nodes[index].getNext()) {
                        //14如果没有,返回null,删除失败
                        return null;
                    }
                    // 如果当前链表中有数据
                    else {
                        //20  如果它下边的next中没数据
                        //21  获取出要删除的node
                        Node nodeInArrNext = nodes[index].getNext();
                        //如果key相等
                        if (nodeInArrNext.getKey().equals(keyFromInput)) {
                            //删除数据
                            V nodeInArrNextValue = (V) (nodeInArrNext.getValue());
                            //如果它下边的next中没数据
                            Node underArrNext = nodeInArrNext.getNext();
                            if (null != underArrNext) {
                                nodeInArrNext.setNext(null);
                                // 直接将数组中node的next置空
                                nodes[index] = nodeInArrNext;
                            } else {
                                nodes[index] = null;
                            }
                            return nodeInArrNextValue;
                        }
                        //如果数组下面第一个桶中不是想要删除的数据,就进入第二个桶中,怎么进入?
                        else {
                            return removeNodeInLink(nodeInArrNext, keyFromInput);
                        }
                    }
                }
            }
        }

        return null;
    }

    private V removeNodeInLink(Node nodeInArrNext, K keyFromInput) {
        //nodeInArrNext就是第一个桶

        Node moreThanOneBucket = nodeInArrNext.getNext();
        K moreThanOneBucketKey = (K) (moreThanOneBucket.getKey());
        V moreThanOneBucketValue = (V) (moreThanOneBucket.getValue());
        Node moreThanOneBucketKeyMore = moreThanOneBucket.getNext();

        if (moreThanOneBucketKey.equals(keyFromInput)) {
            //如果相等
            //如果它下边的next中没数据
            if (null == moreThanOneBucketKeyMore) {
                //将它上边的node的next置空
                moreThanOneBucket = null;
                //获取出要删除的node
                nodeInArrNext.setNext(moreThanOneBucket);
                return moreThanOneBucketValue;
            } else {
                //获取出node的next
                moreThanOneBucket = moreThanOneBucketKeyMore;
                //获取出要删除的node
                nodeInArrNext.setNext(moreThanOneBucket);
                return moreThanOneBucketValue;
            }
        }
        //如果它下边的next中有数据
        else {
            //递归
            return removeNodeInLink(moreThanOneBucket, keyFromInput);

        }
    }
}
