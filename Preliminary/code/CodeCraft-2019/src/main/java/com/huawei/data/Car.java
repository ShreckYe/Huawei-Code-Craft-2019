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

    public int getId() {
        return id;
    }

    public int getFrom() {
        return from;
    }

    public int getTo() {
        return to;
    }

    public int getSpeed() {
        return speed;
    }

    public int getPlanTime() {
        return planTime;
    }
}
