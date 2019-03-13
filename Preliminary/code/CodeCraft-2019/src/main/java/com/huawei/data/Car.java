package com.huawei.data;

public class Car {
    int id, from, to, speed, planTime;

    public Car(int id, int from, int to, int speed, int planTime) {
        this.id = id;
        this.from = from;
        this.to = to;
        this.speed = speed;
        this.planTime = planTime;
    }

    public Car(int[] tuple) {
        this(tuple[0], tuple[1], tuple[2], tuple[3], tuple[4]);
    }

    @Override
    public String toString() {
        return "Car{" +
                "id=" + id +
                ", from=" + from +
                ", to=" + to +
                ", speed=" + speed +
                ", planTime=" + planTime +
                '}';
    }
}
