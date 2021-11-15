package com.clpm.quartz.pojo;

import java.io.Serializable;
//分页器
public class Page implements Serializable {

    private int size;
    private int total;
    private int index;

    public Page(int size, int index) {
        this.size = size;
        this.index = index;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
