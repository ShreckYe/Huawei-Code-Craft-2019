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

    public void setFirst(int first) {
        this.first = first;
    }

    public void setSecond(B second) {
        this.second = second;
    }

    @Override
    public String toString() {
        return "IntObjPair{" +
                "first=" + first +
                ", second=" + second +
                '}';
    }
}
