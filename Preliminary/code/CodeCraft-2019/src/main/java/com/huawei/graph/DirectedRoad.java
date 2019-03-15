package com.huawei.graph;

public class DirectedRoad {
    int id;
    boolean reverseDirection;

    public DirectedRoad(int id, boolean reverseDirection) {
        this.id = id;
        this.reverseDirection = reverseDirection;
    }

    public int getId() {
        return id;
    }

    public boolean isReverseDirection() {
        return reverseDirection;
    }

    @Override
    public String toString() {
        return "DirectedRoad{" +
                "id=" + id +
                ", reverseDirection=" + reverseDirection +
                '}';
    }
}
