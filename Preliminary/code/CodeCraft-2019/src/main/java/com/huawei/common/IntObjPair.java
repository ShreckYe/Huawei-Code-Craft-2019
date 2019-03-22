package com.huawei.common;

public class IntObjPair<B> {
    int first;
    B second;

    public IntObjPair(int first, B second) {
        this.first = first;
        this.second = second;
    }

    public int getFirst() {
        return first;
    }

    public B getSecond() {
        return second;
    }
}
